import {useEffect, useState} from "react";
import axios from "axios";
import {BACKEND_SERVER_ORIGIN} from "../env.js";

async function fetchUserProfileImageUrl(userLink) {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users/${userLink}/profile-image`, {
    withCredentials: true,
  });
  return response.data.imageUrl;
}

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
