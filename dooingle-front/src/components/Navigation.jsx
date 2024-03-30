import {Link} from "react-router-dom";
import {useAuth} from "../hooks/useContext.js";

export default function Navigation() {

  const {authenticatedUserLink} = useAuth()

  return (
    <div className="flex flex-col items-center gap-[1rem]">
      <div>
        <Link to={`/personal-dooingles/${authenticatedUserLink}`}>내 뒹글함</Link>
      </div>
      <div>
        <Link to={`/follows`}>팔로우하는 뒹글러</Link>
      </div>
{/*      <div> TODO 뒹글 탐색 기능 필요함
        <a href="#">뒹글 탐색</a>
      </div>*/}
    </div>
  );
}
