// area-select.js

document.addEventListener("DOMContentLoaded", () => {
  const sidoSelect = document.getElementById("sido");
  const siggSelect = document.getElementById("sigg");

  if (!sidoSelect || !siggSelect) return;

  sidoSelect.addEventListener("change", function () {
    const sidoId = this.value;

    siggSelect.innerHTML = "<option>로딩 중...</option>";

    if (!sidoId) {
      siggSelect.innerHTML = "<option value=''>선택</option>";
      return;
    }

	fetch(`${contextPath}/area/sigg?sidoId=${sidoId}`)
      .then(res => res.json())
      .then(data => {
        siggSelect.innerHTML = "<option value=''>선택</option>";
        data.forEach(sigg => {
          const opt = document.createElement("option");
          opt.value = sigg.id;
          opt.textContent = sigg.name;
          siggSelect.appendChild(opt);
        });
      })
      .catch(() => {
        siggSelect.innerHTML = "<option>불러오기 실패</option>";
      });
  });
});
