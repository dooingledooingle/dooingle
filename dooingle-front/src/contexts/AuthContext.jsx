import {createContext, useEffect, useState} from "react";
import {fetchLoggedInUserLink} from "../fetch.js";
import {useNavigate} from "react-router-dom";

export const AuthContext = createContext()

export default function AuthProvider({children}) {
  /* TODO ChatGPT에게 조언 받은 부분, showLoginModal과 logout, handle401Error 등은 추후 정리 필요함 */
  
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [authenticatedUserLink, setAuthenticatedUserLink] = useState("");
  const [showLoginModal, setShowLoginModal] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchLoggedInUserLink().then(fetchedLink => {
      setAuthenticatedUserLink(fetchedLink)
    })
    setIsAuthenticated(true)
    setShowLoginModal(false);
  }, []);

  /*
    // TODO HttpOnly 쿠키를 사용하므로 이런 방식 불가능, 서버에 요청해야 함
    // 쿠키 삭제 함수
    const deleteCookie = (name) => {
      document.cookie = name + '=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    };
  */

  function logout() {
    try {
      // 서버 요청 로직 필요
      console.log("로그아웃 중")

      // TODO 서버 요청 로직 개발 전 임시로 화면 보여주기 위해 넣은 setTimeout
      setTimeout(() => {
        navigate("/", {replace: true})
      }, 1000);

      // TODO fetch 후 then에 넣을 것
      setIsAuthenticated(false)
      setAuthenticatedUserLink("")

      // 로그아웃 처리가 완료되면 로그인 페이지로 리디렉트
      // navigate('/login', { replace: true });
    } catch (error) {
      console.error("Logout failed: ", error);
      // 에러 처리 로직 추가
    }
  }

  function handle401Error(){
    setShowLoginModal(true);
  }

  return (
    <AuthContext.Provider value={{isAuthenticated, authenticatedUserLink, showLoginModal, logout, handle401Error}}>
      {children}
    </AuthContext.Provider>
  );
}
