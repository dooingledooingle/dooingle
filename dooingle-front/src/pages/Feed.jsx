import Dooingle from "../components/Dooingle.jsx";
import {useEffect, useRef, useState} from "react";
import MorePostButton from "../components/button/MorePostButton.jsx";
import {fetchDooinglesFeedSlice, fetchDooinglesFeedSliceOfFollowing} from "../fetch.js"
import {useAuth, useNotification} from "../hooks/useContext.js";

export default function FeedPage() {

  const [dooingles, setDooingles] = useState([]);
  const [showingEntireFeed, setShowingEntireFeed] = useState(true) // TODO isEntireFeed state가 정말 필요한지는 더 고민해볼 것
  const hasNextSlice = useRef(false);
  const {isAuthenticated} = useAuth();
  const {newFeedNotification, setNewFeedNotification} = useNotification();
  const {personalNotification, setPersonalNotification} = useNotification();

  useEffect(() => {
    fetchDooinglesFeedSlice().then(newDooinglesSlice => {
      setDooingles(newDooinglesSlice.content)
      hasNextSlice.current = !newDooinglesSlice.last
    });
  }, []);

  useEffect(() => {
    setNewFeedNotification(null)
  }, [setNewFeedNotification]);

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
    setShowingEntireFeed(true);
  }

  function handleFollowingFeedButton() {
    fetchDooinglesFeedSliceOfFollowing().then(newDooinglesSlice => {
      setDooingles(newDooinglesSlice.content)
      hasNextSlice.current = !newDooinglesSlice.last
    });
    setShowingEntireFeed(false);
  }

  function handleNewFeedNotificationButton() {
    setNewFeedNotification(null);
    setShowingEntireFeed(true);

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

  function handlePersonalNotificationButton() {
    setPersonalNotification(null);
  }

  return (
    <section className="col-start-4 col-span-6 flex flex-col py-[2.75rem] text-[#5f6368]">
      <div className="flex px-[2rem] gap-[1.75rem] shadow-[inset_0_-0.125rem_0_0_#9aa1aa]">
        <div
          className={"px-[0.5rem] " + (showingEntireFeed ? "shadow-[inset_0_-0.125rem_0_0_#fa61bd]" : "hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]")}>
          <button onClick={handleEntireFeedButton} className="py-[0.5rem]">
            <div className={"" + (showingEntireFeed ? "text-[#fa61bd]" : "hover:text-[#fa61bd]")}>
              전체
            </div>
          </button>
        </div>
        {isAuthenticated && <div
          className={"px-[0.5rem] " + (showingEntireFeed ? "hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]" : "shadow-[inset_0_-0.125rem_0_0_#fa61bd]")}>
          <button onClick={handleFollowingFeedButton} className="py-[0.5rem]">
            <div className={"" + (showingEntireFeed ? "hover:text-[#fa61bd]" : "text-[#fa61bd]")}>
              팔로우
            </div>
          </button>
        </div>}
      </div>

      {newFeedNotification && (
        <button type="button" onClick={handleNewFeedNotificationButton}
                className="fixed top-[3.8rem] self-center max-w-fit mt-[0.75rem] mr-[0.5rem] px-[0.5rem] py-[0.25rem]
                  rounded-[0.625rem] text-[0.75rem] text-white font-bold bg-[#fa61bd]
                  border-[0.0625rem] border-[#fa61bd] animate-pulse">
        새 피드가 있어요!
        </button>
      )}
      {personalNotification && (
        <button type="button" onClick={handlePersonalNotificationButton}
                className="fixed top-[6rem] self-center max-w-fit mt-[0.75rem] mr-[0.5rem] px-[0.5rem] py-[0.25rem]
                  rounded-[0.625rem] text-[0.75rem] text-white font-bold bg-[#fa61bd]
                  border-[0.0625rem] border-[#fa61bd] animate-pulse">
          {(personalNotification.notificationType === "DOOINGLE") && "새 뒹글이 굴러왔어요!"}
          {(personalNotification.notificationType === "CATCH") && "내 뒹글에 캐치가 달렸어요!"}
        </button>
      )}

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
        {hasNextSlice.current && <MorePostButton onClick={() => handleMoreFeedButton(showingEntireFeed)}/>}
      </div>
    </section>
  );
}
