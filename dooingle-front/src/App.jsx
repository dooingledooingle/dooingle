import { createBrowserRouter, RouterProvider } from "react-router-dom";
import WelcomePage from "./pages/Welcome.jsx";
import AdminHomePage from "./pages/AdminHome.jsx";
import FeedPage from "./pages/Feed.jsx";
import PersonalDooinglePage from "./pages/PersonalDooingle.jsx";
import MyProfilePage from "./pages/MyProfile.jsx";

const router = createBrowserRouter([
  { path: '/', element: <WelcomePage /> },
  { path: '/feeds', element: <FeedPage /> },
  { path: '/personal-dooingles/:userLink', element: <PersonalDooinglePage /> },
  { path: '/my-profile', element: <MyProfilePage /> },
  { path: '/admin', element: <AdminHomePage /> },
])

export default function App() {
  return <RouterProvider router={router}/>
}
