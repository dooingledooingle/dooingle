export default function PostSubmitButton({children, type, onClick}) {
  // TODO onClick이 안 넘어오는 경우에 대해서 깔끔하게 정리해야 함
  return <button type={type} onClick={onClick}
                 className="peer
                 mr-[0.5rem] px-[0.5rem] py-[0.25rem] rounded-[0.625rem]
                 text-[0.75rem] font-bold
                 hover:bg-[#fa61bd] hover:text-white transition-colors duration-1000">{children}</button>
}
