import {useRef, useState} from "react";
import axios from "axios";
import {BACKEND_SERVER_ORIGIN} from "../env.js";
import PostSubmitButton from "./button/PostSubmitButton.jsx";

async function fetchAddCatch(dooingleId, catchContent) {
  const addCatchRequestBody = {
    content: catchContent
  }

  const response = await axios.post(
    `${BACKEND_SERVER_ORIGIN}/api/dooingles/${dooingleId}/catches`,
    addCatchRequestBody,
    {
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    },
  );
  return response.data;
}

export default function DooingleAndCatch({ dooingleId, ownerName, dooingleContent, catchContent, isCurrentUserEqualToPageOwner }) {

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

    fetchAddCatch(dooingleId, catchContent);
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
        <form onSubmit={handleCatchSubmit}>
          <input ref={catchRef} type="text" placeholder="엎질러진 물처럼 캐치는 수정할 수 없어요."
                 className="focus:outline-none focus:outline-amber-300 focus:outline-[0.0625rem] m-4"/>
          <button type="submit">제출</button>
          <button type="button" onClick={handleHideCatchFormButton}>제출하지 않고 닫기</button>
        </form>
      </div>) : (<div>
        <div className="flex mt-[1rem] ml-[0.75rem]">
          <PostSubmitButton onClick={handleShowCatchFormButton} className="peer mr-[0.5rem] px-[0.5rem] py-[0.25rem] border-[0.03125rem] border-[#fa61bd] rounded-[0.625rem] hover:bg-[#fa61bd] hover:text-white transition-colors duration-1000">받을래요</PostSubmitButton>
          <img src="/post-button.svg" alt="캐치 버튼" className="w-[2rem] h-[2rem] -scale-x-100 peer-hover:rotate-[-360deg] transition-transform duration-1000"/>
        </div>
      </div>))}
    </div>
  );
}
