import {BACKEND_SERVER_ORIGIN, FRONTEND_SERVER_ORIGIN} from "../../env.js";
import {useAuth} from "../../hooks/useContext.js";
import {useNavigate} from "react-router-dom";

export default function LoginInductionModal() {
  const {isAuthenticated, showLoginInductionModal, setShowLoginInductionModal} = useAuth();
  const navigate = useNavigate();

  const handleStartButton = () => {
    window.location.href = `${BACKEND_SERVER_ORIGIN}/oauth2/login/kakao`
  }

  const handleToPreviousPageButton = () => {
    setShowLoginInductionModal(false);
    navigate(-1);
  }

  return (
    <>
      {
        showLoginInductionModal &&
        <div className="fixed flex items-center inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50" id="my-modal">
          <div className="relative -inset-y-[4rem] mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3 text-center">
              {localStorage.getItem("isAuthenticated") === "true" && <>
                <h3 className="text-lg leading-6 font-medium text-gray-900">뒹글을 로그인한지 오래 지났네요!</h3>
                <div className="mt-2 px-7 py-3">
                  <p className="text-sm text-gray-500">다시 로그인해주세요!</p>
                </div>
              </>}
              {localStorage.getItem("isAuthenticated") !== "true" && <>
                <h3 className="text-lg leading-6 font-medium text-gray-900">뒹글에 로그인해볼까요?</h3>
              </>}
              <div className="flex flex-col items-center justify-center box-border gap-[0.625rem] h-[5.625rem]">
                <button onClick={handleStartButton}
                        className="lg:w-[80%] md:w-[60%] sm:w-[40%] w-[20%] flex justify-center">
                  <img src="/kakao_login_large_narrow.png" alt="카카오 소셜 로그인 버튼" className="max-w-[80%]"/>
                </button>
              </div>
              <button onClick={handleToPreviousPageButton} className="items-center">
                <p className="text-sm text-gray-500 text-center">이전 페이지로 돌아가기</p>
              </button>
            </div>
          </div>
        </div>
      }
    </>
  );
}
