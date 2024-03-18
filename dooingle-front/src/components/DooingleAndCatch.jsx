export default function DooingleAndCatch({ ownerName, dooingleContent, catchContent }) {
  return (
    <div className="py-[1rem]">
      <div className="flex flex-col px-[0.75rem] gap-[0.375rem]">
        <div className="px-[0.5rem] text-[#456bf5] font-bold">
          <p>익명의 뒹글러</p>
        </div>
        <div className="px-[1.25rem] py-[0.625rem] w-[65%] border-[0.03125rem] border-[#8692ff] rounded-[0.625rem]">
          <div className="text-[#5f6368]">
            <p>{dooingleContent}</p>
          </div>
        </div>
      </div>
      <div className="flex flex-col items-end px-[0.75rem] gap-[0.375rem]">
        <div className="px-[0.5rem] text-[#456bf5] font-bold">
          <p>{ownerName}</p>
        </div>
        <div className="px-[1.25rem] py-[0.625rem] w-[65%] border-[0.03125rem] border-[#8692ff] rounded-[0.625rem]">
          <div className="text-[#5f6368]">
            <p>{catchContent}</p>
          </div>
        </div>
      </div>
    </div>
  );
}
