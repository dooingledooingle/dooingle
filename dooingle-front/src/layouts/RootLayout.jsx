import Header from "../components/Header.jsx";
import {Outlet} from "react-router-dom";

export default function RootLayout() {

  return (
    <>
      <Header />
      <Outlet />
    </>
  );
}
