export default function Following({followingUserName}) {
  return (
    <div className="flex gap-[1.25rem]">
      <div>
        {/*<img/>*/}
        <div className="bg-[#d9d9d9] w-[3.75rem] h-[3.75rem]"></div>
      </div>
      <div className="flex items-center">
        <span className="font-bold text-[#8692ff]">{followingUserName}</span>
      </div>
      <div className="flex items-center">
        <span className="text-[#5f6368]">소개글</span>
      </div>
    </div>
  );
}
