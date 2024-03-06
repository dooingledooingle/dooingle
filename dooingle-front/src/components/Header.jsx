export default function Header() {
  return (
    <header className="border-b-[0.0625rem] border-[#d3d3d3]">
      <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] h-[4.5rem] ml-40px">

        <div className="col-start-4 col-span-6 flex justify-center items-center text-[#8692ff]">
          <div className="min-w-fit">
            <a className="text-[2rem] font-bold">Dooingle</a>
          </div>
        </div>

        <div className="col-start-10 col-span-3 text-[#456bf5] font-medium max-h-full">
          <div className="flex justify-end gap-[15%] mr-[5%] min-h-full">
            <div className="flex flex-col justify-end">
              <div className="mb-[0.5rem]">공지사항</div>
            </div>
            <div className="flex flex-col justify-end">
              <div className="mb-[0.5rem]">설정</div>
            </div>
          </div>
        </div>

      </div>
    </header>
  );
}
