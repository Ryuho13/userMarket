document.addEventListener("DOMContentLoaded", () => {
  const toggleItems = (selector, button) => {
    const items = document.querySelectorAll(selector);
    const isHidden = [...items].every(el => el.classList.contains("d-none"));

    if (isHidden) {
      items.forEach((el, i) => {
        el.classList.remove("d-none");
        el.style.opacity = 0;
        setTimeout(() => {
          el.style.transition = "opacity 0.4s ease";
          el.style.opacity = 1;
        }, i * 100);
      });
      button.textContent = "줄이기 ▲";
    } else {
      items.forEach(el => {
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

  const toggleCategory = document.getElementById("toggleCategory");
  if (toggleCategory) {
    toggleCategory.addEventListener("click", () => toggleItems(".extra-category", toggleCategory));
  }

  const toggleSeller = document.getElementById("toggleSeller");
  if (toggleSeller) {
    toggleSeller.addEventListener("click", () => toggleItems(".extra-seller", toggleSeller));
  }
});

document.addEventListener("DOMContentLoaded", () => {
  const btn = document.getElementById("btnWish");
  if (!btn) return;

  btn.addEventListener("click", async () => {
    const productId = btn.dataset.productId;
    const isWish = btn.dataset.wish === "true";

    const formData = new URLSearchParams();
    formData.append("productId", productId);
    formData.append("isWish", isWish);

    const res = await fetch(`${contextPath}/product/wishlist`, {
      method: "POST",
      body: formData
    });

    if (res.ok) {
      btn.dataset.wish = (!isWish).toString();
      btn.classList.toggle("btn-danger", !isWish);
      btn.classList.toggle("btn-outline-secondary", isWish);
      btn.innerHTML = !isWish
        ? '<i class="bi bi-heart-fill"></i> 찜 취소'
        : '<i class="bi bi-heart"></i> 찜';
    } else if (res.status === 401) {
      window.location.href = `${contextPath}/user/login?redirect=/product/detail?id=${productId}`;
    }
  });
});

document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".review-text").forEach(p => {
    const btn = p.closest(".review-item").querySelector(".toggle-btn");
    if (!btn) return;

    const lineHeight = parseFloat(getComputedStyle(p).lineHeight);
    const maxHeight = lineHeight * 3;

    if (p.scrollHeight <= maxHeight + 2) {
      p.classList.add("no-clamp");
      btn.style.display = "none";
      return;
    }

    btn.addEventListener("click", () => {
      const expanded = p.classList.toggle("expanded");
      if (expanded) {
        p.classList.add("no-clamp");
        btn.textContent = "접기 ▲";
      } else {
        p.classList.remove("no-clamp");
        btn.textContent = "더보기 ▼";
      }
    });
  });
});
