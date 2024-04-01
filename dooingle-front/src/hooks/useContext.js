import {useContext} from "react";
import {AuthContext} from "../contexts/AuthContext.jsx";
import {NotificationContext} from "../contexts/NotificationContext.jsx";
import {ReportContext} from "../contexts/ReportContext.jsx";

export function useAuth() {
  return useContext(AuthContext)
}

export function useNotification() {
  return useContext(NotificationContext)
}

export function useReport() {
  return useContext(ReportContext)
}

