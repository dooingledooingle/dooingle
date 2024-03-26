import {Link} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import {BACKEND_SERVER_ORIGIN} from "../env.js";

async function fetchCurrentUser() {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users/current-dooingler`, {
    withCredentials: true,
  });
  return response.data;
}

export default function Navigation() {
  const [currentUserLink, setCurrentUserLink] = useState()

  useEffect(() => {
    fetchCurrentUser().then(currentUser => setCurrentUserLink(currentUser?.userLink))
  }, []);

  return (
    <div className="flex flex-col items-center gap-[1rem]">
      <div>
        <Link to={`/my-profile`}>내 프로필</Link>
      </div>
      <div>
        <Link to={`/personal-dooingles/${currentUserLink}`}>내 뒹글함</Link>
      </div>
      <div>
        <a href="#">팔로우하는 뒹글러</a>
      </div>
      <div>
        <a href="#">뒹글 탐색</a>
      </div>
    </div>
  );
}
