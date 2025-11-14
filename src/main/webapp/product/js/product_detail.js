document.addEventListener("DOMContentLoaded", () => {

  function toggleItems(selector, button) {
    const items = document.querySelectorAll(selector);
    const hidden = [...items].every(el => el.classList.contains("d-none"));

    if (hidden) {
      items.forEach((el, i) => {
        el.classList.remove("d-none");
        el.style.opacity = 0;
        setTimeout(() => {
          el.style.transition = "opacity .3s";
          el.style.opacity = 1;
        }, 50 + i * 80);
      });
      button.textContent = "줄이기 ▲";
    } else {
      items.forEach(el => {
        el.style.transition = "opacity .3s";
        el.style.opacity = 0;
        setTimeout(() => el.classList.add("d-none"), 300);
      });
      button.textContent = "더보기 ▼";
    }
  }

  const toggleCategory = document.getElementById("toggleCategory");
  const toggleSeller = document.getElementById("toggleSeller");

  if (toggleCategory)
    toggleCategory.addEventListener("click", () => toggleItems(".extra-category", toggleCategory));

  if (toggleSeller)
    toggleSeller.addEventListener("click", () => toggleItems(".extra-seller", toggleSeller));



  document.querySelectorAll(".review-text").forEach(text => {
    const btn = text.closest(".review-item").querySelector(".toggle-btn");
    if (!btn) return;

    const lineHeight = parseFloat(getComputedStyle(text).lineHeight);
    const maxHeight = lineHeight * 3;

    if (text.scrollHeight <= maxHeight + 2) {
      btn.style.display = "none";
      return;
    }

    btn.addEventListener("click", () => {
      text.classList.toggle("expanded");

      if (text.classList.contains("expanded")) {
        btn.textContent = "접기 ▲";
      } else {
        btn.textContent = "더보기 ▼";
      }
    });
  });

});
