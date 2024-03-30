import {Link} from "react-router-dom";
import {BACKEND_SERVER_ORIGIN} from "../env.js";

export default function WelcomePage() {

  const handleStartButton = () => {
    window.location.href = `${BACKEND_SERVER_ORIGIN}/oauth2/login/kakao`
  }

  return (
    <>
      <div className="w-full h-[54rem] relative]">
        <section
          className="absolute h-full w-full flex flex-col items-center justify-start py-40 gap-[0.625rem] max-w-full text-[#8692ff]">
          <div
            className="w-[60rem] flex flex-col items-center justify-center py-[7.5rem] px-5 max-w-full">
            <h1
              className="relative lg:text-[8rem] md:text-[6rem] sm:text-[3rem] font-bold inline-block">
              Dooingle
            </h1>
          </div>
          <div className="flex flex-col items-center justify-center box-border gap-[0.625rem] h-[5.625rem]">
            <button onClick={handleStartButton} className="lg:w-[80%] md:w-[60%] sm:w-[40%] w-[20%] flex justify-center">
              <img src="/kakao_login_large_narrow.png" alt="카카오 소셜 로그인 버튼" className="max-w-[80%]" />
            </button>
          </div>
        </section>
      </div>
    </>
  )
}
