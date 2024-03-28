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
            className="pl-[1rem] font-medium text-[#5f6368] hover:text-[#fa61bd]">답변이 있는 뒹글입니다.
          </Link>
        }
      </div>
      <div className="pl-[0.75rem] pr-[1rem] py-[0.5rem] border-[0.03125rem] border-[#8692ff] rounded-[0.625rem] max-w-fit">
        <span>{content}</span>
      </div>
    </div>
  );
}

