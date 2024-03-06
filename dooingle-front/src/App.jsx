import { createBrowserRouter, RouterProvider } from "react-router-dom";
import WelcomePage from "./pages/Welcome.jsx";
import AdminHomePage from "./pages/AdminHome.jsx";
import FeedPage from "./pages/Feed.jsx";

const router = createBrowserRouter([
  { path: '/', element: <WelcomePage /> },
  { path: '/feeds', element: <FeedPage /> },
  { path: '/admin', element: <AdminHomePage /> },
])

export default function App() {
  return <RouterProvider router={router}/>
}
