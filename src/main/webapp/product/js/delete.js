document.addEventListener("click", function(e) {
  const btn = e.target.closest(".delete-image-btn");
  if (!btn) return;

  const imgName = btn.dataset.img;
  const productId = btn.dataset.product;

  if (!confirm("이 이미지를 삭제하시겠습니까?")) return;

  fetch(contextPath + "/product/deleteImage", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
    },
    body: `imgName=${encodeURIComponent(imgName)}&productId=${productId}`
  })
    .then(res => res.text())
    .then(result => {
      console.log("서버 응답:", result);

      if (result === "OK") {
        const wrapper = document.getElementById("img-" + imgName);
        if (wrapper) wrapper.remove();
      } else {
        alert("삭제 실패: " + result);
      }
    })
    .catch(err => {
      console.error("에러:", err);
      alert("서버 오류 발생");
    });
});

function confirmDelete() {
  if (confirm("정말로 이 상품을 삭제하시겠습니까?\n삭제 후에는 복구할 수 없습니다.")) {
    document.getElementById('deleteForm').submit();
  }
}
