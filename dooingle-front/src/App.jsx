import { createBrowserRouter, RouterProvider } from "react-router-dom";
import HomePage from "./pages/Home.jsx";
import AdminHomePage from "./pages/AdminHome.jsx";

const router = createBrowserRouter([
  { path: '/', element: <HomePage /> },
  { path: '/admin', element: <AdminHomePage /> },
])

export default function App() {
  return <>
    <div>
      <button type="button" className="bg-amber-400">Hello tailwindcss</button>
    </div>
    <RouterProvider router={router}/>
  </>
}
