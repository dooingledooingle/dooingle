import Header from "../components/Header.jsx";
import {Outlet} from "react-router-dom";
import LoginInductionModal from "../components/modal/LoginInductionModal.jsx";
import useAxiosInterceptor from "../hooks/useAxiosInterceptor.jsx";
import ReportModal from "../components/modal/ReportModal.jsx";

export default function RootLayout() {
  useAxiosInterceptor();

  return (
    <>
      <LoginInductionModal />
      <ReportModal />
      <Header />
      <Outlet />
    </>
  );
}
