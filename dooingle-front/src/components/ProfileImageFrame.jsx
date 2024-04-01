import {useEffect, useState} from "react";
import {fetchUserProfileImageUrl} from "../fetch.js";

export default function ProfileImageFrame({userLink}) {
  const [userProfileImage, setUserProfileImage] = useState()

  useEffect(() => {
    if (!userLink) {
      setUserProfileImage("/no-image-1.png")
    } else {
      fetchUserProfileImageUrl(userLink).then(imageUrl =>
        imageUrl !== null ? setUserProfileImage(imageUrl) : setUserProfileImage("/no-image-1.png"))
    }
  }, [userLink]);

  return (
    <div>
      <img className="border-[0.125rem] rounded-full w-[7.5rem] h-[7.5rem] object-cover"
           src={userProfileImage}
           alt="사용자 프로필 이미지"/>
    </div>
  );
}
