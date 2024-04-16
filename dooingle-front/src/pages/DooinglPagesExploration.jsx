import {useEffect, useRef, useState} from "react";
import {fetchRandomDooinglers, fetchSearchDooinglers} from "../fetch.js"
import SearchedUser from "../components/SearchedUser.jsx";

export default function DooinglePagesExplorationPage() {

  const [dooinglers, setDooinglers] = useState([]);
  const searchKeywordRef = useRef();

  useEffect(() => {
    fetchRandomDooinglers().then(list => {
      setDooinglers(list)
    });
  }, []);

  function handleSearchButton() {
    const searchKeyword = searchKeywordRef.current.value;

    fetchSearchDooinglers(searchKeyword).then(dooinglers => {
      setDooinglers(dooinglers);
    })
  }

  function handleKeyDown(event) {
    if (event.key === "Enter") {
      handleSearchButton();
    }
  }

  return (
    <section className="col-start-4 col-span-6 flex flex-col gap-[1.75rem] py-[2.75rem] text-[#5f6368]">
      <div className="self-center w-[80%]">
        <div className="flex justify-between w-full h-[3rem] border-[0.125rem] border-[#ef7ec2] rounded-[0.625rem]">
          <input type="text" placeholder="검색할 닉네임을 입력해주세요." ref={searchKeywordRef} onKeyDown={handleKeyDown} className="mx-[0.5rem] w-[90%] focus:outline-none" />
          <button onClick={handleSearchButton} className="self-center w-[10%] min-w-fit h-[90%]">
            <img src="/search-icon.svg" alt="검색 아이콘" className="min-w-[2rem] h-full"/>
          </button>
        </div>
      </div>

      <div className="flex flex-col gap-[1.75rem] px-[0.625rem] py-[1.25rem]">
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
    </section>
  );
}
