import {useEffect} from "react";
import Spinner from "../components/miscellaneous/Spinner.jsx";
import {useAuth} from "../hooks/useContext.js";
import {useNavigate} from "react-router-dom";

export default function LoginPage() {
  
  const {login} = useAuth()
  const navigate = useNavigate();

  useEffect(() => {
    login();
    navigate("/feeds")
  }, [login]);

  return (
    <div className="w-full h-screen flex flex-col justify-center items-center">
      <Spinner/>
      <p className="mt-[2rem] text-[1.5rem] font-semibold">로그인 처리 중...</p>
    </div>
  );
}
