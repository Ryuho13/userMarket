document.addEventListener("DOMContentLoaded", () => {

  const contextPath = document.body.dataset.contextPath;

  const sidoSelect = document.getElementById("sido");
  const siggContainer = document.getElementById("siggContainer");

  if (!sidoSelect || !siggContainer) return;

  async function loadSigg(sidoId) {
    if (!sidoId) {
      siggContainer.innerHTML = `<p class="text-secondary small">시/군/구를 선택해주세요.</p>`;
      return;
    }

    const res = await fetch(`${contextPath}/product/sigg?sidoId=${sidoId}`);
    if (!res.ok) {
      siggContainer.innerHTML = `<p class="text-secondary small">정보를 불러오지 못했습니다.</p>`;
      return;
    }

    const list = await res.json();

    if (!list.length) {
      siggContainer.innerHTML = `<p class="text-secondary small">등록된 구/군이 없습니다.</p>`;
      return;
    }

    let html = "";
    list.forEach(sigg => {
      html += `
      <div class="form-check mb-1">
        <input class="form-check-input"
               type="radio"
               name="sigg_area"
               id="sigg_${sigg.id}"
               value="${sigg.id}"
               ${serverParams.siggArea == sigg.id ? 'checked' : ''}>
        <label class="form-check-label" for="sigg_${sigg.id}">
          ${sigg.name}
        </label>
      </div>
      `;
    });

    siggContainer.innerHTML = html;
  }

  if (serverParams.sidoId) {
    loadSigg(serverParams.sidoId);
  }

  sidoSelect.addEventListener("change", function () {
    loadSigg(this.value);
  });

});
