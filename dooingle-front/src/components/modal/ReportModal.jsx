import {useReport} from "../../hooks/useContext.js";
import {useRef} from "react";
import {fetchAddBadReport} from "../../fetch.js";

export default function ReportModal() {
  const {showReportModal, setShowReportModal, reportTarget, setReportTarget} = useReport();
  const reportReasonRef = useRef();

  function handleReportButton() {
    fetchAddBadReport(reportTarget.reportedTargetType, reportTarget.reportedTargetId, reportReasonRef.current.value);

    // TODO 예외 처리
    setReportTarget({})
    setShowReportModal(false)
    alert("정상적으로 신고되었습니다.")
  }

  function handleCancelButton() {
    setReportTarget({})
    setShowReportModal(false)
  }

  return (
    <>
      {
        showReportModal &&
        <div className="fixed flex items-center inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative -inset-y-[4rem] mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="flex flex-col items-center my-3 gap-[1rem]">
              <span className="font-light">{reportTarget.reportedTargetContent}</span>
              <span className="text-[1.125rem] font-medium text-gray-900">
                {reportTarget.reportedTargetType === "DOOINGLE" && "이 뒹글을 정말 신고하시겠어요?"}
                {reportTarget.reportedTargetType === "CATCH" && "이 캐치를 정말 신고하시겠어요?"}
              </span>
              <textarea ref={reportReasonRef} placeholder="신고하는 이유를 입력해주세요."
                        className="ml-[6%] w-[80%] p-[0.75rem] overflow-y-hidden resize-none
                    border-[0.03125rem] border-[#9aa1aa] rounded-[0.625rem]
                    focus:outline-none text-[0.75rem]"/>
              <div className="flex justify-center gap-[1.5rem]">
                <button onClick={handleReportButton} className="p-[0.5rem] bg-[#fa61bd] rounded-[0.5rem]">
                  <p className="text-[0.75rem] text-white">신고하기</p>
                </button>
                <button onClick={handleCancelButton} className="p-[0.5rem]">
                  <p className="text-[0.75rem]">취소</p>
                </button>
              </div>
            </div>
          </div>
        </div>
      }
    </>
  );
}
