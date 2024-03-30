import {useEffect, useState} from "react";
import FollowingUser from "../components/FollowingUser.jsx";
import {fetchFollowingList} from "../fetch.js";

export default function FollowPage() {

  const [followingList, setFollowingList] = useState([]);

  useEffect(() => {
    fetchFollowingList().then(list => {
      setFollowingList(list)
    })
  }, []);

  return (
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
          <FollowingUser
            key={following.followingUserName} /*TODO 바꿔야 함*/
            userName={following.followingUserName}
            userLink={following.followingUserLink}
            userProfileImageUrl={following.followingUserProfileImageUrl}
            userDescription={following.followingUserDescription}
          />
        ))}
      </div>
    </section>
  );
}
