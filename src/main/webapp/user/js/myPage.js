// 페이지가 다 그려지면 아이콘 렌더링하고 기본 탭을 켬
document.addEventListener("DOMContentLoaded", function () {
  // Lucide 아이콘 렌더링 (CDN 스크립트가 있으면 동작)
  if (window.lucide && lucide.createIcons) {
    lucide.createIcons();
  }

  // 기본 탭은 'products'로
  changeTab('products');
});

// 탭 바꾸기 (버튼 클래스도 그냥 대충 바꿔줌)
function changeTab(tabName) {
  var tabProductsBtn = document.getElementById("tab-products");
  var tabWishlistBtn = document.getElementById("tab-wishlist");
  var tabChatsBtn = document.getElementById("tab-chats");

  var contentProducts = document.getElementById("content-products");
  var contentWishlist = document.getElementById("content-wishlist");
  var contentChats = document.getElementById("content-chats");

  // 전부 숨기고
  if (contentProducts) contentProducts.classList.add("hidden");
  if (contentWishlist) contentWishlist.classList.add("hidden");
  if (contentChats) contentChats.classList.add("hidden");

  // 버튼 스타일도 전부 비활성화 느낌으로
  if (tabProductsBtn) tabProductsBtn.className = "tab-button border-b-2 font-medium py-2 px-1 text-gray-500 border-transparent hover:border-gray-300";
  if (tabWishlistBtn) tabWishlistBtn.className = "tab-button border-b-2 font-medium py-2 px-1 text-gray-500 border-transparent hover:border-gray-300";
  if (tabChatsBtn) tabChatsBtn.className = "tab-button border-b-2 font-medium py-2 px-1 text-gray-500 border-transparent hover:border-gray-300";

  // 선택된 탭만 보여주고 버튼 강조
  if (tabName === 'products') {
    if (contentProducts) contentProducts.classList.remove("hidden");
    if (tabProductsBtn) tabProductsBtn.className = "tab-button border-b-2 font-medium py-2 px-1 text-green-500 border-green-500";
  } else if (tabName === 'wishlist') {
    if (contentWishlist) contentWishlist.classList.remove("hidden");
    if (tabWishlistBtn) tabWishlistBtn.className = "tab-button border-b-2 font-medium py-2 px-1 text-green-500 border-green-500";
  } else if (tabName === 'chats') {
    if (contentChats) contentChats.classList.remove("hidden");
    if (tabChatsBtn) tabChatsBtn.className = "tab-button border-b-2 font-medium py-2 px-1 text-green-500 border-green-500";
  }
}

// 이미지 미리보기 (파일 타입 체크만 간단히)
function previewImage(e) {
  var file = e.target.files && e.target.files[0];
  if (!file) return;

  if (!file.type || file.type.indexOf("image/") !== 0) {
    alert("이미지 파일만 올려주세요!");
    e.target.value = "";
    return;
  }

  var reader = new FileReader();
  reader.onload = function (ev) {
    var img = document.getElementById("profile-image");
    if (img) {
      img.src = ev.target.result;
    }
  };
  reader.readAsDataURL(file);
}

// (참고) JSP의 버튼에 이미 onclick="changeTab('products')"가 있으니
// 전역의 changeTab을 그대로 씀. 위처럼 전역 함수 선언으로 충분함.
