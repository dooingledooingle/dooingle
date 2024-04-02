import {Link} from "react-router-dom";
import {useReport} from "../hooks/useContext.js";

export default function Dooingle({ownerName, ownerUserLink, dooingleId, content, hasCatch}) {

  const {setShowReportModal, setReportTarget} = useReport()

  function handleReportButton(reportedTargetType, reportedTargetId, reportedTargetContent) {
    setReportTarget({
      reportedTargetType: reportedTargetType,
      reportedTargetId: reportedTargetId,
      reportedTargetContent: reportedTargetContent
    })
    setShowReportModal(true)
  }

  return (
    <div className="flex flex-col p-[0.75rem] gap-[0.5rem]">
      <div className="text-[#456bf5] font-bold">
        <span>익명의 뒹글러 → </span>
        <Link to={`/personal-dooingles/${ownerUserLink}`}>{ownerName}</Link>
        {
          hasCatch &&
          <Link
            to={`/personal-dooingles/${ownerUserLink}?lastDooingleId=${dooingleId + 1}`}
            className="pl-[1.125rem] font-medium text-[0.75rem] text-[#5f6368] hover:text-[#fa61bd]">답변이 있는 뒹글입니다.
          </Link>
        }
      </div>
      <div className="flex items-center gap-[0.5rem]">
        <div className="pl-[0.75rem] pr-[1rem] py-[0.5rem] border-[0.03125rem] border-[#8692ff] rounded-[0.625rem] w-fit max-w-[75%]">
          <span className="text-[#5f6368] whitespace-pre-wrap break-words">{content}</span>
        </div>
        <button type="button" onClick={() => handleReportButton("DOOINGLE", dooingleId, content)}>
          <img src="/report.svg" alt="신고 버튼" className="w-[1.125rem] hover:src"/>
        </button>
      </div>
    </div>
  );
}
