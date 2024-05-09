import {createContext, useEffect, useState} from "react";
import {fetchLoggedInUserLink, fetchLogout} from "../fetch.js";

export const AuthContext = createContext()

export default function AuthProvider({children}) {
  
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [authenticatedUserLink, setAuthenticatedUserLink] = useState("");
  const [showLoginInductionModal, setShowLoginInductionModal] = useState(false);

  useEffect(() => {
    const savedIsAuthenticated = localStorage.getItem("isAuthenticated")

    if (savedIsAuthenticated && savedIsAuthenticated === "true") {
      setIsAuthenticated(true)

      fetchLoggedInUserLink().then(fetchedLink => {
        setAuthenticatedUserLink(fetchedLink)
      })
    }
  }, []);

  function login() {
    setIsAuthenticated(true);
    localStorage.setItem("isAuthenticated", "true");
  }

  function logout() {
    try {
      fetchLogout().then(data => {
          localStorage.removeItem("isAuthenticated");
          setIsAuthenticated(false);
          setAuthenticatedUserLink("");
        }
      )

    } catch (error) {
      console.error("Logout failed: ", error);
      // TODO 에러 처리 로직 추가
    }
  }

  function handle401Error(){
    setIsAuthenticated(false);
    setAuthenticatedUserLink("");
    setShowLoginInductionModal(true);
  }

  return (
    <AuthContext.Provider value={{isAuthenticated, authenticatedUserLink, showLoginInductionModal, setShowLoginInductionModal, login, logout, handle401Error}}>
      {children}
    </AuthContext.Provider>
  );
}
