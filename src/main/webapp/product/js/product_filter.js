document.addEventListener("DOMContentLoaded", () => {
  // ✅ 중복 초기화 방지
  if (window.__productFilterInit) return;
  window.__productFilterInit = true;

  const filterForm    = document.querySelector(".product_filter form");
  const priceButtons  = document.querySelectorAll(".price-btn");
  const minInput      = document.getElementById("minPrice");
  const maxInput      = document.getElementById("maxPrice");
  const sidoSelect    = document.getElementById("sido");
  const siggContainer = document.getElementById("siggContainer");
  const contextPath   = window.contextPath || "";

  // ✅ 서버에서 전달된 현재 선택값(둘 다 "ID" 기준)
  const currentSidoId  = (window.serverParams?.sidoId || "").toString();
  const currentSiggId  = (window.serverParams?.siggArea || "").toString();

  // ✅ 페이지 로드 시 기존 지역 자동 복원
  if (currentSidoId) {
    sidoSelect.value = currentSidoId;
    loadSiggList(currentSidoId, currentSiggId);
  }

  // ✅ 시도 선택 시 → 시군구 목록 갱신
  if (sidoSelect) {
    sidoSelect.addEventListener("change", () => {
      const sidoId = sidoSelect.value;
      // 시군구 초기화 후 목록 다시 로드
      siggContainer.innerHTML = "<p class='text-secondary small'>시/군/구를 선택해주세요.</p>";
      loadSiggList(sidoId, "");
    });
  }

  // ✅ 시군구 목록 불러오기 (value=ID로 통일)
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

        // ✅ 기존 선택값 복원 (ID 비교)
        if (idStr === String(selectedSiggId)) {
          wrapper.querySelector("input").checked = true;
        }

        radioList.appendChild(wrapper);
      });

      siggContainer.innerHTML = "";
      siggContainer.appendChild(radioList);

      // ✅ 라디오 클릭 시 자동 제출
      radioList.querySelectorAll('input[name="sigg_area"]').forEach((radio) => {
        radio.addEventListener("change", () => submitOnce());
      });

      // ✅ “더보기” 버튼
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
      console.error(err);
      siggContainer.innerHTML = "<p class='text-danger small'>시군구 정보를 불러오지 못했습니다.</p>";
    }
  }

  // ✅ 카테고리 라디오 → 자동 제출
  document.querySelectorAll('input[name="category"]').forEach((radio) => {
    radio.addEventListener("change", () => submitOnce());
  });

  // ✅ 가격 버튼 → 값만 변경 (자동 제출 X)
  priceButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      priceButtons.forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");

      const value = parseInt(btn.dataset.value, 10);
      minInput.value = 0;
      maxInput.value = value === 0 ? "" : String(value);
    });
  });

  // ✅ “적용하기” 버튼 클릭 → 제출(중복방지)
  document.getElementById("applyPrice")?.addEventListener("click", (e) => {
    e.preventDefault();
    submitOnce();
  });

  // ✅ 중복 제출 방지 helper
  function submitOnce() {
    if (!filterForm) return;
    if (filterForm.dataset.submitting === "1") return;
    filterForm.dataset.submitting = "1";
    filterForm.submit();
  }
});

// ✅ 개별 필터 제거 버튼 (X) 클릭 시 해당 필터만 제거 → 한 번만 제출
document.addEventListener("click", (e) => {
  const btn = e.target.closest(".active-filter .remove-filter");
  if (!btn) return;

  const filterForm = document.querySelector(".product_filter form");
  const type  = btn.dataset.type;

  if (type === "category") {
    document.querySelectorAll('input[name="category"]').forEach(r => r.checked = false);
  } else if (type === "price") {
    document.getElementById("minPrice").value = "";
    document.getElementById("maxPrice").value = "";
  } else if (type === "sido") {
    const sido = document.querySelector("#sido");
    if (sido) sido.value = "";
    const siggContainer = document.getElementById("siggContainer");
    if (siggContainer) siggContainer.innerHTML = "<p class='text-secondary small'>시/군/구를 선택해주세요.</p>";
    // 시군구 라디오 전부 해제
    document.querySelectorAll('input[name="sigg_area"]').forEach(r => r.checked = false);
  } else if (type === "sigg") {
    document.querySelectorAll('input[name="sigg_area"]').forEach(r => r.checked = false);
  }

  if (filterForm) {
    if (filterForm.dataset.submitting === "1") return;
    filterForm.dataset.submitting = "1";
    filterForm.submit();
  }
});

document.getElementById('onlyAvailable')?.addEventListener('change', function(){
  this.form.submit();
});
