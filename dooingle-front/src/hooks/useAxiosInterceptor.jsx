import axios from "axios";
import {useAuth} from "./useContext.js";
import {useEffect} from "react";

export default function useAxiosInterceptor() {
  // TODO https://velog.io/@jce1407/Axios-Interceptor에서-Hook을-사용하는-방법
  //  커스텀 훅 관련 복잡한 이야기 - 처음에 단순하게 ChatGPT 코드 그대로 실행했으나,
  //  Hooks can only be called inside of the body of a function component. 에러와 함께 실패
  //  "axios interceptor에서 커스텀 훅을 사용하려면" 키워드로 구글링 함
  const {handle401Error} = useAuth()

  const responseInterceptor = axios.interceptors.response.use(
    response => response,
    error => {
      if (error.response.status === 401) {
        handle401Error();
      }
      return Promise.reject(error);
    }
  );

  useEffect(() => {
    return () => {
      axios.interceptors.response.eject(responseInterceptor);
    };
  }, [responseInterceptor]);
}
