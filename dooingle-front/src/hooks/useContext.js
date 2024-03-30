import {useContext} from "react";
import {AuthContext} from "../contexts/AuthContext.jsx";
import {NotificationContext} from "../contexts/NotificationContext.jsx";

export function useAuth() {
  return useContext(AuthContext)
}

export function useNotification() {
  return useContext(NotificationContext)
}
