import {Link} from "react-router-dom";

export default function Header() {
  return (
    <header className="shadow-[inset_0_-0.0625rem_0_0_#d3d3d3]">
      <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] h-[4.5rem] ml-40px">

        <div className="col-start-4 col-span-6 flex justify-center items-center text-[#8692ff]">
          <div className="min-w-fit">
            <Link to="/feeds" className="text-[2rem] font-bold">Dooingle</Link>
          </div>
        </div>

        <div className="col-start-10 col-span-3 flex items-center justify-center gap-[1rem] max-h-full pt-[0.5rem]">
          <Link to={"/notices"}>
            <img src="/notice.svg" alt="공지사항 링크" className="h-[2.5rem]"/>
          </Link>
{/*          <Link to={"#"}> TODO 알림 기능 필요함
            <img src="/notification.svg" alt="알림 링크" className="h-[2.5rem]"/>
          </Link>*/}
          <Link to={"/my-profile"}>
            <img src="/profile.svg" alt="프로필 링크" className="h-[2.5rem]"/>
          </Link>
        </div>

      </div>
    </header>
  );
}
