import {fetchDeleteCatch} from "../../fetch.js";

export default function DeleteModal({setShowDeleteModal, setDooinglesAndCatches, deleteTargetRelatedDooingleIdRef, deleteTargetIdRef, deleteContentRef}) {

  function handleCatchDeleteButton() {
    fetchDeleteCatch(deleteTargetRelatedDooingleIdRef.current, deleteTargetIdRef.current).then(() => {
      setDooinglesAndCatches(prev => {
          const catchDeletedDooinglesAndCatches = [...prev]
          // 삭제 후 렌더링을 위해 임의로 content, deletedAt을 넣음 - TODO 더 나은 방법이 있을 것 같다...
          const targetCatch = catchDeletedDooinglesAndCatches.filter(dooingleAndCatch => dooingleAndCatch.dooingleId === Number(deleteTargetRelatedDooingleIdRef.current))[0].catch
          targetCatch.content = null;
          targetCatch.deletedAt = Date.now();
          return catchDeletedDooinglesAndCatches;
        }
      )
    })

    deleteContentRef.current = undefined;
    setShowDeleteModal(false);
  }

  function handleCancelButton() {
    deleteContentRef.current = undefined
    setShowDeleteModal(false);
  }

  return (
    <div className="fixed flex items-center inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div className="relative -inset-y-[4rem] mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div className="flex flex-col items-center my-3 gap-[1rem]">
          <span className="font-light">{deleteContentRef.current}</span>
          <span className="font-medium text-gray-900">이 캐치를 정말 삭제하시겠어요?</span>
          <span className="font-light text-gray-900 text-[0.875rem]">삭제한 캐치는 다시 볼 수 없어요 😭😭</span>
          <div className="flex justify-center gap-[1.5rem]">
            <button onClick={handleCatchDeleteButton} className="p-[0.5rem] bg-[#fa61bd] rounded-[0.5rem]">
              <p className="text-[0.75rem] text-white">삭제하기</p>
            </button>
            <button onClick={handleCancelButton} className="p-[0.5rem]">
              <p className="text-[0.75rem]">취소</p>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
