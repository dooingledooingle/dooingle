import {Link} from "react-router-dom";

export default function Dooingle({ownerName, ownerUserLink, dooingleId, content, hasCatch}) {
  return (
    <div className="flex flex-col p-[0.75rem] gap-[0.5rem]">
      <div className="text-[#456bf5] font-bold">
        <span>익명의 뒹글러 → </span>
        <Link to={`/personal-dooingles/${ownerUserLink}`}>{ownerName}</Link>
        {
          hasCatch &&
          <Link
            to={`/personal-dooingles/${ownerUserLink}?lastDooingleId=${dooingleId + 1}`}
            className="pl-[1rem] font-medium text-[#5f6368]">답변이 있는 뒹글입니다.
          </Link>
        }
        {/* TODO dooingleId 넣어서 개인 페이지 해당 글로 바로 갈 수 있도록 만들기 */}
      </div>
      <div className="px-[1.25rem] py-[0.625rem] border-[0.03125rem] border-[#8692ff] rounded-[0.625rem]">
        <div className="text-[#5f6368]">
          <p>{content}</p>
        </div>
      </div>
    </div>
  );
}

