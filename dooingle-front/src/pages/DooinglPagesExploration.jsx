import {useEffect, useRef, useState} from "react";
import {fetchRandomDooinglers, fetchSearchDooinglers} from "../fetch.js"
import SearchedUser from "../components/SearchedUser.jsx";

export default function DooinglePagesExplorationPage() {

  const [dooinglers, setDooinglers] = useState([]);
  const [showingRandomDooinglers, setShowingRandomDooinglers] = useState(true);
  const searchKeywordRef = useRef();

  useEffect(() => {
    fetchRandomDooinglers().then(list => {
      setDooinglers(list);
    });
  }, []);

  function handleSearchButton() {
    const searchKeyword = searchKeywordRef.current.value;

    fetchSearchDooinglers(searchKeyword).then(dooinglers => {
      setShowingRandomDooinglers(false);
      setDooinglers(dooinglers);
    })
  }

  function handleKeyDown(event) {
    if (event.key === "Enter") {
      handleSearchButton();
    }
  }

  function handleRecommendRandomDooinglePage() {
    fetchRandomDooinglers().then(list => {
      setShowingRandomDooinglers(true);
      setDooinglers(list);
      searchKeywordRef.current.value = "";
    });
  }

  return (
    <section className="col-start-4 col-span-6 flex flex-col gap-[1.125rem] py-[2.75rem] text-[#5f6368]">
      <div className="self-center w-[80%]">
        <div className="flex justify-between w-full h-[3rem] border-[0.0625rem] border-[#ef7ec2] rounded-[0.625rem]">
          <input type="text" placeholder="검색할 닉네임을 입력해주세요." ref={searchKeywordRef} onKeyDown={handleKeyDown} className="mx-[0.5rem] w-[90%] focus:outline-none" />
          <button onClick={handleSearchButton} className="self-center w-[10%] min-w-fit h-[90%]">
            <img src="/search-icon.svg" alt="검색 아이콘" className="min-w-[2rem] h-full"/>
          </button>
        </div>
      </div>

      {showingRandomDooinglers && <div className="self-center mt-[0.875rem] font-medium text-[0.85rem]">아래 랜덤 추천 뒹글 페이지는 어때요?</div>}
      {!showingRandomDooinglers && <div className="self-center mt-[0.875rem] font-medium text-[0.85rem]">{`"${searchKeywordRef.current.value}" 검색 결과는 아래와 같아요!`}</div>}
      <div className="flex flex-col gap-[1.75rem] px-[0.625rem]">
        {dooinglers.map(dooingler => (
          <SearchedUser
            key={dooingler.userLink}
            userName={dooingler.nickname}
            userLink={dooingler.userLink}
            userProfileImageUrl={dooingler.imageUrl}
            userDescription={dooingler.description}
          />
        ))}
      </div>
      <button onClick={handleRecommendRandomDooinglePage} className="self-center text-[0.85rem] text-[#8692ff] hover:text-[#ef7ec2]">
        {showingRandomDooinglers && <span>다른 랜덤 뒹글 페이지를 추천해주세요!</span>}
        {!showingRandomDooinglers && <span>랜덤 뒹글 페이지를 추천해주세요!</span>}
      </button>
    </section>
  );
}
