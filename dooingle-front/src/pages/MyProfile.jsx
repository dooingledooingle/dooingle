import Header from "../components/Header.jsx";
import {useEffect, useRef, useState} from "react";
import axios from "axios";
import {BACKEND_SERVER_ORIGIN} from "../env.js";
import {Link} from "react-router-dom";

const profileInitialState = {
  nickname: "",
  description: "",
  imageUrl: "",
}

async function fetchCurrentProfile() { // TODO Feed에도 있는 함수, 추후 반드시 정리 필요
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users/profile`, {
    withCredentials: true,
  });
  return response.data;
}

async function fetchUpdateProfile(formData) {
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
  console.log(response.status);
  /* TODO 에러 처리 */
}

export default function MyProfilePage() {
  const [prevProfile, setPrevProfile] = useState(profileInitialState);
  const [updateImageUrlPreview, setUpdateImageUrlPreview] = useState("");
  const imageFileInputRef = useRef();
  const descriptionInputRef = useRef();

  useEffect(() => {
    fetchCurrentProfile().then(fetchedProfile => {
      setPrevProfile(fetchedProfile)
    })
  }, []);

  function handleImageInput(event) {
    const imageFile = event.target.files[0]

    if (imageFile) {
      const fileExtension = imageFile.name.split(".").pop().toLowerCase();
      if (fileExtension !== "png" && fileExtension !== "jpg" && fileExtension !== "jpeg") {
        alert("png 또는 jpg 파일만 업로드 가능합니다.");
        event.target.value = "";
        return;
      }

      const fileSizeInMB = imageFile.size / 1024 / 1024;
      if (fileSizeInMB > 5) {
        alert("파일 크기는 5MB를 초과할 수 없습니다.");
        event.target.value = "";
        return;
      }

      const createdPreviewUrl = URL.createObjectURL(imageFile);
      setUpdateImageUrlPreview(createdPreviewUrl);
    } else {

      alert("파일을 선택해주세요!");
    }
  }

  function handleRestoreProfileImageButton() {
    setUpdateImageUrlPreview(prevProfile.imageUrl);
  }

  function handleSaveButton() {
    const formData = new FormData();

    let formDataImageUrl = null
    if (updateImageUrlPreview === "") {
      formDataImageUrl = prevProfile.imageUrl
    }

    const requestObject = {
      description: descriptionInputRef.current.value, // .value가 없을 경우, "Converting circular structure to JSON" 에러 발생
      imageUrl: formDataImageUrl,
    }

    formData.append("request", JSON.stringify(requestObject))
    formData.append("img", imageFileInputRef.current.files[0])

    fetchUpdateProfile(formData)
  }

  return (
    <>
      <Header />

      <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] h-[4.5rem] ml-40px">
        <section className="col-start-4 col-span-6 flex flex-col items-center py-[2.75rem] text-[#5f6368] gap-8"> {/* TODO UI 정리 시 py-[2.75rem] gap-8 지우고 정리작업 하기 */}
          <img className="border-[0.125rem] rounded-full w-[15rem] h-[15rem] object-cover"
               src={updateImageUrlPreview || prevProfile.imageUrl}
               alt="사용자 프로필 이미지 미리보기"/>
          <div>
            <input type="file" ref={imageFileInputRef} onChange={handleImageInput} />
          </div>
          <div>
            <button onClick={handleRestoreProfileImageButton}>프로필 사진 원래대로 버튼</button>
          </div>
          <div className="w-full">
            <div className="flex justify-center gap-16">
              <span>이름</span>
              <span>{prevProfile.nickname}</span>
            </div>
            <div className="flex justify-center gap-4">
              <span>자기소개</span>
              <input type="text" ref={descriptionInputRef} placeholder={prevProfile.description} />
            </div>
          </div>
          <div>
            <button onClick={handleSaveButton}>변경 내용 저장 버튼</button>
          </div>
        </section>

        <div className="col-start-1 col-span-12 mt-10">
          <Link to={"/"}>웰컴 페이지로</Link>
        </div>
      </div>
    </>
  );
}
