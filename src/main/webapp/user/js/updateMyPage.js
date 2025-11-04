// ===== 에러 메시지 공통 UI =====
function displayError(message) {
  const box = document.getElementById('error-message');
  const span = document.getElementById('error-text') || box;
  if (!box) { alert(message); return; }
  span.textContent = message;
  box.classList.remove('hidden');
}

// ===== 이미지 미리보기 =====
function previewImage(e) {
  const file = e.target.files && e.target.files[0];
  if (!file) return;

  if (!file.type || !file.type.startsWith('image/')) {
    displayError('이미지 파일만 올려주세요.');
    e.target.value = '';
    return;
  }

  const reader = new FileReader();
  reader.onload = (ev) => {
    const img = document.getElementById('profile-image');
    if (img) img.src = ev.target.result;
  };
  reader.readAsDataURL(file);
}

// ===== 입력값 유틸 =====
function val(form, name) {
  const el = form.elements.namedItem(name);
  return el ? el.value.trim() : '';
}

// ===== 폼 유효성 검사 (JSP의 onsubmit="return checkForm(event)" 와 매칭) =====
function checkForm(e) {
  const form = e?.target || document.forms['updateMyPage'];
  if (!form) { displayError('폼을 찾을 수 없습니다.'); e?.preventDefault?.(); return false; }

  const name = val(form, 'name');
  const nickname = val(form, 'nickname');
  const pw = form.elements.namedItem('password')?.value || '';
  const pw2 = form.elements.namedItem('password_confirm')?.value || '';
  const phone = val(form, 'phone');
  const mail1 = val(form, 'mail1');
  const mail2 = val(form, 'mail2');

  // 필수
  if (!name) { displayError('성명을 입력하세요.'); e.preventDefault(); form.elements.namedItem('name')?.focus(); return false; }
  if (!nickname) { displayError('닉네임을 입력하세요.'); e.preventDefault(); form.elements.namedItem('nickname')?.focus(); return false; }

  // 비밀번호 (둘 중 하나라도 입력되면 일치 검사)
  if (pw || pw2) {
    if (pw !== pw2) { displayError('비밀번호와 비밀번호 확인이 일치하지 않습니다.'); e.preventDefault(); form.elements.namedItem('password_confirm')?.focus(); return false; }
    // (선택) 규칙 예시: 8자 이상
    // if (pw.length < 8) { displayError('비밀번호는 8자 이상이어야 합니다.'); e.preventDefault(); form.elements.namedItem('password')?.focus(); return false; }
  }

  // (선택) 전화번호 숫자만
  if (phone && !/^\d{9,11}$/.test(phone)) {
    displayError("전화번호는 숫자만 9~11자리로 입력하세요.");
    e.preventDefault();
    form.elements.namedItem('phone')?.focus();
    return false;
  }

  // (선택) 이메일 조합 검증
  if (mail1 && mail2) {
    const email = `${mail1}@${mail2}`;
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      displayError('이메일 형식을 확인해주세요.');
      e.preventDefault();
      form.elements.namedItem('mail1')?.focus();
      return false;
    }
  }

  return true; // 통과 → 제출
}

// ===== 초기화 (한 번만 실행) =====
document.addEventListener('DOMContentLoaded', () => {
  // 아이콘 (CDN이 있을 때만)
  if (window.lucide && typeof lucide.createIcons === 'function') {
    lucide.createIcons();
  }

  // inline onchange를 JS로도 연결해두면 유지보수 편함(둘 다 있어도 무방)
  const upload = document.getElementById('profile-upload');
  if (upload && !upload._boundPreview) {
    upload.addEventListener('change', previewImage);
    upload._boundPreview = true;
  }

  // 폼 바인딩: JSP에서 onsubmit 이미 연결되어 있으니 필수는 아님
  const form = document.forms['updateMyPage'];
  if (form && !form._boundSubmit) {
    form.addEventListener('submit', checkForm);
    form._boundSubmit = true;
  }
});
