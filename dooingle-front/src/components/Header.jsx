import {Link} from "react-router-dom";
import {useEffect, useRef, useState} from "react";

export default function Header() {
  const [showMenu, setShowMenu] = useState(false);
  const userMenuRef = useRef();

  useEffect(() => {
    function handleOutsideClick(event) {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setShowMenu(false);
      }
    }

    function handleEscKey(event) {
      if (event.key === "Escape") {
        setShowMenu(false);
      }
    }

    document.addEventListener("mousedown", handleOutsideClick);
    document.addEventListener("keydown", handleEscKey);

    return () => {
      document.removeEventListener("mousedown", handleOutsideClick);
      document.removeEventListener("keydown", handleEscKey);
    };
  }, [userMenuRef]);

  function toggleUserMenu() {
    setShowMenu(!showMenu);
  }

  return (
    <header className="shadow-[inset_0_-0.0625rem_0_0_#d3d3d3]">
      <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] h-[4.5rem] ml-40px">

        <div className="col-start-4 col-span-6 flex justify-center items-center text-[#8692ff]">
          <div className="min-w-fit">
            <Link to="/feeds" className="text-[2rem] font-bold">Dooingle</Link>
          </div>
        </div>

        <div className="col-start-10 col-span-3 flex items-end justify-center gap-[1rem] max-h-full pt-[0.5rem]">
          <Link to={"/notices"} className="pb-[0.3125rem]">
            <img src="/notice.svg" alt="공지사항 링크" className="h-[2.5rem]"/>
          </Link>
{/*          <Link to={"#"}> TODO 알림 기능 필요함
            <img src="/notification.svg" alt="알림 링크" className="h-[2.5rem]"/>
          </Link>*/}
          <div ref={userMenuRef} className="relative">
            <button onClick={toggleUserMenu} className="focus:outlink-none">
              <img src="/user-menu.svg" alt="사용자 메뉴 드롭다운 메뉴" className="h-[2.5rem]"/>
            </button>
            {showMenu && (
              <ul className="absolute flex flex-col gap-[0.125rem] right-0 w-[8rem]
              bg-white rounded-b-[0.5rem] border-[#d3d3d3] border-[0.03125rem] border-t-0 shadow-sm pb-[0.25rem]">
                <li className="px-[1.25rem] py-[0.375rem] hover:bg-[#eaecf9]">
                  <Link to={"/my-profile"}>프로필 설정</Link>
                </li>
                <li className="px-[1.25rem] py-[0.375rem] hover:bg-[#eaecf9]">
                  <Link to={"#"}>로그아웃</Link>
                </li>
              </ul>
            )}
          </div>
        </div>

      </div>
    </header>
  );
}
