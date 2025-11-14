document.addEventListener("DOMContentLoaded", () => {
  function toggleItems(selector, button) {
    const items = document.querySelectorAll(selector);

    const isHidden = [...items].some(el => el.classList.contains("d-none"));

    if (isHidden) {
      // ▼ 현재 숨겨진 상태 → 보여주기
      items.forEach((el, i) => {
        el.classList.remove("d-none");
        el.style.opacity = "0";

        setTimeout(() => {
          el.style.transition = "opacity .3s";
          el.style.opacity = "1";
        }, 10 + i * 80);
      });
      button.textContent = "줄이기 ▲";

    } else {
      // ▲ 현재 보이는 상태 → 다시 숨기기
      items.forEach((el) => {
        el.style.transition = "opacity .3s";
        el.style.opacity = "0";
        setTimeout(() => {
          el.classList.add("d-none");
        }, 300);
      });
      button.textContent = "더보기 ▼";
    }
  }

  const toggleCategory = document.getElementById("toggleCategory");
  if (toggleCategory) {
    toggleCategory.addEventListener("click", () =>
      toggleItems(".extra-category", toggleCategory)
    );
  }

  const toggleSeller = document.getElementById("toggleSeller");
  if (toggleSeller) {
    toggleSeller.addEventListener("click", () =>
      toggleItems(".extra-seller", toggleSeller)
    );
  }
});
