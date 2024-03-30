import Dooingle from "../components/Dooingle.jsx";
import {useEffect, useRef, useState} from "react";
import MorePostButton from "../components/button/MorePostButton.jsx";
import {fetchDooinglesFeedSlice, fetchDooinglesFeedSliceOfFollowing} from "../fetch.js"
import {useNotification} from "../contexts/useContext.js";

export default function FeedPage() {

  const [dooingles, setDooingles] = useState([]);
  const [isEntireFeed, setIsEntireFeed] = useState(true) // TODO isEntireFeed state가 정말 필요한지는 더 고민해볼 것
  const hasNextSlice = useRef(false);
  const {newFeedNotification, setNewFeedNotification} = useNotification()

  useEffect(() => {
    fetchDooinglesFeedSlice().then(newDooinglesSlice => {
      setDooingles(newDooinglesSlice.content)
      hasNextSlice.current = !newDooinglesSlice.last
    });
  }, []);

  function handleMoreFeedButton(isEntireFeed) {
    const lastDooingleId = dooingles.slice(-1)[0]?.["dooingleId"]

    if (isEntireFeed === true) {
      fetchDooinglesFeedSlice(lastDooingleId).then(newDooinglesSlice => {
        setDooingles(prevDooingles => {
          const uniqueNewDooingles = newDooinglesSlice.content.filter(newDooingle => prevDooingles.every(prevDooingle => prevDooingle?.dooingleId !== newDooingle?.dooingleId))
          return [...prevDooingles, ...uniqueNewDooingles]
        })
        hasNextSlice.current = !newDooinglesSlice.last
      })
    } else {
      fetchDooinglesFeedSliceOfFollowing(lastDooingleId).then(newDooinglesSlice => {
        setDooingles(prevDooingles => {
          const uniqueNewDooingles = newDooinglesSlice.content.filter(newDooingle => prevDooingles.every(prevDooingle => prevDooingle?.dooingleId !== newDooingle?.dooingleId))
          return [...prevDooingles, ...uniqueNewDooingles]
        })
        hasNextSlice.current = !newDooinglesSlice.last
      })
    }
  }

  function handleEntireFeedButton() {
    fetchDooinglesFeedSlice().then(newDooinglesSlice => {
      setDooingles(newDooinglesSlice.content)
      hasNextSlice.current = !newDooinglesSlice.last
    });
    setIsEntireFeed(true);
  }

  function handleFollowingFeedButton() {
    fetchDooinglesFeedSliceOfFollowing().then(newDooinglesSlice => {
      setDooingles(newDooinglesSlice.content)
      hasNextSlice.current = !newDooinglesSlice.last
    });
    setIsEntireFeed(false);
  }

  function handleNewFeedNotificationButton() {
    setNewFeedNotification(null);
    setIsEntireFeed(true);

    /* TODO 일단 직전 dooingles의 크기와 상관 없이 새로운 slice 가져와서 교체함 */
    fetchDooinglesFeedSlice().then(newDooinglesSlice => {
      setDooingles(newDooinglesSlice.content)
      hasNextSlice.current = !newDooinglesSlice.last
    });

    window.scrollTo({
      top: 0,
      behavior: "instant",
    });
  }

/*  function handlePersonalNotificationButton() {
    setPersonalNotification(null);
  }*/

  return (
    <section className="col-start-4 col-span-6 flex flex-col py-[2.75rem] text-[#5f6368]">
      <div className="flex px-[2rem] gap-[1.75rem] shadow-[inset_0_-0.125rem_0_0_#9aa1aa]">
        <div className="hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]">
          <button onClick={handleEntireFeedButton} className="py-[0.5rem]">
            <div>
              전체
            </div>
          </button>
        </div>
        <div className="hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]">
          <button onClick={handleFollowingFeedButton} className="py-[0.5rem]">
            <div>
              팔로우
            </div>
          </button>
        </div>
      </div>

      {newFeedNotification && (
        <button type="button" onClick={handleNewFeedNotificationButton}
                className="fixed top-[4.5rem] self-center max-w-fit mt-[0.75rem] mr-[0.5rem] px-[0.5rem] py-[0.25rem]
                  rounded-[0.625rem] text-[0.75rem] text-white font-bold bg-[#fa61bd]
                  border-[0.0625rem] border-[#fa61bd] animate-pulse">
          새 피드가 있어요!
        </button>
      )}
      {/*          TODO 개인 알림 관련 별도 작업 필요
          {personalNotification && (
            <div className="border-2">
              <button onClick={handlePersonalNotificationButton}>새로 뒹글을 받았거나 내가 쓴 뒹글에 캐치가 있어요!</button>
            </div>
          )}*/}

      <div className="pt-[1rem]">
        {dooingles.map(dooingle => (
          <Dooingle
            key={dooingle.dooingleId}
            ownerName={dooingle.ownerName}
            ownerUserLink={dooingle.ownerUserLink}
            dooingleId={dooingle.dooingleId}
            content={dooingle.content}
            hasCatch={dooingle.hasCatch}
          />
        ))}
      </div>
      <div className="flex justify-center mt-[1rem]">
        {hasNextSlice.current && <MorePostButton onClick={() => handleMoreFeedButton(isEntireFeed)}/>}
      </div>
    </section>
  );
}
