document.addEventListener("DOMContentLoaded", () => {
  const filterForm = document.querySelector(".product_filter form");
  const priceButtons = document.querySelectorAll(".price-btn");
  const minInput = document.getElementById("minPrice");
  const maxInput = document.getElementById("maxPrice");
  const sidoSelect = document.getElementById("sido");
  const siggContainer = document.getElementById("siggContainer");
  const contextPath = window.contextPath || "";

  // ✅ 서버에서 전달된 필터값
  const currentSidoId = window.serverParams?.sidoId || "";
  const currentSiggArea = window.serverParams?.siggArea || "";

  // ✅ 숨김 input 생성 (필터 유지용)
  const hiddenSidoInput = document.createElement("input");
  hiddenSidoInput.type = "hidden";
  hiddenSidoInput.name = "sidoId";
  hiddenSidoInput.value = currentSidoId;
  filterForm.appendChild(hiddenSidoInput);

  const hiddenSiggInput = document.createElement("input");
  hiddenSiggInput.type = "hidden";
  hiddenSiggInput.name = "sigg_area";
  hiddenSiggInput.value = currentSiggArea;
  filterForm.appendChild(hiddenSiggInput);

  // ✅ 페이지 로드 시 기존 지역 자동 복원
  if (currentSidoId) {
    sidoSelect.value = currentSidoId;
    loadSiggList(currentSidoId, currentSiggArea);
  }

  // ✅ 시도 선택 시 → 시군구 목록 갱신
  if (sidoSelect) {
    sidoSelect.addEventListener("change", () => {
      const sidoId = sidoSelect.value;
      hiddenSidoInput.value = sidoId;
      hiddenSiggInput.value = "";
      loadSiggList(sidoId, "");
    });
  }

  // ✅ 시군구 목록 불러오기 함수
  async function loadSiggList(sidoId, selectedSigg) {
    if (!sidoId) {
      siggContainer.innerHTML = "<p class='text-secondary small'>시/군/구를 선택해주세요.</p>";
      return;
    }

    siggContainer.innerHTML = "<p class='text-secondary small'>로딩 중...</p>";

    try {
      const res = await fetch(`${contextPath}/area/sigg?sidoId=${sidoId}`);
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
        wrapper.innerHTML = `
          <input class="form-check-input" type="radio" name="sigg_area" id="sigg_${sigg.id}" value="${sigg.name}">
          <label class="form-check-label" for="sigg_${sigg.id}">${sigg.name}</label>
        `;

        // ✅ 기존 선택값 복원
        if (sigg.name === selectedSigg) {
          wrapper.querySelector("input").checked = true;
        }

        radioList.appendChild(wrapper);
      });

      siggContainer.innerHTML = "";
      siggContainer.appendChild(radioList);

      // ✅ 라디오 클릭 시 자동 필터 적용
      radioList.querySelectorAll('input[name="sigg_area"]').forEach((radio) => {
        radio.addEventListener("change", (e) => {
          hiddenSiggInput.value = e.target.value;
          filterForm.submit();
        });
      });

      // ✅ “더보기” 버튼 추가
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
    radio.addEventListener("change", () => {
      filterForm.submit();
    });
  });

  // ✅ 가격 버튼 → 값만 변경 (자동 제출 X)
  priceButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      priceButtons.forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");

      const value = parseInt(btn.dataset.value);
      minInput.value = 0;
      maxInput.value = value === 0 ? "" : value;
    });
  });

  // ✅ “적용하기” 버튼 클릭 → 제출
  document.getElementById("applyPrice").addEventListener("click", (e) => {
    e.preventDefault();
    filterForm.submit();
  });
});


// ✅ 개별 필터 제거 버튼 (X) 클릭 시 해당 필터만 제거
document.addEventListener("click", (e) => {
  if (e.target.matches(".active-filter .remove-filter")) {
    const targetType = e.target.dataset.type;
    const targetValue = e.target.dataset.value;
    const filterForm = document.querySelector(".product_filter form");

    if (targetType === "category") {
      const categoryRadio = document.querySelector(`input[name="category"][value="${targetValue}"]`);
      if (categoryRadio) categoryRadio.checked = false;
    }

    if (targetType === "price") {
      document.getElementById("minPrice").value = "";
      document.getElementById("maxPrice").value = "";
    }

    if (targetType === "sido") {
      document.querySelector("#sido").value = "";
      const hiddenSido = document.querySelector('input[name="sidoId"]');
      const hiddenSigg = document.querySelector('input[name="sigg_area"]');
      if (hiddenSido) hiddenSido.value = "";
      if (hiddenSigg) hiddenSigg.value = "";
    }

    if (targetType === "sigg") {
      // ✅ 라디오 해제 + hidden input 초기화
      const checkedRadio = document.querySelector('input[name="sigg_area"]:checked');
      if (checkedRadio) checkedRadio.checked = false;

      const hiddenSigg = document.querySelector('input[name="sigg_area"][type="hidden"]');
      if (hiddenSigg) hiddenSigg.value = "";
    }

    // ✅ 해당 필터 제거 후 form 다시 제출
    filterForm.submit();
  }
});
