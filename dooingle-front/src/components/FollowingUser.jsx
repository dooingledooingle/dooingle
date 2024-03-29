import {Link} from "react-router-dom";
import SmallSubmitButton from "./button/SmallSubmitButton.jsx";
import {useState} from "react";
import axios from "axios";
import {BACKEND_SERVER_ORIGIN} from "../env.js";

async function fetchAddFollow(userLink) {
  const response = await axios.post(
    `${BACKEND_SERVER_ORIGIN}/api/follow/${userLink}`,
    null,
    {withCredentials: true},
  );
  return response.data;
}

async function fetchCancelFollow(userLink) { // TODO Feed에도 있는 함수, 추후 반드시 정리 필요
  // TODO 팔로우 취소 후 목록 화면 어떻게 보여줄지 고민 (1) 목록에서 빼버린다. (2) 팔로우 취소 버튼 대신 팔로우 버튼을 보여준다.
  const response = await axios.delete(`${BACKEND_SERVER_ORIGIN}/api/follow/${userLink}`, {
    withCredentials: true,
  });
  return response.data;
}

export default function FollowingUser({userName, userLink, userProfileImageUrl, userDescription}) {

  const [isFollowingUser, setIsFollowingUser] = useState(true)

  function handleAddFollowButton() {
    fetchAddFollow(userLink).then(() => setIsFollowingUser(true))
  }

  function handleCancelFollowButton() {
    fetchCancelFollow(userLink).then(() => setIsFollowingUser(false))
  }

  return (
    <div className="flex justify-between items-center">
      <div className="flex gap-[1rem] items-center">
        <div>
          <img className="w-[3.75rem] h-[3.75rem] border-[0.03125rem]" src={userProfileImageUrl || "/no-image-1.png"}
               alt="팔로우 뒹글러 프로필 이미지"/>
        </div>
        <div className="flex items-center">
          <span className="font-bold text-[#8692ff]"></span>
          <Link to={`/personal-dooingles/${userLink}`} className="font-bold text-[#8692ff]">{userName}</Link>
        </div>
        <div className="flex items-center max-w-[50%]">
          <span className="text-[#5f6368]">{userDescription}</span>
        </div>
      </div>
      {isFollowingUser && <SmallSubmitButton type="button" onClick={handleCancelFollowButton} className="max-h-[2rem]">팔로우 취소</SmallSubmitButton>}
      {!isFollowingUser && <SmallSubmitButton type="button" onClick={handleAddFollowButton} className="max-h-[2rem]">팔로우</SmallSubmitButton>}
    </div>
  );
}
