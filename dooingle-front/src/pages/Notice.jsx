import {useSearchParams} from "react-router-dom";
import {useEffect, useState} from "react";
import NoticeItem from "../components/NoticeItem.jsx";
import Pagination from "../components/Pagination.jsx";
import {fetchNoticePage} from "../fetch.js";

const noticePageInitialState = {
  totalElements: 0,
  totalPages: 0,
  size: 0,
  content: [],
  number: 0,
  sort: {},
  first: true,
  last: true,
  numberOfElements: 0,
  pageable: {},
  empty: true
}

export default function NoticePage() {

  const [currentNoticePage, setCurrentNoticePage]= useState(noticePageInitialState);
  const [searchParams, setSearchParams] = useSearchParams();

  const pageNumber = searchParams.get("page") || "1";

  useEffect(() => {
    fetchNoticePage(pageNumber).then(fetchedPage => {
      setCurrentNoticePage(fetchedPage)
    })
  }, [pageNumber])

  function handlePageChange(pageNumber) {
    setSearchParams(`page=${pageNumber}`);
  }

  return (
    <section className="col-start-4 col-span-6 flex flex-col py-[2.75rem] text-[#5f6368]">
      <div className="flex px-[2rem] gap-[1.75rem] shadow-[inset_0_-0.125rem_0_0_#9aa1aa]">
        <div className="hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]">
          <div className="py-[0.5rem]">
            <span>공지사항</span>
          </div>
        </div>
      </div>

      <div className="flex flex-col gap-[1.5rem] px-[0.625rem] py-[1.25rem] text-[#5f6368]">
        {currentNoticePage.content.map(notice => (
          <NoticeItem
            key={notice.id}
            id={notice.id}
            title={notice.title}
            createdAt={notice.createdAt}
          />
        ))}
      </div>

      {/* callback, memo 같은 것을 사용해주면 괜찮을 듯 */}
      <div className="py-[0.75rem] border-t-[0.03125rem] border-t-[#9aa1aa]">
        <Pagination currentPage={currentNoticePage} currentPageNumber={pageNumber} onPageChange={handlePageChange}/>
      </div>
    </section>
  );
}
