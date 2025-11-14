document.addEventListener("DOMContentLoaded", () => {
  if (window.__productFilterInit) return;
  window.__productFilterInit = true;

  const filterForm = document.querySelector(".product_filter form");
  const priceButtons = document.querySelectorAll(".price-btn");
  const minInput = document.getElementById("minPrice");
  const maxInput = document.getElementById("maxPrice");
  const sidoSelect = document.getElementById("sido");
  const siggContainer = document.getElementById("siggContainer");
  const onlyAvailable = document.getElementById("onlyAvailable");
  const contextPath = window.contextPath || "";
  const filterToggleBtn = document.getElementById("filterToggleBtn");
  const filterBox = document.querySelector(".product_filter");

  const currentSidoId = (window.serverParams?.sidoId || "").toString();
  const currentSiggId = (window.serverParams?.siggArea || "").toString();

  function applyInitialFilterState() {
    if (window.innerWidth >= 1026) {
      filterBox.classList.add("expanded");
      filterToggleBtn.style.display = "none";
    } else {
      const saved = localStorage.getItem("filterOpen");
      if (saved === "1") filterBox.classList.add("expanded");
      else filterBox.classList.remove("expanded");
      filterToggleBtn.style.display = "block";
      updateToggleText();
    }
  }

  applyInitialFilterState();

  if (currentSidoId) {
    sidoSelect.value = currentSidoId;
    loadSiggList(currentSidoId, currentSiggId);
  }

  if (sidoSelect) {
    sidoSelect.addEventListener("change", () => {
      const sidoId = sidoSelect.value;
      siggContainer.innerHTML = "<p class='text-secondary small'>시/군/구를 선택해주세요.</p>";
      loadSiggList(sidoId, "");
    });
  }

  async function loadSiggList(sidoId, selectedSiggId) {
    if (!sidoId) {
      siggContainer.innerHTML = "<p class='text-secondary small'>시/군/구를 선택해주세요.</p>";
      return;
    }

    siggContainer.innerHTML = "<p class='text-secondary small'>로딩 중...</p>";

    try {
      const res = await fetch(`${contextPath}/area/sigg?sidoId=${encodeURIComponent(sidoId)}`);
      const siggs = await res.json();

      if (!siggs.length) {
        siggContainer.innerHTML = "<p class='text-secondary small'>시군구 정보가 없습니다.</p>";
        return;
      }

      const radioList = document.createElement("div");
      radioList.className = "d-flex flex-column gap-1";

      siggs.forEach((sigg) => {
        const wrapper = document.createElement("div");
        wrapper.className = "form-check";
        const idStr = String(sigg.id);

        wrapper.innerHTML = `
          <input class="form-check-input" type="radio" name="sigg_area" id="sigg_${idStr}" value="${idStr}">
          <label class="form-check-label" for="sigg_${idStr}">${sigg.name}</label>
        `;

        if (idStr === selectedSiggId) {
          wrapper.querySelector("input").checked = true;
        }

        radioList.appendChild(wrapper);
      });

      siggContainer.innerHTML = "";
      siggContainer.appendChild(radioList);

      radioList.querySelectorAll('input[name="sigg_area"]').forEach((radio) => {
        radio.addEventListener("change", () => submitOnce());
      });

      if (siggs.length > 6) {
        const moreBtn = document.createElement("button");
        moreBtn.type = "button";
        moreBtn.className = "btn btn-link text-decoration-none p-0 mt-1 small text-primary";
        moreBtn.textContent = "더보기";
        moreBtn.addEventListener("click", () => {
          radioList.classList.toggle("expanded");
          moreBtn.textContent = radioList.classList.contains("expanded") ? "접기" : "더보기";
        });
        siggContainer.appendChild(moreBtn);
      }
    } catch (err) {
      siggContainer.innerHTML = "<p class='text-danger small'>시군구 정보를 불러오지 못했습니다.</p>";
    }
  }

  document.querySelectorAll('input[name="category"]').forEach((radio) => {
    radio.addEventListener("change", () => submitOnce());
  });

  if (onlyAvailable) {
    onlyAvailable.addEventListener("change", () => submitOnce());
  }

  priceButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      priceButtons.forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");
      const value = parseInt(btn.dataset.value, 10);
      minInput.value = 0;
      maxInput.value = value === 0 ? "" : String(value);
    });
  });

  const applyPriceBtn = document.getElementById("applyPrice");
  if (applyPriceBtn) {
    applyPriceBtn.addEventListener("click", (e) => {
      e.preventDefault();
      submitOnce();
    });
  }

  document.addEventListener("click", (e) => {
    const btn = e.target.closest(".active-filter .remove-filter");
    if (!btn) return;

    const type = btn.dataset.type;

    if (type === "category") {
      document.querySelectorAll('input[name="category"]').forEach((r) => (r.checked = false));
    } else if (type === "price") {
      minInput.value = "";
      maxInput.value = "";
    } else if (type === "sido") {
      sidoSelect.value = "";
      siggContainer.innerHTML = "<p class='text-secondary small'>시/군/구를 선택해주세요.</p>";
      document.querySelectorAll('input[name="sigg_area"]').forEach((r) => (r.checked = false));
    } else if (type === "sigg") {
      document.querySelectorAll('input[name="sigg_area"]').forEach((r) => (r.checked = false));
    }

    submitOnce();
  });

  if (filterToggleBtn && filterBox) {
    filterToggleBtn.addEventListener("click", () => {
      const isOpen = filterBox.classList.toggle("expanded");
      if (window.innerWidth < 1026) {
        localStorage.setItem("filterOpen", isOpen ? "1" : "0");
      }
      updateToggleText();
    });
  }

  function updateToggleText() {
    if (!filterToggleBtn) return;
    filterToggleBtn.textContent = filterBox.classList.contains("expanded")
      ? "필터 닫기 ▲"
      : "필터 보기 ▼";
  }

  function submitOnce() {
    if (!filterForm) return;
    if (filterForm.dataset.submitting === "1") return;
    if (window.innerWidth < 1026) localStorage.setItem("filterOpen", "1");
    filterForm.dataset.submitting = "1";
    filterForm.submit();
  }

  window.addEventListener("resize", applyInitialFilterState);
});
