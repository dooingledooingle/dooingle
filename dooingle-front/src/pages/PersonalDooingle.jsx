import ProfileImageFrame from "../components/ProfileImageFrame.jsx";
import Navigation from "../components/Navigation.jsx";
import DooingleAndCatch from "../components/DooingleAndCatch.jsx";
import DooinglerListAside from "../components/DooinglerListAside.jsx";
import {useParams, useSearchParams} from "react-router-dom";
import {useEffect, useRef, useState} from "react";
import {FRONTEND_SERVER_ORIGIN} from "../env.js"
import MorePostButton from "../components/button/MorePostButton.jsx";
import SmallSubmitButton from "../components/button/SmallSubmitButton.jsx";
import {
  fetchDooinglesAndCatchesSlice,
  fetchIsFollowingUser,
  fetchAddFollow,
  fetchCancelFollow,
  fetchAddDooingle,
  fetchPageOwnerUserProfile,
  fetchFollowCount
} from "../fetch.js";
import {useAuth} from "../hooks/useContext.js";
import DeleteModal from "../components/modal/DeleteModal.jsx";

export default function PersonalDooinglePage() {

  const {isAuthenticated, authenticatedUserLink} = useAuth()
  const [dooinglesAndCatches, setDooinglesAndCatches] = useState([]);
  const [pageOwnerUserProfile, setPageOwnerUserProfile] = useState({});
  const [isFollowingUser, setIsFollowingUser] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false)
  const [followerCount, setFollowerCount] = useState(false)
  // const [isEntireFeed, setIsEntireFeed] = useState(true) // TODO isEntireFeed state가 정말 필요한지는 더 고민해볼 것
  const params = useParams();
  const [searchParams] = useSearchParams();
  const pageOwnerUserLink = params?.userLink;
  const dooingleRef = useRef();
  const hasNextSlice = useRef(false);
  const deleteTargetRelatedDooingleIdRef = useRef();
  const deleteTargetContentRef = useRef();
  const deleteTargetIdRef = useRef();
  const isCurrentUserEqualToPageOwner = (authenticatedUserLink === pageOwnerUserLink);

  useEffect(() => {
    if (searchParams) {
      fetchDooinglesAndCatchesSlice(pageOwnerUserLink, searchParams.get("lastDooingleId")).then(newDooinglesAndCatchesSlice => {
        setDooinglesAndCatches(newDooinglesAndCatchesSlice.content)
        hasNextSlice.current = !newDooinglesAndCatchesSlice.last
      });
    } else {
      fetchDooinglesAndCatchesSlice(pageOwnerUserLink).then(newDooinglesAndCatchesSlice => {
        setDooinglesAndCatches(newDooinglesAndCatchesSlice.content)
        hasNextSlice.current = !newDooinglesAndCatchesSlice.last
      });
    }
  }, [pageOwnerUserLink, searchParams]);
  
  useEffect(() => {
    isAuthenticated && fetchIsFollowingUser(pageOwnerUserLink).then(result => {
      setIsFollowingUser(result)
    })

    fetchPageOwnerUserProfile(pageOwnerUserLink).then(result => {
      setPageOwnerUserProfile(result)
    })

    fetchFollowCount(pageOwnerUserLink).then(result => {
      setFollowerCount(result)
    })
  }, [pageOwnerUserLink]);

  function handleAddFollowButton() {
    fetchAddFollow(pageOwnerUserLink).then(() => setIsFollowingUser(true))

    setFollowerCount(prevCount => prevCount + 1)
  }

  function handleCancelFollowButton() {
    fetchCancelFollow(pageOwnerUserLink).then(() => setIsFollowingUser(false))

    setFollowerCount(prevCount => prevCount - 1)
  }

  function handleDooingleSubmit(event) {
    event.preventDefault(); // 폼 제출 기본 동작 방지

    const dooingleContent = dooingleRef.current.value;

    if (dooingleContent.toString().trim().length > 200 || dooingleContent.toString().trim().length < 10) {
      alert("뒹글은 10자 이상 200자 이하여야 합니다.");
      return;
    }

    fetchAddDooingle(pageOwnerUserLink, dooingleContent).then(addedDooingle => {
      /* DooingleResponse와 DooingleAndCatchResponse의 차이 때문에 억지로 catch, catch 내의 content 필드를 넣어줌 */
      addedDooingle = {...addedDooingle, catch: {content: null}}
      dooingleRef.current.value = ""

      setDooinglesAndCatches((prevDooinglesAndCatches => {
        return [addedDooingle, ...prevDooinglesAndCatches];
      }))
    })
  }

  function handleMoreDooingleAndCatchButton() {
    const lastDooingleId = dooinglesAndCatches.slice(-1)[0]?.["dooingleId"]

    /* TODO 답변이 없는 나눠야 함 */
    fetchDooinglesAndCatchesSlice(pageOwnerUserLink, lastDooingleId).then(newDooinglesAndCatchesSlice => {
      setDooinglesAndCatches(prevDooinglesAndCatches => {
        const uniqueNewDooinglesAndCatches = newDooinglesAndCatchesSlice.content.filter(newDooingleAndCatche => prevDooinglesAndCatches.every(prevDooingleAndCatch => prevDooingleAndCatch?.dooingleId !== newDooingleAndCatche?.dooingleId))
        return [...prevDooinglesAndCatches, ...uniqueNewDooinglesAndCatches]
      })
      hasNextSlice.current = !newDooinglesAndCatchesSlice.last
    })
  }

  function handleCopyUserLinkButton() {
    navigator.clipboard.writeText(`${FRONTEND_SERVER_ORIGIN}/personal-dooingles/${pageOwnerUserLink}`)
      .then(() => {
        alert("페이지 링크를 복사했어요.")
      })
      .catch(reason => {
        alert("링크 복사에 실패했어요.")
        console.log("링크 복사 실패", reason)
      })
  }

  return (
    <>
      {showDeleteModal && <DeleteModal setShowDeleteModal= {setShowDeleteModal}
                                       setDooinglesAndCatches = {setDooinglesAndCatches}
                                       deleteTargetRelatedDooingleIdRef = {deleteTargetRelatedDooingleIdRef}
                                       deleteTargetIdRef = {deleteTargetIdRef}
                                       deleteContentRef = {deleteTargetContentRef}/>}

      {/* 소개 섹션 반투명 */}
      <section className="h-[10rem] bg-[#AAAAAA] shadow-[0_0.25rem__0.25rem_#888888]">
        <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] min-h-full opacity-100">
          <div className="col-start-4 col-span-6 flex justify-start items-center gap-[5%]">
            <ProfileImageFrame userLink={pageOwnerUserLink} />
            <div className="flex flex-col gap-[0.375rem]">
              <div className="flex items-center gap-[0.75rem]">
                <span className="text-[1.5rem] font-bold text-white">{pageOwnerUserProfile.nickname}</span>
                <div className="flex gap-[0.5rem] items-center">
                  {!isAuthenticated && <div className="text-[1.5rem] font-extrabold text-[#8692ff]">★</div>}
                  {isAuthenticated && isCurrentUserEqualToPageOwner && <div className="text-[1.5rem] font-extrabold text-[#8692ff]">★</div>}
                  {isAuthenticated && !isCurrentUserEqualToPageOwner && isFollowingUser && <button onClick={handleCancelFollowButton} className="text-[1.5rem] font-extrabold text-[#8692ff]">★</button>}
                  {isAuthenticated && !isCurrentUserEqualToPageOwner && !isFollowingUser && <button onClick={handleAddFollowButton} className="text-[1.5rem] font-extrabold text-[#FFFFFF] hover:text-[#8692ff] transition-colors">☆</button>}
                  <span className="mt-[0.125rem] text-[1.125rem] text-white">{followerCount}</span>
                </div>
              </div>
              <div><span className="text-[1rem] ">{pageOwnerUserProfile.description}</span></div>
              <button onClick={handleCopyUserLinkButton} className="flex items-center">
                <div className="w-[1.25rem] h-[1.25rem] mr-[0.25rem]"><img src="/copy-button-image.svg" alt="뒹글러 페이지 링크 복사 버튼" /></div>
                <span className="text-[0.875rem] font-light">페이지 링크 복사</span>
              </button>
            </div>
          </div>
        </div>
      </section>

      <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] h-[4.5rem]">
        {/* Feed와 배치 다른 부분: nav의 py가 3.75rem -> 3rem, 본문 섹션 py가 2.75rem -> 0.75rem */}

        {/* nav */}
        <nav className="col-start-1 col-span-3 flex justify-center text-[#5f6368]">
          <div className="flex flex-col items-center py-[3rem] gap-[1.25rem]">
            <Navigation/>
          </div>
        </nav>

        {/* 뒹글 & 캐치 */}
        <section className="col-start-4 col-span-6 flex flex-col py-[0.75rem] text-[#5f6368]">
          <div className="flex px-[2rem] gap-[1.75rem] shadow-[inset_0_-0.125rem_0_0_#9aa1aa]">
            <div className="hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]">
              <button className="py-[0.5rem]">
                <div>
                  전체
                </div>
              </button>
            </div>
            {/* TODO 아직 답변이 없는 뒹글 기능 추가해야 함 */}
            {/*<div className="hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]">*/}
            {/*  <button className="py-[0.5rem]">*/}
            {/*    <div>*/}
            {/*      아직 답변이 없는 뒹글*/}
            {/*    </div>*/}
            {/*  </button>*/}
            {/*</div>*/}
          </div>

          {isCurrentUserEqualToPageOwner ||
            <form
            className="flex justify-center items-center mt-[2rem] mb-[1rem] gap-[3%]"
            onSubmit={handleDooingleSubmit}>
            <textarea ref={dooingleRef} placeholder="뒹글은 당신의 얼굴입니다."
                      className="w-[70%] p-[1rem] overflow-y-hidden resize-none
                    border-[0.03125rem] border-[#fa61bd] rounded-[0.625rem]
                    focus:outline-none focus:outline-[#fa61bd] focus:outline-[0.0625rem] focus:outline-rounded-[0.5rem]"/>
            <div className="flex group">
              <img src="/post-button.svg" alt="캐치 버튼" onClick={handleDooingleSubmit}
                   className="w-[2rem] h-[2rem] group-hover:rotate-[360deg] hover:rotate-[360deg] transition-transform duration-1000 cursor-pointer"/>
              <SmallSubmitButton type="submit">굴릴래요</SmallSubmitButton>
            </div>
          </form>}
          <div className="pb-[1rem]">
            {dooinglesAndCatches?.map(dooingleAndCatch => (
              <DooingleAndCatch
                key={dooingleAndCatch.dooingleId}
                dooingleId={dooingleAndCatch.dooingleId}
                ownerName={dooingleAndCatch.ownerName}
                setDooinglesAndCatches={setDooinglesAndCatches}
                dooingleContent={dooingleAndCatch.content}
                catchResponse={dooingleAndCatch.catch}
                isCurrentUserEqualToPageOwner={isCurrentUserEqualToPageOwner}
                setShowDeleteModal = {setShowDeleteModal}
                deleteTargetRelatedDooingleIdRef = {deleteTargetRelatedDooingleIdRef}
                deleteTargetIdRef = {deleteTargetIdRef}
                deleteTargetContentRef= {deleteTargetContentRef}
              />
            ))}
          </div>
          <div className="flex justify-center">
            {hasNextSlice.current && <MorePostButton onClick={() => handleMoreDooingleAndCatchButton()} />}
          </div>
        </section>

        {/* aside */}
        <DooinglerListAside/>
      </div>
    </>
  );
}
