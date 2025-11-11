// ================== 유틸 ==================
function displayError(message, duration = 3000) {
  const box = document.getElementById("error-message");
  const textSpan = document.getElementById("error-text");
  if (!box || !textSpan) { console.error("Error box/text not found:", message); return; }
  textSpan.textContent = message;
  box.classList.remove("hidden"); box.classList.add("flex");
  setTimeout(() => { box.classList.add("hidden"); box.classList.remove("flex"); }, duration);
}

function byName(form, name) {
  if (name === 'password' || name === 'password_confirm') return form.elements.namedItem(name);
  const el = form?.elements?.namedItem(name);
  if (!el) throw new Error(`필드 '${name}'를 찾을 수 없습니다.`);
  return el;
}

function initIcons() {
  if (typeof lucide !== "undefined") lucide.createIcons();
}

// ================== 이메일 도메인 직접입력 처리 ==================
function handleEmailDomain() {
  const mail2Select = document.querySelector('select[name="mail2"]');
  if (!mail2Select) return;

  let wrapper = document.getElementById('mail3-input-wrapper');
  let input = null;

  if (!wrapper) {
    wrapper = document.createElement('div');
    wrapper.id = 'mail3-input-wrapper';
    wrapper.className = 'w-full md:w-5/12 flex items-center';
    wrapper.style.display = 'none';

    const relative = document.createElement('div');
    relative.className = 'relative flex-grow';

    input = document.createElement('input');
    input.type = 'text';
    input.name = 'mail3-input';
    input.maxLength = 50;
    input.placeholder = '도메인 직접 입력';
    input.className = 'form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500';

    const btn = document.createElement('button');
    btn.type = 'button';
    btn.title = '도메인 선택으로 돌아가기';
    btn.className = 'absolute right-0 top-0 h-full flex items-center pr-1.5 focus:outline-none';
    btn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" ' +
                    'viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" ' +
                    'stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-chevron-down text-gray-500 hover:text-gray-700">' +
                    '<path d="m6 9 6 6 6-6"/></svg>';

    btn.addEventListener('click', () => toggle(false));

    relative.appendChild(input);
    relative.appendChild(btn);
    wrapper.appendChild(relative);
    mail2Select.after(wrapper);
  } else {
    input = wrapper.querySelector('input[name="mail3-input"]');
  }

  function toggle(direct) {
    if (direct) {
      mail2Select.style.display = 'none';
      wrapper.style.display = 'flex';
      input.required = true;
      mail2Select.required = false;
      mail2Select.value = "";
      setTimeout(() => input.focus(), 0);
    } else {
      mail2Select.style.display = '';
      wrapper.style.display = 'none';
      input.value = '';
      input.required = false;
      mail2Select.required = true;
      if (mail2Select.value === "") {
        mail2Select.value = mail2Select.options[0]?.value || 'naver.com';
      }
    }
  }

  mail2Select.addEventListener('change', function() {
    toggle(this.value === "");
  });

  toggle(mail2Select.value === "");
}

// ================== 주소 AJAX (시/도 → 시/군/구) ==================
function initAreaAjax() {
  const sido = document.getElementById('sido');
  const sigg = document.getElementById('sigg');
  if (!sido || !sigg) return;

  // 컨텍스트 경로
  let ctx = (window.APP_CTX || '');
  if (!ctx) {
    const parts = window.location.pathname.split('/');
    ctx = parts.length > 1 ? ('/' + parts[1]) : '';
  }

  function resetSigg() {
    sigg.innerHTML = '<option value="">시/군/구 선택</option>';
    sigg.disabled = true;
  }
  if (!sido.value) resetSigg();

  sido.addEventListener('change', async function () {
    resetSigg();
    const sid = this.value;
    if (!sid) return;

    try {
      const url = ctx + '/area/sigg?sidoId=' + encodeURIComponent(sid);
      const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
      if (!res.ok) throw new Error('HTTP ' + res.status);
      const data = await res.json();
      data.forEach(it => {
        const opt = document.createElement('option');
        opt.value = it.id;
        opt.textContent = it.name;
        sigg.appendChild(opt);
      });
      sigg.disabled = false;
    } catch (e) {
      console.error('SIGG fetch error:', e);
      alert('시/군/구 목록을 불러오는 중 오류가 발생했습니다.');
    }
  });
}

