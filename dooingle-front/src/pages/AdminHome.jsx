import { Link } from "react-router-dom";

export default function AdminHomePage() {
  return (
    <>
      <div><Link to={"/"}>사용자 페이지 홈으로</Link></div>
      <div><Link to={"http://localhost:8080/swagger-ui/index.html#/"}>Swagger</Link></div>
      <div><Link to={"http://localhost:8080/h2-console"}>H2 콘솔</Link></div>
    </>
  )
}
