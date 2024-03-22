import {useEffect, useState} from "react";
import axios from "axios";
import {BACKEND_SERVER_ORIGIN} from "../env.js";

async function fetchUserProfile() {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users/profile`, {
    withCredentials: true,
  });
  return response.data?.imageUrl;
}

export default function ProfileImageFrame() {
  const [currentUserImageUrl, setCurrentUserImageUrl] = useState()

  useEffect(() => {
    fetchUserProfile().then(imageUrl =>
      imageUrl ? setCurrentUserImageUrl(imageUrl) : setCurrentUserImageUrl("no-image.png"))
  }, []);

  return (
    <div>
      <img className="border-[0.125rem] rounded-full w-[7.5rem] h-[7.5rem] object-cover"
           src={currentUserImageUrl}
           alt="사용자 프로필 이미지"/>
    </div>
  );
}
