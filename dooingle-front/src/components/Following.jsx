import {Link} from "react-router-dom";

export default function Following({userName, userLink, userProfileImageUrl, userDescription}) {
  return (
    <div className="flex gap-[1.25rem]">
      <div>
        <img className="w-[3.75rem] h-[3.75rem] border-[0.03125rem]" src={userProfileImageUrl} alt="팔로우 뒹글러 프로필 이미지" />
      </div>
      <div className="flex items-center">
        <span className="font-bold text-[#8692ff]"></span>
        <Link to={`/personal-dooingles/${userLink}`} className="font-bold text-[#8692ff]">{userName}</Link>
      </div>
      <div className="flex items-center">
        <span className="text-[#5f6368]">{userDescription}</span>
      </div>
    </div>
  );
}
