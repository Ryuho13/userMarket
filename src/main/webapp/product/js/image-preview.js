document.addEventListener("DOMContentLoaded", () => {
  const imageInput = document.getElementById('images');
  const previewContainer = document.getElementById('previewContainer');

  if (!imageInput || !previewContainer) return;

  imageInput.addEventListener('change', (event) => {
    previewContainer.innerHTML = ''; // 기존 미리보기 초기화
    const files = event.target.files;

    Array.from(files).forEach(file => {
      if (!file.type.startsWith('image/')) return;

      const reader = new FileReader();
      reader.onload = e => {
        const img = document.createElement('img');
        img.src = e.target.result;
        img.className = 'w-32 h-32 object-cover rounded-lg shadow';
        previewContainer.appendChild(img);
      };
      reader.readAsDataURL(file);
    });
  });
});
