document.addEventListener("DOMContentLoaded", () => {
  const imageInput = document.getElementById('images');
  const previewContainer = document.getElementById('previewContainer');

  if (!imageInput || !previewContainer) return;

  let selectedFiles = [];

  imageInput.addEventListener('change', (event) => {
    const files = Array.from(event.target.files);
    selectedFiles = selectedFiles.concat(files);
    renderPreview();
  });

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
          selectedFiles.splice(index, 1);
          updateInputFiles();
          renderPreview();
        });

        wrapper.appendChild(img);
        wrapper.appendChild(removeBtn);
        previewContainer.appendChild(wrapper);
      };

      reader.readAsDataURL(file);
    });

    updateInputFiles();
  }

  function updateInputFiles() {
    const dataTransfer = new DataTransfer();
    selectedFiles.forEach(file => dataTransfer.items.add(file));
    imageInput.files = dataTransfer.files;
  }
});
