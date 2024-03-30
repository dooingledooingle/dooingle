import {Link} from "react-router-dom";
import SmallSubmitButton from "./button/SmallSubmitButton.jsx";
import {useState} from "react";
import {fetchAddFollow, fetchCancelFollow} from "../fetch.js";

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
          <span className="text-[0.875rem] text-[#5f6368]">{userDescription}</span>
        </div>
      </div>
      {isFollowingUser && <SmallSubmitButton type="button" onClick={handleCancelFollowButton} className="max-h-[2rem] font-normal">팔로우 취소</SmallSubmitButton>}
      {!isFollowingUser && <SmallSubmitButton type="button" onClick={handleAddFollowButton} className="max-h-[2rem] font-normal">팔로우</SmallSubmitButton>}
    </div>
  );
}
