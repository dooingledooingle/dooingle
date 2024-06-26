import {useEffect, useRef, useState} from "react";
import SmallSubmitButton from "../components/button/SmallSubmitButton.jsx";
import {fetchCurrentProfile, fetchUpdateProfile} from "../fetch.js";
import {useAuth} from "../hooks/useContext.js";

const profileInitialState = {
  nickname: "",
  description: "",
  imageUrl: "",
}

export default function MyProfilePage() {
  const [prevProfile, setPrevProfile] = useState(profileInitialState);
  const [updateImageUrlPreview, setUpdateImageUrlPreview] = useState("");
  const imageFileInputRef = useRef();
  const descriptionInputRef = useRef();
  const {isAuthenticated, setShowLoginInductionModal} = useAuth();

  useEffect(() => {
    isAuthenticated && fetchCurrentProfile().then(fetchedProfile => {
      setPrevProfile(fetchedProfile)
    })

    !isAuthenticated && setShowLoginInductionModal(true)
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
    const descriptionInputValue = descriptionInputRef.current.value
    
    if (descriptionInputValue.toString().trim().length > 100 || descriptionInputValue.toString().trim().length < 5) {
      alert("자기소개는 5자 이상 100자 이하여야 합니다.");
      return;
    }

    const formData = new FormData();

    let formDataImageUrl = null
    if (updateImageUrlPreview === "") {
      formDataImageUrl = prevProfile.imageUrl
    }

    const requestObject = {
      description: descriptionInputValue, // current.value가 아니라 current만 둘 경우, "Converting circular structure to JSON" 에러 발생
      imageUrl: formDataImageUrl,
    }

    formData.append("request", JSON.stringify(requestObject))
    formData.append("img", imageFileInputRef.current.files[0])

    fetchUpdateProfile(formData).then(profileAfterUpdate => {
      setPrevProfile(prev => {
        return {...prev, description: profileAfterUpdate.description, imageUrl: profileAfterUpdate.imageUrl};
      })

      /* TODO 에러 처리 */
      alert("프로필이 성공적으로 수정됐습니다.")
    })
  }

  return (
    <>
      <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] h-[4.5rem] ml-40px">
        <section className="col-start-4 col-span-6 flex flex-col items-center py-[2.75rem] text-[#5f6368] gap-[2rem]"> {/* TODO UI 정리 시 py-[2.75rem] gap-8 지우고 정리작업 하기 */}
          <img className="border-[0.125rem] rounded-full w-[15rem] h-[15rem] object-cover"
               src={updateImageUrlPreview || prevProfile.imageUrl || "/no-image-1.png"}
               alt="사용자 프로필 이미지 미리보기"/>
          <div className="flex items-center gap-[2.75rem]">
            <SmallSubmitButton type="button" onClick={handleRestoreProfileImageButton} className="mr-0">선택 취소</SmallSubmitButton>
            <input id="profileImageFileInput" type="file" ref={imageFileInputRef} onChange={handleImageInput} className="hidden"/>
            <label htmlFor="profileImageFileInput" className="cursor-pointer px-[0.5rem] py-[0.25rem]
                 text-[0.75rem] text-[#5f6368] text-center font-bold
                 border-[0.0625rem] border-[#fa61bd] rounded-[0.625rem]
                 peer-hover:border-transparent transition-colors">프로필 사진 <br/> 파일 선택</label>
          </div>
          <div className="w-full flex flex-col gap-[1rem]">
            <div className="flex justify-center">
              <div className="w-[50%] flex justify-end">
                <span className="mr-[10%]">이름</span>
              </div>
              <div className="w-[50%]">
                <span className="ml-[10%]">{prevProfile.nickname}</span>
              </div>
            </div>
            <div className="flex justify-center">
              <div className="w-[50%] flex justify-end items-center">
                <span className="mr-[10%]">자기소개</span>
              </div>
              <div className="w-[50%]">
                <textarea ref={descriptionInputRef} placeholder={prevProfile.description}
                          className="ml-[6%] w-[80%] p-[0.75rem] overflow-y-hidden resize-none
                    border-[0.03125rem] border-[#9aa1aa] rounded-[0.625rem]
                    focus:outline-none"/>
              </div>
            </div>
          </div>
          <div className="mt-[1rem]">
            <button onClick={handleSaveButton} className="px-[0.75rem] py-[0.5rem] rounded-[0.625rem] font-bold
                   bg-[#fa61bd] text-white">변경 내용 저장</button>
          </div>
        </section>
      </div>
    </>
  );
}
