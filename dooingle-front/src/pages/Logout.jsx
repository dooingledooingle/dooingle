import {useEffect} from "react";
import Spinner from "../components/miscellaneous/Spinner.jsx";
import {useAuth} from "../hooks/useContext.js";
import {useNavigate} from "react-router-dom";

export default function LogoutPage() {
  
  const {logout} = useAuth()
  const navigate = useNavigate();

  useEffect(() => {
    logout();
    navigate("/", {replace: true});
  }, [logout]);

  return (
    <div className="w-full h-screen flex flex-col justify-center items-center">
      <Spinner/>
      <p className="mt-[2rem] text-[1.5rem] font-semibold">로그아웃 처리 중...</p>
    </div>
  );
}
