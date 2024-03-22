import { createBrowserRouter, RouterProvider } from "react-router-dom";
import WelcomePage from "./pages/Welcome.jsx";
import AdminHomePage from "./pages/AdminHome.jsx";
import FeedPage from "./pages/Feed.jsx";
import PersonalDooinglePage from "./pages/PersonalDooingle.jsx";

const router = createBrowserRouter([
  { path: '/', element: <WelcomePage /> },
  { path: '/feeds', element: <FeedPage /> },
  { path: '/personal-dooingles', element: <PersonalDooinglePage /> },
  { path: '/personal-dooingles/:userId', element: <PersonalDooinglePage /> },
  { path: '/admin', element: <AdminHomePage /> },
])

export default function App() {
  return <RouterProvider router={router}/>
}
