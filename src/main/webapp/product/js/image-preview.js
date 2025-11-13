document.addEventListener("DOMContentLoaded", () => {
  const imageInput = document.getElementById('images');
  const previewContainer = document.getElementById('previewContainer');

  if (!imageInput || !previewContainer) return;

  let selectedFiles = []; // 실제 업로드할 파일 목록 저장용

  imageInput.addEventListener('change', (event) => {
    const files = Array.from(event.target.files);

    // 새로 선택한 파일들을 추가
    selectedFiles = selectedFiles.concat(files);

    renderPreview();
  });

  // 미리보기 + X버튼을 렌더링하는 함수
  function renderPreview() {
    previewContainer.innerHTML = "";

    selectedFiles.forEach((file, index) => {
      const reader = new FileReader();

      reader.onload = (e) => {
        const wrapper = document.createElement("div");
        wrapper.className = "relative inline-block";
        wrapper.style.position = "relative";

        const img = document.createElement("img");
        img.src = e.target.result;
        img.className = "rounded-lg shadow-md";
        img.style.width = "128px";
        img.style.height = "128px";
        img.style.objectFit = "cover";

        // X 버튼
        const removeBtn = document.createElement("button");
        removeBtn.innerHTML = "✕";
        removeBtn.style.cssText = `
          position: absolute;
          top: -8px;
          right: -8px;
          width: 24px;
          height: 24px;
          background: #ff4d4d;
          color: white;
          border: none;
          border-radius: 50%;
          cursor: pointer;
        `;

        removeBtn.addEventListener("click", () => {
          // 실제 배열에서 제거
          selectedFiles.splice(index, 1);

          // input 파일 목록도 다시 구성해야 함
          updateInputFiles();

          // 다시 렌더링
          renderPreview();
        });

        wrapper.appendChild(img);
        wrapper.appendChild(removeBtn);

        previewContainer.appendChild(wrapper);
      };

      reader.readAsDataURL(file);
    });

    // 실제 input에도 리스트 반영
    updateInputFiles();
  }

  // input.files를 selectedFiles로 다시 세팅
  function updateInputFiles() {
    const dataTransfer = new DataTransfer();
    selectedFiles.forEach(file => dataTransfer.items.add(file));
    imageInput.files = dataTransfer.files;
  }
});
