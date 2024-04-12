export default function Pagination({ currentPage, currentPageNumber, onPageChange }) {

  // 페이지 범위 계산
  const totalPages = currentPage.totalPages;
  const startPage = Math.floor((currentPageNumber - 1) / 5) * 5 + 1;
  const endPage = Math.min(startPage + 4, totalPages);

  // 페이지 번호 배열 생성
  const pageNumbers = Array.from({ length: endPage - startPage + 1 }, (_, idx) => startPage + idx);

  return (
    <div className="flex justify-center gap-[1.25rem] font-bold">
      {/* 이전 페이지 그룹 */}
      {startPage > 1 && (
        <button
          className="text-[0.625rem] hover:text-[#8692ff]"
          onClick={() => onPageChange(startPage - 1)}
        >
          ◀
        </button>
      )}

      {/* 페이지 번호 */}
      {pageNumbers.map((pageNumber) => (
        <button
          key={pageNumber}
          className={currentPageNumber === pageNumber.toString() ? "text-[#8692ff]" : "text-[#5f6368]"}
          onClick={() => onPageChange(pageNumber)}
        >
          {pageNumber}
        </button>
      ))}

      {/* 다음 페이지 그룹 */}
      {endPage < totalPages && (
        <button
          className="text-[0.625rem] hover:text-[#8692ff]"
          onClick={() => onPageChange(endPage + 1)}
        >
          ▶
        </button>
      )}
    </div>
  );
}
