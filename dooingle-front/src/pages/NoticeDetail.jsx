import {Link, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {fetchNoticeResponse} from "../fetch.js";

export default function NoticeDetailPage() {

  const [noticeResponse, setNoticeResponse] = useState();
  const params = useParams();
  const noticeId = params?.noticeId;

  useEffect(() => {
    fetchNoticeResponse(noticeId).then(noticeResponseData => {
      setNoticeResponse(noticeResponseData)
    })
  }, [noticeId])

  return (
    <section className="col-start-4 col-span-6 flex flex-col py-[2.75rem] text-[#5f6368]">
      <div className="flex px-[2rem] gap-[1.75rem] shadow-[inset_0_-0.125rem_0_0_#9aa1aa]">
        <div className="hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]">
          <div className="py-[0.5rem]">
            <span>공지사항</span>
          </div>
        </div>
      </div>

      <div className="flex flex-col gap-[1.5rem] px-[0.625rem] py-[1.25rem] text-[#5f6368]">
        <div className="flex justify-between">
          <div className="font-bold text-[1rem]">{noticeResponse?.title}</div>
          <div className="font-bold text-[0.875rem]">{noticeResponse?.createdAt.toString().substring(0, 10)}</div>
        </div>
        <div>
          <div className="text-[0.875rem]">{noticeResponse?.content}</div>
        </div>
      </div>

      <div className="px-[1rem] py-[0.75rem] border-t-[0.03125rem] border-t-[#9aa1aa]">
        <div className="text-[0.875rem] text-[#5f6368]] flex justify-end gap-[1rem]">
          <Link to="../" relative="path">목록</Link>
        </div>
      </div>
    </section>
  );
}
