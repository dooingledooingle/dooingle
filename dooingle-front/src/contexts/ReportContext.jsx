import {createContext, useRef, useState} from "react";

export const ReportContext = createContext()

export default function ReportProvider({children}) {
  const [showReportModal, setShowReportModal] = useState(false);
  const [reportTarget, setReportTarget] = useState({});
  const reportPostContentRef = useRef("");

  return (
    <ReportContext.Provider value={{showReportModal, setShowReportModal, reportTarget, setReportTarget, reportPostContentRef}}>
      {children}
    </ReportContext.Provider>
  );
}
