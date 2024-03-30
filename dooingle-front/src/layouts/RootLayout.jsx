import Header from "../components/Header.jsx";
import {Outlet} from "react-router-dom";
import LoginInductionModal from "../components/modal/LoginInductionModal.jsx";
import useAxiosInterceptor from "../hooks/useAxiosInterceptor.jsx";

export default function RootLayout() {
  useAxiosInterceptor();

  return (
    <>
      <LoginInductionModal />
      <Header />
      <Outlet />
    </>
  );
}
