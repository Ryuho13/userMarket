const btn = document.getElementById("wishBtn");

if (btn) {
  btn.addEventListener("click", async function () {
    const productId = this.dataset.productId;

    try {
      const res = await fetch(window.contextPath + "/product/wishlist", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: "productId=" + encodeURIComponent(productId)
      });

      if (!res.ok) return;

      const data = await res.json();
      const wished = data.isWished;

      this.dataset.wish = wished;

      const icon = this.querySelector("i");
      if (wished) {
        icon.classList.remove("bi-heart");
        icon.classList.add("bi-heart-fill", "text-danger");
      } else {
        icon.classList.add("bi-heart");
        icon.classList.remove("bi-heart-fill", "text-danger");
      }
    } catch (e) {
      console.error(e);
    }
  });
}
