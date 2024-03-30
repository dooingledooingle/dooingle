import {useContext} from "react";
import {AuthContext} from "./AuthContext.jsx";
import {NotificationContext} from "./NotificationContext.jsx";

export function useAuth() {
  return useContext(AuthContext)
}

export function useNotification() {
  return useContext(NotificationContext)
}
