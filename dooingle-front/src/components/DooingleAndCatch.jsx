import {useRef, useState} from "react";
import SmallSubmitButton from "./button/SmallSubmitButton.jsx";
import {fetchAddCatch} from "../fetch.js";

export default function DooingleAndCatch({ dooingleId, ownerName, setDooinglesAndCatches, dooingleContent, catchContent, isCurrentUserEqualToPageOwner }) {

  const [isCatchFormVisible, setIsCatchFormVisible] = useState(false);
  const catchRef = useRef();

  function handleShowCatchFormButton() {
    setIsCatchFormVisible(true);
  }

  function handleHideCatchFormButton() {
    setIsCatchFormVisible(false);
  }

  function handleCatchSubmit(event) {
    event.preventDefault();

    const catchContent = catchRef.current.value;
    fetchAddCatch(dooingleId, catchContent).then(addedCatch => {
      setDooinglesAndCatches(prevDooinglesAndCatches => {
        /* 새로운 리스트 만들어서 원래 내용 복사 후 그 리스트를 반환하게 해야함 */
        const catchAddedDooinglesAndCatches = [...prevDooinglesAndCatches];
        catchAddedDooinglesAndCatches.filter(dooingleAndCatch => dooingleAndCatch.dooingleId === dooingleId)[0].catch.content = addedCatch.content;
        return catchAddedDooinglesAndCatches
      })
    })
    catchRef.current.value = "";
  }

  return (
    <div className="py-[1rem]">
      <div className="flex flex-col px-[0.75rem] gap-[0.375rem]">
        <div className="px-[0.5rem] text-[#456bf5] font-bold  max-w-fit">
          <span>익명의 뒹글러</span>
        </div>
        <div className="px-[1.25rem] py-[0.625rem] w-[65%] border-[0.03125rem] border-[#8692ff] rounded-[0.625rem] max-w-fit">
          <span className="text-[#5f6368]">{dooingleContent}</span>
        </div>
      </div>
      {catchContent ? <div className="flex flex-col items-end px-[0.75rem] gap-[0.375rem]">
        <div className="px-[0.5rem] text-[#456bf5] font-bold max-w-fit">
          <span>{ownerName}</span>
        </div>
        <div className="px-[1.25rem] py-[0.625rem] w-[65%] border-[0.03125rem] border-[#98a2ff] rounded-[0.625rem] max-w-fit">
          <span className="text-[#5f6368]">{catchContent}</span>
        </div>
      </div> : null}
      {(catchContent === null && isCurrentUserEqualToPageOwner) && (isCatchFormVisible ? (<div>
        <form onSubmit={handleCatchSubmit} className="flex items-center">
          <textarea ref={catchRef} placeholder="엎질러진 물처럼 캐치는 수정할 수 없어요."
                    className="w-[50%] m-4 px-[0.5rem] py-[0.25rem] overflow-y-hidden resize-none
                    border-[0.03125rem] border-[#fa61bd] rounded-[0.625rem]
                    focus:outline-none focus:outline-[#fa61bd] focus:outline-[0.0625rem] focus:outline-rounded-[0.5rem]"/>
          <SmallSubmitButton type="submit">제출</SmallSubmitButton>
          <SmallSubmitButton type="button" onClick={handleHideCatchFormButton}>제출하지 않고 닫기</SmallSubmitButton>
        </form>
      </div>) : (<div> {/* TODO 감싸는 div 필요 없는 경우 하나 없애기 */}
        <div className="flex mt-[1rem] ml-[0.75rem]">
          <SmallSubmitButton onClick={handleShowCatchFormButton}>받을래요</SmallSubmitButton>
          <img src="/post-button.svg" alt="캐치 버튼" className="w-[2rem] h-[2rem] -scale-x-100 peer-hover:rotate-[-360deg] transition-transform duration-1000"/>
        </div>
      </div>))}
    </div>
  );
}
