import axios from "axios";
import {BACKEND_SERVER_ORIGIN} from "./env.js";

export async function fetchDooinglesFeedSlice(lastDooingleId = null) {
  const queryParameter = lastDooingleId === null ? "" : `?cursor=${lastDooingleId}`

  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/dooingles`.concat(queryParameter), {
    withCredentials: true, // ajax 요청에서 withCredentials config 추가
  });
  return response.data;
}

export async function fetchDooinglesFeedSliceOfFollowing(lastDooingleId = null) {
  const queryParameter = lastDooingleId === null ? "" : `?cursor=${lastDooingleId}`

  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/dooingles/follow`.concat(queryParameter), {
    withCredentials: true,
  });
  return response.data;
}

export async function fetchLoggedInUserLink() {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users/current-dooingler`, {
    withCredentials: true, // ajax 요청에서 withCredentials config 추가
  });
  return response.data.userLink;
}

export async function fetchDooinglesAndCatchesSlice(userLink, lastDooingleId = null) {
  const queryParameter = lastDooingleId === null ? "" : `?cursor=${lastDooingleId}`

  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users/${userLink}/dooingles`.concat(queryParameter), {
    withCredentials: true, // ajax 요청에서 withCredentials config 추가
  });
  return response.data;
}

export async function fetchAddCatch(dooingleId, catchContent) {
  const addCatchRequestBody = {
    content: catchContent
  }

  const response = await axios.post(
    `${BACKEND_SERVER_ORIGIN}/api/dooingles/${dooingleId}/catches`,
    addCatchRequestBody,
    {
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    },
  );
  return response.data;
}

export async function fetchIsFollowingUser(userLink) {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/follow/${userLink}`, {
    withCredentials: true, // ajax 요청에서 withCredentials config 추가
  });
  return response.data.isFollowingUser;
}

export async function fetchFollowingList() {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/follow`, {
    withCredentials: true, // ajax 요청에서 withCredentials config 추가
  });
  return response.data;
}

export async function fetchAddFollow(userLink) {
  const response = await axios.post(
    `${BACKEND_SERVER_ORIGIN}/api/follow/${userLink}`,
    null,
    {withCredentials: true},
  );
  return response.data;
}

export async function fetchCancelFollow(userLink) {
  const response = await axios.delete(`${BACKEND_SERVER_ORIGIN}/api/follow/${userLink}`, {
    withCredentials: true,
  });
  return response.data;
}

export async function fetchAddDooingle(userLink, dooingleContent) {
  const addDooingleRequestBody = {
    content: dooingleContent
  }

  const response = await axios.post(
    `${BACKEND_SERVER_ORIGIN}/api/users/${userLink}/dooingles`,
    addDooingleRequestBody,
    {
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    },
  );
  return response.data;
}

export async function fetchPageOwnerUserProfile(userLink) {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users/${userLink}/profile`, {
    withCredentials: true,
  });
  return response.data;
}

export async function fetchCurrentProfile() {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users/profile`, {
    withCredentials: true,
  });
  return response.data;
}

export async function fetchUpdateProfile(formData) {
  const response = await axios.patch(
    `${BACKEND_SERVER_ORIGIN}/api/users/profile`,
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
      withCredentials: true,
    }
  );
  /* TODO 에러 처리 */
  console.log(response.status);

  return response.data;
}

export async function fetchNoticePage(pageNumber) {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/notices?page=${pageNumber}`, {
    withCredentials: true,
  });
  return response.data;
}

export async function fetchNoticeResponse(noticeId) {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/notices/${noticeId}`, {
    withCredentials: true,
  });
  return response.data;
}

export async function fetchUserProfileImageUrl(userLink) {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users/${userLink}/profile-image`, {
    withCredentials: true,
  });
  return response.data.imageUrl;
}

export async function fetchUserList(condition) {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users?condition=${condition}`, {
    withCredentials: true,
  });
  return response.data;
}

export async function fetchNotifications() {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/notifications`, {
    withCredentials: true, // ajax 요청에서 withCredentials config 추가
  });
  return response.data;
}
