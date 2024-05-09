import {Link} from "react-router-dom";
import {useAuth} from "../hooks/useContext.js";

export default function Navigation() {

  const {isAuthenticated, authenticatedUserLink} = useAuth()

  return (
    <div className="flex flex-col items-center gap-[1rem]">
      <div>
        <Link to={`/feeds`} className="hover:text-[#ef7ec2]">뒹글 피드</Link>
      </div>
      {isAuthenticated && <>
        <div>
          <Link to={`/personal-dooingles/${authenticatedUserLink}`} className="hover:text-[#ef7ec2]">내 뒹글 페이지</Link>
        </div>
        <div>
          <Link to={`/follows`} className="hover:text-[#ef7ec2]">팔로우하는 뒹글러</Link>
        </div>
      </>}
      <div>
        <Link to={`/exploration`} className="hover:text-[#ef7ec2]">뒹글 페이지 탐색</Link>
      </div>
    </div>
  );
}
