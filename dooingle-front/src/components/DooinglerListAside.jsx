export default function DooinglerListAside() {
  return (
    <aside
      className="col-start-10 col-span-3 flex flex-col justify-end items-center text-[#5f6368]">
      <div className="sticky bottom-0 py-[4.5rem]">
        <div
          className="flex flex-col items-center gap-[0.25rem] rounded-br-[0.625rem] border-b-[0.0625rem] border-[#ef7ec2]">
          <div className="flex flex-col gap-[0.5rem] px-[1rem] py-[0.625rem]">
            <div className="font-bold text-[#5f6368] text-[1rem]">
              <p>새로운 뒹글 페이지</p>
            </div>
            <div className="flex flex-col gap-[0.25rem] px-[0.625rem]">
              <p>깜이</p>
              <p>최유민</p>
              <p>곽준선</p>
              <p>김다진</p>
              <p>노하영</p>
            </div>
          </div>
          <div className="flex flex-col gap-[0.5rem] px-[1rem] py-[0.625rem]">
            <div className="font-bold text-[#5f6368] text-[1rem]">
              <p>뜨거운 뒹글 페이지</p>
            </div>
            <div className="flex flex-col gap-[0.25rem] px-[0.625rem]">
              <p>깜이</p>
              <p>최유민</p>
              <p>곽준선</p>
              <p>김다진</p>
              <p>노하영</p>
            </div>
          </div>
        </div>
      </div>
    </aside>
  );
}
