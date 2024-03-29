import Header from "../components/Header.jsx";
import ProfileImageFrame from "../components/ProfileImageFrame.jsx";
import Navigation from "../components/Navigation.jsx";
import DooinglerListAside from "../components/DooinglerListAside.jsx";
import {Link, useSearchParams} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import {BACKEND_SERVER_ORIGIN} from "../env.js";
import NoticeItem from "../components/NoticeItem.jsx";
import Pagination from "../components/Pagination.jsx";

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

async function fetchLoggedInUserLink() { // TODO 중복 함수 - 추후 정리해야 함
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users/current-dooingler`, {
    withCredentials: true,
  });
  return response.data.userLink;
}

async function fetchNoticePage(pageNumber) {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/notices?page=${pageNumber}`, {
    withCredentials: true,
  });
  return response.data;
}

export default function NoticePage() {

  const [currentUserLink, setCurrentUserLink] = useState(undefined);
  const [currentNoticePage, setCurrentNoticePage]= useState(noticePageInitialState);
  const [searchParams, setSearchParams] = useSearchParams();

  const pageNumber = searchParams.get("page") || "1";

  useEffect(() => {
    fetchLoggedInUserLink().then(loggedInUserLink => {
      setCurrentUserLink(loggedInUserLink)
    })
  }, []);

  useEffect(() => {
    fetchNoticePage(pageNumber).then(fetchedPage => {
      setCurrentNoticePage(fetchedPage)
    })
  }, [pageNumber])

  function handlePageChange(pageNumber) {
    setSearchParams(`page=${pageNumber}`);
  }

  return (
    <>
      <Header />

      <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] h-[4.5rem] ml-40px">
        <nav className="col-start-1 col-span-3 flex justify-center text-[#5f6368]">
          <div className="flex flex-col items-center py-[3.75rem] gap-[1.25rem]">
            <ProfileImageFrame userLink={currentUserLink} />
            <Navigation/>
            {/*<div className="flex flex-col items-center pt-10">*/}
            {/*  <div className="text-xl text-red-500">알림 관련 임시</div>*/}
            {/*  <button onClick={handleConnect}>connect 요청</button>*/}
            {/*  <div className="py-[1rem]">*/}
            {/*    {sseNotification.message}*/}
            {/*    {sseNotification.cursor}*/}
            {/*  </div>*/}
            {/*  <button onClick={handleTestConnect}>test connect 요청</button>*/}
            {/*  <button onClick={handleTestClick}>test 요청</button>*/}
            {/*  <div>{testData}</div>*/}
            {/*</div>*/}
          </div>
        </nav>

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

        <DooinglerListAside/>

        <div className="col-start-1 col-span-12 mt-10">
        <Link to={"/"}>웰컴 페이지로</Link>
        </div>
      </div>
    </>
  )
}
