document.addEventListener("DOMContentLoaded", () => {
  // 🔹 부드러운 토글 애니메이션 적용 함수
  const toggleItems = (selector, button) => {
    const items = document.querySelectorAll(selector);
    const isHidden = [...items].every(el => el.classList.contains("d-none"));

    if (isHidden) {
      // ▶ 펼치기 (fade-in)
      items.forEach((el, i) => {
        el.classList.remove("d-none");
        el.style.opacity = 0;
        setTimeout(() => {
          el.style.transition = "opacity 0.4s ease";
          el.style.opacity = 1;
        }, i * 100); // 순차적으로 나타나게
      });
      button.textContent = "줄이기 ▲";
    } else {
      // ▶ 접기 (fade-out)
      items.forEach((el, i) => {
        el.style.transition = "opacity 0.3s ease";
        el.style.opacity = 0;
        setTimeout(() => {
          el.classList.add("d-none");
        }, 300);
      });
      button.textContent = "더보기 ▼";
      window.scrollTo({
        top: button.parentElement.offsetTop - 200,
        behavior: "smooth"
      });
    }
  };

  // 카테고리 상품
  const toggleCategory = document.getElementById("toggleCategory");
  if (toggleCategory) {
    toggleCategory.addEventListener("click", () => toggleItems(".extra-category", toggleCategory));
  }

  // 판매자 상품
  const toggleSeller = document.getElementById("toggleSeller");
  if (toggleSeller) {
    toggleSeller.addEventListener("click", () => toggleItems(".extra-seller", toggleSeller));
  }
});
