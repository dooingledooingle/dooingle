import {BACKEND_SERVER_ORIGIN} from "../env.js";
import {Link} from "react-router-dom";

export default function WelcomePage() {

  const handleStartButton = () => {
    window.location.href = `${BACKEND_SERVER_ORIGIN}/oauth2/login/kakao`
  }

  return (
    <section
      className="w-full h-screen flex flex-col items-center justify-center gap-[6rem] lg:gap-[4rem] md:gap-[2rem] sm:gap-[1.5rem] max-w-full text-[#8692ff]">
      <div
        className="flex flex-col items-center justify-center w-full">
        <img src="/dooingle-outline.svg" alt="뒹글 로고" className="w-[40%] lg:w-[30%] md:w-[20%] sm:w-[20%]"/>
      </div>
      <div className="flex flex-col items-center justify-center box-border">
        <button onClick={handleStartButton} className="flex justify-center">
          <img src="/kakao_login_large_narrow.png" alt="카카오 소셜 로그인 버튼" className="lg:w-[60%] md:w-[30%] sm:w-[30%] w-[20%] "/>
        </button>
      </div>
      <Link to="/feeds">그냥 구경할래요</Link>
    </section>
  )
}
