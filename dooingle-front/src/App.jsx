import {createBrowserRouter, RouterProvider} from "react-router-dom";
import WelcomePage from "./pages/Welcome.jsx";
import AdminHomePage from "./pages/AdminHome.jsx";
import FeedPage from "./pages/Feed.jsx";
import PersonalDooinglePage from "./pages/PersonalDooingle.jsx";
import MyProfilePage from "./pages/MyProfile.jsx";
import FollowPage from "./pages/Follow.jsx";
import NoticePage from "./pages/Notice.jsx";
import NoticeDetailPage from "./pages/NoticeDetail.jsx";
import RootLayout from "./layouts/RootLayout.jsx";
import MainLayout from "./layouts/MainLayout.jsx";
import AuthProvider from "./contexts/AuthContext.jsx";
import LogoutPage from "./pages/Logout.jsx";
import NotificationProvider from "./contexts/NotificationContext.jsx";
import ReportProvider from "./contexts/ReportContext.jsx";
import LoginPage from "./pages/Login.jsx";
import DooinglePagesExplorationPage from "./pages/DooinglPagesExploration.jsx";

const router = createBrowserRouter([
  { path: '/', element: (
    <AuthProvider>
      <WelcomePage />
    </AuthProvider>
    )
  },
  {
    path: '/login',
    element: (
      <AuthProvider>
        <LoginPage />
      </AuthProvider>
    ),
  },
  {
    path: '/logout',
    element: (
      <AuthProvider>
        <LogoutPage />
      </AuthProvider>
    ),
  },
  {
    element: (
      <AuthProvider>
        <NotificationProvider>
          <ReportProvider>
            <RootLayout/>
          </ReportProvider>
        </NotificationProvider>
      </AuthProvider>
    ),
    children: [
      { path: '/personal-dooingles/:userLink', element: <PersonalDooinglePage /> },
      { path: '/my-profile', element: <MyProfilePage /> },
      {
        element: <MainLayout />,
        children: [
          { path: '/feeds', element: <FeedPage /> },
          { path: '/exploration', element: <DooinglePagesExplorationPage /> },
          { path: '/follows', element: <FollowPage /> },
          { path: '/notices', element: <NoticePage /> },
          { path: '/notices/:noticeId', element: <NoticeDetailPage /> },
        ]
      }
    ]
  },
  { path: '/admin', element: <AdminHomePage /> },
])

export default function App() {
  return <RouterProvider router={router}/>;
}
