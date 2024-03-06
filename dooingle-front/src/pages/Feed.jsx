import { Link } from "react-router-dom";
import Header from "../components/Header.jsx";

export default function FeedPage() {
  return (
    <>
      <Header />

      <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] h-[4.5rem] ml-40px">
        <nav className="col-start-1 col-span-3 flex justify-center text-[#5f6368] bg-amber-50">
          Hello Nav
        </nav>

        <section className="col-start-4 col-span-6 flex justify-center text-[#5f6368] bg-amber-100">
          <div>주 콘텐츠 영역</div>
          {/* 주 콘텐츠 영역 */}
        </section>

        <aside className="col-start-10 col-span-3 flex justify-center text-[#5f6368] bg-amber-200">
          <div>뜨거운, 새 뒹글러 목록 표시</div>
          {/* 뜨거운, 새 뒹글러 목록 표시 */}
        </aside>
      </div>

      <Link to={"/"}>웰컴 페이지로</Link>
    </>
  )
}
