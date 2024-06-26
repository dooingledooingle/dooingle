import {useRef, useState} from "react";
import SmallSubmitButton from "./button/SmallSubmitButton.jsx";
import {fetchAddCatch} from "../fetch.js";
import {useReport} from "../hooks/useContext.js";

export default function DooingleAndCatch({
                                           dooingleId,
                                           ownerName,
                                           setDooinglesAndCatches,
                                           dooingleContent,
                                           catchResponse,
                                           isCurrentUserEqualToPageOwner,
                                           setShowDeleteModal,
                                           deleteTargetRelatedDooingleIdRef,
                                           deleteTargetIdRef,
                                           deleteTargetContentRef,
                                         }) {

  const [isCatchFormVisible, setIsCatchFormVisible] = useState(false);
  const {setShowReportModal, setReportTarget} = useReport();
  const catchRef = useRef();

  const catchId = catchResponse.id;
  const catchContent = catchResponse.content;
  const existsCatch = catchResponse.createdAt !== null;
  const isCatchDeleted = catchResponse.deletedAt !== null;

  function handleShowCatchFormButton() {
    setIsCatchFormVisible(true);
  }

  function handleHideCatchFormButton() {
    setIsCatchFormVisible(false);
  }

  function handleCatchSubmit(event) {
    event.preventDefault();

    const catchContent = catchRef.current.value;

    if (catchContent.toString().trim().length > 200 || catchContent.toString().trim().length < 10) {
      alert("뒹글은 10자 이상 200자 이하여야 합니다.");
      return;
    }

    fetchAddCatch(dooingleId, catchContent).then(addedCatch => {
      setDooinglesAndCatches(prevDooinglesAndCatches => {
        /* 새로운 리스트 만들어서 원래 내용 복사 후 그 리스트를 반환하게 해야함 */
        const catchAddedDooinglesAndCatches = [...prevDooinglesAndCatches];
        catchAddedDooinglesAndCatches.filter(dooingleAndCatch => dooingleAndCatch.dooingleId === dooingleId)[0].catch = addedCatch;
        return catchAddedDooinglesAndCatches
      })
    })
    catchRef.current.value = "";
  }

  function handleReportButton(reportedTargetType, reportedTargetId, reportedTargetContent) {
    setReportTarget({
      reportedTargetType: reportedTargetType,
      reportedTargetId: reportedTargetId,
      reportedTargetContent: reportedTargetContent
    })
    setShowReportModal(true)
  }

  function handleDeleteButton() {
    deleteTargetRelatedDooingleIdRef.current = dooingleId;
    deleteTargetIdRef.current = catchResponse.catchId;
    deleteTargetContentRef.current = catchContent;

    setShowDeleteModal(true);
  }

  return (
    <>
      <div className="py-[1rem] ml-[1rem]">
        <div className="flex flex-col px-[0.75rem] gap-[0.375rem]">
          <div className="px-[0.5rem] text-[#456bf5] font-bold  max-w-fit">
            <span>익명의 뒹글러</span>
          </div>
          <div className="flex justify-start items-center gap-[0.5rem]">
            <div
              className="px-[1.25rem] py-[0.625rem] w-fit border-[0.03125rem] border-[#8692ff] rounded-[0.625rem] max-w-[65%]">
              <span className="text-[#5f6368] whitespace-pre-wrap break-words">{dooingleContent}</span>
            </div>
            <button type="button" onClick={() => handleReportButton("DOOINGLE", dooingleId, dooingleContent)}>
              <img src="/report.svg" alt="뒹글 신고 버튼" className="w-[1.375rem]"/>
            </button>
            {(!existsCatch && isCurrentUserEqualToPageOwner) && (!isCatchFormVisible &&
              <div className="flex group">
                <SmallSubmitButton onClick={handleShowCatchFormButton}>받을래요</SmallSubmitButton>
                <img src="/post-button.svg" alt="캐치 버튼" onClick={handleShowCatchFormButton}
                     className="w-[2rem] h-[2rem] -scale-x-100 group-hover:rotate-[-360deg] hover:rotate-[-360deg] transition-transform duration-1000 cursor-pointer"/>
              </div>)}
          </div>

        </div>
        {
          existsCatch ? <div className="flex flex-col items-end px-[0.75rem] gap-[0.375rem] mr-[1.5rem]">
            <div className="px-[0.5rem] text-[#456bf5] font-bold max-w-fit">
              <span>{ownerName}</span>
            </div>
            {
              !isCatchDeleted &&
              <div className="flex justify-end items-center gap-[0.5rem] w-full">
                {isCurrentUserEqualToPageOwner &&
                  <button type="button" onClick={handleDeleteButton}>
                    <img src="/delete-icon.svg" alt="캐치 삭제 버튼" className="w-[1.375rem]"/>
                  </button>
                }
                <button type="button" onClick={() => handleReportButton("CATCH", catchId, catchContent)}>
                  <img src="/report.svg" alt="캐치 신고 버튼" className="w-[1.375rem]"/>
                </button>
                <div
                  className="px-[1.25rem] py-[0.625rem] w-fit border-[0.03125rem] border-[#fa61bd] rounded-[0.625rem] max-w-[65%]">
                  <span className="text-[#5f6368] whitespace-pre-wrap break-words">{catchContent}</span>
                </div>
              </div>
            }
            {
              isCatchDeleted &&
              <div className="py-[0.5rem] w-fit">
                <span className="text-[#8e677f] break-words">삭제된 캐치라서 다시 볼 수 없어요...</span>
              </div>
            }
          </div> : null
        }
        {(catchContent === null && isCurrentUserEqualToPageOwner) && (isCatchFormVisible && (<div>
          <form onSubmit={handleCatchSubmit} className="flex ml-[1rem] items-center">
          <textarea ref={catchRef} placeholder="엎질러진 물처럼 캐치는 수정할 수 없어요."
                    className="w-[50%] m-4 px-[0.5rem] py-[0.25rem] overflow-y-hidden resize-none
                    border-[0.03125rem] border-[#fa61bd] rounded-[0.625rem]
                    focus:outline-none focus:outline-[#fa61bd] focus:outline-[0.0625rem] focus:outline-rounded-[0.5rem]"/>
            <SmallSubmitButton type="submit">이대로 받을래요</SmallSubmitButton>
            <SmallSubmitButton type="button" onClick={handleHideCatchFormButton}>그냥 닫기</SmallSubmitButton>
          </form>
        </div>))}
      </div>
    </>
  );
}
