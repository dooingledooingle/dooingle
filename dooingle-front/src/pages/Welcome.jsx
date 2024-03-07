import {Link, useNavigate} from "react-router-dom";

export default function WelcomePage() {
  // 페이지 이동 임시 작성
  const navigate = useNavigate()
  const navigateToPurchase = () => navigate("/feeds")

  return (
    <>
      <div className="w-full h-[54rem] relative]">
        <section
          className="absolute h-full w-full flex flex-col items-center justify-start py-40 gap-[0.625rem] max-w-full text-[#8692ff]">
          <div
            className="w-[60rem] flex flex-col items-center justify-center py-[7.5rem] px-5 max-w-full">
            <h1
              className="relative text-[8rem] font-bold inline-block">
              Dooingle
            </h1>
          </div>
          <div
            className="w-[14rem] flex flex-col items-center justify-center box-border gap-[0.625rem]">
            <button
              className="cursor-pointer p-3 bg-[#8692ff] self-stretch rounded-xl flex flex-col items-center justify-center hover:bg-blue-600"
              onClick={navigateToPurchase}
            >
              <div
                className="relative text-[1.375rem] font-medium text-white inline-block">
                로그인
              </div>
            </button>
            <button
              className="cursor-pointer p-3 bg-[#8692ff] self-stretch rounded-xl flex flex-col items-center justify-center hover:bg-blue-600">
              <div
                className="relative text-[1.375rem] font-medium text-white inline-block">
                회원가입
              </div>
            </button>
          </div>
        </section>
      </div>

      <Link to={"/admin"}>관리자 페이지로</Link>
    </>
  )
}
