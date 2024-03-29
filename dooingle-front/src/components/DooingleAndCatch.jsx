import {useRef, useState} from "react";
import axios from "axios";
import {BACKEND_SERVER_ORIGIN} from "../env.js";

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
        <div className="px-[0.5rem] text-[#456bf5] font-bold">
          <p>익명의 뒹글러</p>
        </div>
        <div className="px-[1.25rem] py-[0.625rem] w-[65%] border-[0.03125rem] border-[#8692ff] rounded-[0.625rem]">
          <div className="text-[#5f6368]">
            <p>{dooingleContent}</p>
          </div>
        </div>
      </div>
      {catchContent ? <div className="flex flex-col items-end px-[0.75rem] gap-[0.375rem]">
        <div className="px-[0.5rem] text-[#456bf5] font-bold">
          <p>{ownerName}</p>
        </div>
        <div className="px-[1.25rem] py-[0.625rem] w-[65%] border-[0.03125rem] border-[#8692ff] rounded-[0.625rem]">
          <div className="text-[#5f6368]">
            <p>{catchContent}</p>
          </div>
        </div>
      </div> : null}
      {(catchContent === null && isCurrentUserEqualToPageOwner) && (isCatchFormVisible ? (<div>
        <form onSubmit={handleCatchSubmit}>
          <input ref={catchRef} type="text" placeholder="엎질러진 물처럼 캐치는 수정할 수 없어요."/>
          <button type="submit">제출</button>
          <button type="button" onClick={handleHideCatchFormButton}>제출하지 않고 닫기</button>
        </form>
      </div>) : (<button onClick={handleShowCatchFormButton}>받아라~</button>))}
    </div>
  );
}
