import {Link} from "react-router-dom";

export default function SearchedUser({userName, userLink, userProfileImageUrl, userDescription}) {

  return (
    <div className="flex items-center gap-[1rem] p-[0.75rem] border-[0.03125rem] border-[#8d9dd6] rounded-[0.625rem]">
      <div>
        <Link to={`/personal-dooingles/${userLink}`}>
          <img className="w-[5rem] h-[5rem] border-[0.03125rem]" src={userProfileImageUrl || "/no-image-1.png"}
               alt="팔로우 뒹글러 프로필 이미지"/>
        </Link>
      </div>
      <div>
        <div>
          <Link to={`/personal-dooingles/${userLink}`} className="font-bold text-[1.125rem] text-[#8692ff]">{userName}</Link>
        </div>
        <div>
          <span className="text-[#5f6368]">{userDescription}</span>
        </div>
      </div>
    </div>
  );
}
