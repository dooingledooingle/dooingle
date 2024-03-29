import Header from "../components/Header.jsx";
import ProfileImageFrame from "../components/ProfileImageFrame.jsx";
import Navigation from "../components/Navigation.jsx";
import DooinglerListAside from "../components/DooinglerListAside.jsx";
import {Link} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import {BACKEND_SERVER_ORIGIN} from "../env.js";
import Dooingle from "../components/Dooingle.jsx";
import Following from "../components/Following.jsx";

async function fetchLoggedInUserLink() { // TODO 중복 함수 - 추후 정리해야 함
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users/current-dooingler`, {
    withCredentials: true, // ajax 요청에서 withCredentials config 추가
  });
  return response.data.userLink;
}

async function fetchFollowingList() { // TODO 중복 함수 - 추후 정리해야 함
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/follow`, {
    withCredentials: true, // ajax 요청에서 withCredentials config 추가
  });
  return response.data;
}

export default function FollowPage() {

  const [currentUserLink, setCurrentUserLink] = useState(undefined);
  const [followingList, setFollowingList] = useState([]);

  useEffect(() => {
    fetchLoggedInUserLink().then(loggedInUserLink => {
      setCurrentUserLink(loggedInUserLink)
    })
    fetchFollowingList().then(list => {
      setFollowingList(list)
    })
  }, []);

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
                <span>팔로우 뒹글러 목록</span>
              </div>
            </div>
          </div>

          <div className="flex flex-col gap-[1.75rem] px-[0.625rem] py-[1.25rem]">
            {followingList.map(following => (
              <Following
                key={following.followingUserName} /*TODO 바꿔야 함*/
                userName={following.followingUserName}
                userLink={following.followingUserLink}
                userProfileImageUrl={following.followingUserProfileImageUrl}
                userDescription={following.followingUserDescription}
              />
            ))}
            <Following
              userName="김관장"
            />
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
