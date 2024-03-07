import { Link } from "react-router-dom";
import Header from "../components/Header.jsx";
import Dooingle from "../components/Dooingle.jsx";
import ProfileImageFrame from "../components/ProfileImageFrame.jsx";
import Navigation from "../components/Navigation.jsx";

export default function FeedPage() {
  return (
    <>
      <Header />

      <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] h-[4.5rem] ml-40px">
        <nav className="col-start-1 col-span-3 flex justify-center text-[#5f6368]">
          <div className="flex flex-col items-center py-[3.75rem] gap-[1.25rem]">
            <ProfileImageFrame />
            <Navigation />
          </div>
        </nav>

        <section className="col-start-4 col-span-6 flex flex-col py-[2.75rem] text-[#5f6368]">
          <div className="flex px-[2rem] gap-[1.75rem] shadow-[inset_0_-0.125rem_0_0_#9aa1aa]">
            <div className="hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]">
              <button className="py-[0.5rem]">
                <div>
                  전체
                </div>
              </button>
            </div>
            <div className="hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]">
              <button className="py-[0.5rem]">
                <div>
                  팔로우
                </div>
              </button>
            </div>
          </div>

          <div className="py-[1rem]">
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
          </div>
        </section>

        <aside
          className="col-start-10 col-span-3 flex flex-col justify-end items-center text-[#5f6368]">
          <div className="sticky bottom-0 py-[4.5rem]">
            <div className="flex flex-col items-center gap-[0.25rem] rounded-br-[0.625rem] border-b-[0.0625rem] border-[#ef7ec2]">
              <div className="flex flex-col gap-[0.5rem] px-[1rem] py-[0.625rem]">
                <div className="font-bold text-[#5f6368] text-[1rem]">
                  <p>새로운 뒹글 페이지</p>
                </div>
                <div className="flex flex-col gap-[0.25rem] px-[0.625rem]">
                  <p>깜이</p>
                  <p>최유민</p>
                  <p>곽준선</p>
                  <p>김다진</p>
                  <p>노하영</p>
                </div>
              </div>
              <div className="flex flex-col gap-[0.5rem] px-[1rem] py-[0.625rem]">
                <div className="font-bold text-[#5f6368] text-[1rem]">
                  <p>뜨거운 뒹글 페이지</p>
                </div>
                <div className="flex flex-col gap-[0.25rem] px-[0.625rem]">
                  <p>깜이</p>
                  <p>최유민</p>
                  <p>곽준선</p>
                  <p>김다진</p>
                  <p>노하영</p>
                </div>
              </div>
            </div>
          </div>
        </aside>

        <div className="col-start-1 col-span-12 mt-10">
          <Link to={"/"}>웰컴 페이지로</Link>
        </div>
      </div>
    </>
  )
}