// ================== 이미지 미리보기 ==================
function previewImage(event) {
  const file = event.target.files && event.target.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = (e) => {
    const img = document.getElementById("profile-image");
    if (img) img.src = e.target.result;
  };
  reader.readAsDataURL(file);
}

// ================== 폼 검증 ==================
function checkForm(e) {
  e.preventDefault();
  const form = e?.currentTarget || e?.target;
  if (!form) { displayError('폼을 찾을 수 없습니다.'); return false; }

  try {
    const $name  = byName(form, 'name');
    const $nick  = byName(form, 'nickname');
    const $pw    = byName(form, 'password');
    const $pw2   = byName(form, 'password_confirm');
    const $phone = byName(form, 'phone');
    const $addr3 = byName(form, 'addr3');
    const sido   = document.getElementById('sido');
    const sigg   = document.getElementById('sigg');

    if (!$name.value.trim()) { displayError('성명을 입력하세요.'); $name.focus(); return false; }
    if (!$nick.value.trim()) { displayError('닉네임을 입력하세요.'); $nick.focus(); return false; }

    if (($pw && $pw.value) || ($pw2 && $pw2.value)) {
      if ($pw.value.length < 6) { displayError('비밀번호는 최소 6자 이상이어야 합니다.'); $pw.focus(); return false; }
      if ($pw.value !== $pw2.value) { displayError('비밀번호가 일치하지 않습니다.'); $pw2.focus(); return false; }
    }

    const phoneRegex = /^\d+$/;
    if (!$phone.value.trim() || !phoneRegex.test($phone.value.trim())) {
      displayError("전화번호를 '-' 없이 숫자만 입력하세요."); $phone.focus(); return false;
    }

    // 이메일(직접입력 모드면 mail2에 주입)
    const $mail1 = byName(form, 'mail1');
    const $mail2 = byName(form, 'mail2');
    const wrapper = document.getElementById('mail3-input-wrapper');
    const m3 = wrapper ? wrapper.querySelector('input[name="mail3-input"]') : null;
    const directMode = wrapper && wrapper.style.display !== 'none';
    if (!$mail1.value.trim()) { displayError('이메일 ID를 입력하세요.'); $mail1.focus(); return false; }
    if (directMode) {
      if (!m3 || !m3.value.trim()) { displayError('이메일 도메인을 직접 입력하세요.'); m3?.focus(); return false; }
      $mail2.value = m3.value.trim();
    } else if ($mail2.value === "") {
      displayError('이메일 도메인을 선택하거나 직접 입력하세요.'); $mail2.focus(); return false;
    }

    // 주소 검증
    if (!sido.value.trim()) { displayError('주소 (시/도)를 선택하세요.'); sido.focus(); return false; }
    if (!sigg.value.trim()) { displayError('주소 (시/군/구)를 선택하세요.'); sigg.focus(); return false; }
    if (!$addr3.value.trim()) { displayError('상세 주소를 입력하세요.'); $addr3.focus(); return false; }

    form.submit();
    return true;

  } catch (err) {
    console.error('Validation Error:', err);
    displayError(err.message || '폼 처리 중 오류가 발생했습니다.');
    return false;
  }
}

// ================== 초기화 ==================
document.addEventListener('DOMContentLoaded', () => {
  initIcons();
  handleEmailDomain();
  initAreaAjax();

  const form = document.querySelector('form[name="updateMyPage"], form[name="updateMember"]');
  if (form) form.addEventListener('submit', checkForm);
});
