document.addEventListener("DOMContentLoaded", function () {
  var toggleBtn = document.getElementById("filterToggleBtn");
  var filterPanel = document.querySelector(".product_filter");
  if (!toggleBtn || !filterPanel) return;

  var mq = window.matchMedia("(max-width: 992px)");

  function applyState() {
    if (mq.matches) {
      if (!filterPanel.classList.contains("expanded")) {
        filterPanel.classList.remove("expanded");
      }
      toggleBtn.style.display = "block";
      toggleBtn.textContent = filterPanel.classList.contains("expanded") ? "필터 닫기 ▲" : "필터 보기 ▼";
    } else {
      filterPanel.classList.remove("expanded");
      filterPanel.style.maxHeight = "";
      filterPanel.style.opacity = "";
      toggleBtn.style.display = "none";
    }
  }

  applyState();
  mq.addEventListener("change", applyState);

  toggleBtn.addEventListener("click", function () {
    var willExpand = !filterPanel.classList.contains("expanded");
    if (willExpand) {
      filterPanel.classList.add("expanded");
      toggleBtn.textContent = "필터 닫기 ▲";
    } else {
      filterPanel.classList.remove("expanded");
      toggleBtn.textContent = "필터 보기 ▼";
    }
  });
});
