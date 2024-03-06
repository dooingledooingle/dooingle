import { Link } from "react-router-dom";
import Header from "../components/Header.jsx";

export default function FeedPage() {
  return (
    <>
      <Header />

      <Link to={"/"}>웰컴 페이지로</Link>
    </>
  )
}
