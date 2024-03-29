export default function PostSubmitButton({children, onClick, className}) {
  return <button onClick={onClick} className={className}>{children}</button>
}
