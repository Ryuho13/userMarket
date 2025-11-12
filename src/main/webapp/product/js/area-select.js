document.addEventListener("DOMContentLoaded", () => {
  const sidoSelect = document.getElementById("sido");
  const siggSelect = document.getElementById("sigg");
  const siggContainer = document.getElementById("siggContainer");

  // ✅ 지역 필터 처리
  if (sidoSelect) {
    sidoSelect.addEventListener("change", async function () {
      const sidoId = this.value;

      // product-form.jsp (드롭다운 방식)
      if (siggSelect) {
        siggSelect.innerHTML = "<option>로딩 중...</option>";

        if (!sidoId) {
          siggSelect.innerHTML = "<option value=''>선택</option>";
          return;
        }

        try {
          const res = await fetch(`${contextPath}/area/sigg?sidoId=${sidoId}`);
          const siggs = await res.json();

          siggSelect.innerHTML = "<option value=''>선택</option>";
          siggs.forEach(sigg => {
            const opt = document.createElement("option");
            opt.value = sigg.id;
            opt.textContent = sigg.name;
            siggSelect.appendChild(opt);
          });
        } catch (err) {
          console.error(err);
          siggSelect.innerHTML = "<option>불러오기 실패</option>";
        }
        return;
      }

      // product-list.jsp (라디오 방식)
      if (siggContainer) {
        siggContainer.innerHTML = "<p class='text-secondary small'>로딩 중...</p>";

        if (!sidoId) {
          siggContainer.innerHTML = "<p class='text-secondary small'>시/군/구를 선택해주세요.</p>";
          return;
        }

        try {
          const res = await fetch(`${contextPath}/area/sigg?sidoId=${sidoId}`);
          const siggs = await res.json();

          if (!siggs.length) {
            siggContainer.innerHTML = "<p class='text-secondary small'>시군구 정보가 없습니다.</p>";
            return;
          }

          const radioList = document.createElement("div");
          radioList.className = "d-flex flex-column gap-1";

          siggs.forEach(sigg => {
            const wrapper = document.createElement("div");
            wrapper.className = "form-check";
            wrapper.innerHTML = `
              <input class="form-check-input sigg-radio" type="radio" name="sigg_area" id="sigg_${sigg.id}" value="${sigg.name}">
              <label class="form-check-label" for="sigg_${sigg.id}">${sigg.name}</label>
            `;
            radioList.appendChild(wrapper);
          });

          siggContainer.innerHTML = "";
          siggContainer.appendChild(radioList);

          // 🔹 추가: 시군구 선택 시 자동 필터 적용
          const filterForm = document.querySelector(".product_filter form");
          document.querySelectorAll(".sigg-radio").forEach(radio => {
            radio.addEventListener("change", () => {
              if (filterForm) filterForm.submit();
            });
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
          console.error(err);
          siggContainer.innerHTML = "<p class='text-danger small'>시군구 정보를 불러오지 못했습니다.</p>";
        }
      }
    });
  }

  // ✅ 가격 필터 처리
  const priceButtons = document.querySelectorAll(".price-btn");
  const minInput = document.getElementById("minPrice");
  const maxInput = document.getElementById("maxPrice");

  if (priceButtons.length > 0) {
    priceButtons.forEach(btn => {
      btn.addEventListener("click", () => {
        priceButtons.forEach(b => b.classList.remove("active"));
        btn.classList.add("active");

        const value = parseInt(btn.dataset.value);
        if (value === 0) {
          minInput.value = 0;
          maxInput.value = 0;
        } else {
          minInput.value = 0;
          maxInput.value = value;
        }
      });
    });
  }
});
