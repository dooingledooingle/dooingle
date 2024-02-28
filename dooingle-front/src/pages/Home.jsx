import { Link } from "react-router-dom";

export default function HomePage() {
  return (
    <Link to={"/admin"}>관리자 페이지로</Link>
  )
}
