
document.addEventListener("DOMContentLoaded", function () {
  const btn = document.getElementById("wishBtn");
  if (!btn) return;

  btn.addEventListener("click", async () => {
    const productId = btn.dataset.productId;
    const isWish = btn.dataset.wish === "true";

    try {
      const res = await fetch(`${window.contextPath || ''}/product/wishlist`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({
          productId: productId,
          isWish: isWish
        })
      });

      if (!res.ok) {
        if (res.status === 401) {
          alert("로그인이 필요합니다.");
          window.location.href = `${window.contextPath || ''}/user/login?redirect=/product/detail?id=${productId}`;
          return;
        }
        throw new Error("요청 실패");
      }

      const data = await res.json();
      if (data.success) {
        btn.dataset.wish = data.isWished;
        const icon = btn.querySelector("i");

        if (data.isWished) {
          icon.classList.remove("bi-heart");
          icon.classList.add("bi-heart-fill", "text-danger");
        } else {
          icon.classList.remove("bi-heart-fill", "text-danger");
          icon.classList.add("bi-heart");
        }
      }
    } catch (err) {
      console.error(err);
      alert("찜 처리 중 오류가 발생했습니다.");
    }
  });
});

