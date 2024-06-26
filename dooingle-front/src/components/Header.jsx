import {Link} from "react-router-dom";
import {useEffect, useRef, useState} from "react";
import {useAuth, useNotification} from "../hooks/useContext.js";
import {fetchNotifications} from "../fetch.js";

export default function Header() {
  const {isAuthenticated} = useAuth();
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [showNotificationDropdown, setShowNotificationDropdown] = useState(false);
  const [notifications, setNotifications] = useState([])
  const userMenuRef = useRef();
  const notificationDropdownRef = useRef();
  const {personalNotification, setPersonalNotification} = useNotification();
  const {setShowLoginInductionModal} = useAuth();

  useEffect(() => {
    function handleOutsideClick(event) {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setShowUserMenu(false);
      }

      if (notificationDropdownRef.current && !notificationDropdownRef.current.contains(event.target)) {
        setShowNotificationDropdown(false);
      }
    }

    function handleEscKey(event) {
      if (event.key === "Escape") {
        setShowUserMenu(false);
        setShowNotificationDropdown(false);
      }
    }

    document.addEventListener("mousedown", handleOutsideClick);
    document.addEventListener("keydown", handleEscKey);

    return () => {
      document.removeEventListener("mousedown", handleOutsideClick);
      document.removeEventListener("keydown", handleEscKey);
    };
  }, [userMenuRef, notificationDropdownRef]);

  function toggleUserMenu() {
    setShowNotificationDropdown(false)
    setShowUserMenu(!showUserMenu);
  }

  function toggleNotificationDropdown() {
    if (personalNotification) {
      setPersonalNotification(null);
    }
    setShowUserMenu(false);

    fetchNotifications().then(fetchedNotificationSlice => {
      setNotifications(fetchedNotificationSlice.content)
    })
    setShowNotificationDropdown(!showNotificationDropdown)
  }

  function handleLoginButton() {
    setShowLoginInductionModal(true);
  }

  return (
    <header className="shadow-[inset_0_-0.0625rem_0_0_#d3d3d3]">
      <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] h-[4.5rem] ml-40px">

        <div className="col-start-4 col-span-6 flex justify-center items-center text-[#8692ff]">
          <div className="min-w-fit">
            <Link to="/feeds" className="text-[2rem] font-bold">
              <img src="/dooingle-outline.svg" alt="헤더 로고, 피드페이지 링크" className="h-[2.5rem]"/>
            </Link>
          </div>
        </div>

        <div className="col-start-10 col-span-3 flex items-end justify-center gap-[1rem] max-h-full pt-[0.5rem]">
          <Link to={"/notices"} className="pb-[0.3125rem]">
            <img src="/notice.svg" alt="공지사항 링크" className="h-[2.5rem]"/>
          </Link>

          {isAuthenticated && <div ref={notificationDropdownRef} className="relative -z-1">
            <button onClick={toggleNotificationDropdown} className="focus:outlink-none">
              {!personalNotification && <img src="/notification-off.svg" alt="알림 드롭다운" className="h-[2.5rem]"/>}
              {personalNotification && <img src="/notification-on.svg" alt="알림 드롭다운" className="h-[2.5rem]"/>}
            </button>
            {showNotificationDropdown && (
              <ul className="absolute z-40 flex flex-col right-0 w-[14.5rem]
              bg-white rounded-b-[0.5rem] border-[#d3d3d3] border-[0.03125rem] border-t-0 shadow-sm pt-[0.25rem] pb-[0.5rem]">
                {notifications.length !== 0 && notifications.map(notification =>
                  <li key={notification.cursor.toString()}
                      className="px-[1.25rem] py-[0.375rem]">
                    <div className="flex justify-between">
                      <span className="text-[0.8125rem] pr-[0.75rem]">
                        {(notification.notificationType === "DOOINGLE") && "새 뒹글이 굴러왔어요!"}
                        {(notification.notificationType === "CATCH") && "뒹글에 캐치가 달렸어요!"}
                      </span>
                      <Link to={`/personal-dooingles/${notification.ownerUserLink}`}
                            className="self-center text-[0.75rem] hover:text-[#ef7ec2]"> 보러 가기</Link>
                    </div>
                  </li>
                )}
                {notifications.length === 0 &&
                  <li className="px-[1.25rem] py-[0.375rem]">
                    <div className="flex justify-center">
                      <span className="text-[0.8125rem] pr-[0.75rem]">지금은 아무 것도 없어요!</span>
                    </div>
                  </li>
                }
              </ul>
            )}
          </div>}

          <div ref={userMenuRef} className="relative -z-1">
            <button onClick={toggleUserMenu} className="focus:outlink-none">
              <img src="/user-menu.svg" alt="사용자 메뉴 드롭다운 메뉴" className="h-[2.5rem]"/>
            </button>
            {showUserMenu && isAuthenticated && (
              <ul className="absolute flex flex-col gap-[0.125rem] right-0 w-[8rem]
              bg-white rounded-b-[0.5rem] border-[#d3d3d3] border-[0.03125rem] border-t-0 shadow-sm pb-[0.25rem]">
                <li className="px-[1.25rem] py-[0.375rem] hover:bg-[#eaecf9]">
                  <Link to={"/my-profile"}>프로필 설정</Link>
                </li>
                <li className="px-[1.25rem] py-[0.375rem] hover:bg-[#eaecf9]">
                  <Link to={"/logout"}>로그아웃</Link>
                </li>
              </ul>
            )}
            {showUserMenu && !isAuthenticated && (
              <ul className="absolute flex flex-col gap-[0.125rem] right-0 w-[8rem]
              bg-white rounded-b-[0.5rem] border-[#d3d3d3] border-[0.03125rem] border-t-0 shadow-sm pb-[0.25rem]">
                <li className="px-[1.25rem] py-[0.375rem] hover:bg-[#eaecf9]">
                  <button onClick={handleLoginButton}>로그인</button>
                </li>
              </ul>
            )}
          </div>
        </div>

      </div>
    </header>
  );
}
