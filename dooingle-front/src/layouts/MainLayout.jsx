import ProfileImageFrame from "../components/ProfileImageFrame.jsx";
import Navigation from "../components/Navigation.jsx";
import DooinglerListAside from "../components/DooinglerListAside.jsx";
import {Outlet} from "react-router-dom";
import {useAuth} from "../hooks/useContext.js";

export default function MainLayout() {

  const {isAuthenticated, authenticatedUserLink} = useAuth();

  return (
    <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] h-[4.5rem] ml-40px">
      <nav className="col-start-1 col-span-3 flex justify-center text-[#5f6368]">
        <div className="flex flex-col items-center py-[3.75rem] gap-[1.25rem]">
          {isAuthenticated && <>
            <ProfileImageFrame userLink={authenticatedUserLink}/>
            <Navigation/>
          </>}
        </div>
      </nav>

      <Outlet />

      <DooinglerListAside/>
    </div>
  );
}