import {createContext, useEffect, useState} from "react";
import {fetchLoggedInUserLink} from "../fetch.js";

export const AuthContext = createContext()

export default function AuthProvider({children}) {
  /* TODO ChatGPT에게 조언 받은 부분, showLoginModal과 logout, handle401Error 등은 추후 정리 필요함 */
  
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [authenticatedUserLink, setAuthenticatedUserLink] = useState("");
  const [showLoginModal, setShowLoginModal] = useState(false);

  useEffect(() => {
    fetchLoggedInUserLink().then(fetchedLink => {
      setAuthenticatedUserLink(fetchedLink)
    })
    setIsAuthenticated(true)
    setShowLoginModal(false);
  }, []);

  function logout() {
    setIsAuthenticated(false)
    setAuthenticatedUserLink("")
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
