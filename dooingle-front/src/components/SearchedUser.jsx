import {Link} from "react-router-dom";
import SmallSubmitButton from "./button/SmallSubmitButton.jsx";

export default function SearchedUser({userName, userLink, userProfileImageUrl, userDescription}) {

  return (
    <div className="flex justify-between items-center">
      <div className="flex gap-[1rem] items-center">
        <div>
          <img className="w-[5rem] h-[5rem] border-[0.03125rem]" src={userProfileImageUrl || "/no-image-1.png"}
               alt="팔로우 뒹글러 프로필 이미지"/>
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
    </div>
  );
}
