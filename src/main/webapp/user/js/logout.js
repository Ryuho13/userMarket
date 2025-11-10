/**
 * @fileoverview 단감나라 로그아웃 페이지에서 사용되는 클라이언트 측 스크립트.
 * 로그아웃 시 아이디 저장 정보를 정리합니다.
 */

document.addEventListener("DOMContentLoaded", () => {
  console.log("Logout page loaded. Performing client-side cleanup.");

  // 1. 로컬 스토리지 정리
  // '아이디 저장' 기능이 활성화되었던 경우, 로그아웃 시 해당 정보를 삭제하여
  // 다음 로그인 시 아이디가 자동으로 채워지지 않도록 합니다. (선택 사항)
  if (localStorage.getItem("savedUserId")) {
    // console.log("Removing savedUserId from localStorage.");
    localStorage.removeItem("savedUserId");
  }

  // 2. 추가적인 클라이언트 정리 작업이 필요하다면 여기에 추가합니다.
  // 예: 특정 쿠키 삭제, 캐시 초기화 등 (대부분 서버에서 처리됨)

  // 💡 참고: 실제 로그아웃 처리는 서버(LogoutServlet)에서 세션을 무효화함으로써 이루어집니다.
  // 이 JS는 주로 클라이언트 측의 사용자 경험 개선(UX) 및 정리 작업에 사용됩니다.
});