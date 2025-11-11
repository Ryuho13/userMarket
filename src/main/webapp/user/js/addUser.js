// ================== 공통 유틸 ==================
function initializeIcons() {
  if (typeof lucide !== "undefined") {
    lucide.createIcons();
  }
}

function displayError(message, duration = 3000) {
  const box = document.getElementById("error-message");
  if (!box) return console.error("Error box not found:", message);
  const span = box.querySelector("span") || box;
  span.textContent = message;
  box.classList.remove("hidden");
  box.classList.add("flex");
  setTimeout(() => {
    box.classList.add("hidden");
    box.classList.remove("flex");
  }, duration);
}

function byName(form, name) {
  const el = form && form.elements && form.elements.namedItem(name);
  if (!el) throw new Error(`필드 '${name}'를 찾을 수 없습니다.`);
  return el;
}

// ================== 폼 검증 ==================
function checkAddForm(e) {
  const form = e?.target || document.forms['newUser'];
  if (!form) { displayError('폼을 찾을 수 없습니다.'); return false; }

  try {
    // 기본 필드
    const $id    = byName(form, 'id');
    const $pw    = byName(form, 'pw');
    const $pw2   = byName(form, 'password_confirm');
    const $name  = byName(form, 'name');
    const $nick  = byName(form, 'nickname');
    const $phone = byName(form, 'phn');
    const $addr3 = byName(form, 'addr3');

    // 이메일
    const $mail1 = byName(form, 'mail1');
    const $mail2 = byName(form, 'mail2');

    // 주소(선택)
    const $sido  = byName(form, 'sidoId');
    const $sigg  = byName(form, 'regionId');

    // 1) 아이디
    if (!$id.value.trim()) { displayError('아이디를 입력하세요.'); $id.focus(); return false; }
    const idRegex = /^[A-Za-z0-9]{6,12}$/;
    if (!idRegex.test($id.value.trim())) {
      displayError("아이디는 영문 또는 숫자 6~12자리로 입력하세요."); $id.focus(); return false;
    }

    // 2) 비밀번호
    if (!$pw.value) { displayError('비밀번호를 입력하세요.'); $pw.focus(); return false; }
    if ($pw.value !== $pw2.value) { displayError('비밀번호가 일치하지 않습니다.'); $pw2.focus(); return false; }

    // 3) 이름/닉네임
    if (!$name.value.trim()) { displayError('성명을 입력하세요.'); $name.focus(); return false; }
    if (!$nick.value.trim()) { displayError('닉네임을 입력하세요.'); $nick.focus(); return false; }

    // 4) 전화번호 숫자만
    const phoneRegex = /^\d+$/;
    if (!$phone.value.trim() || !phoneRegex.test($phone.value.trim())) {
      displayError("전화번호를 '-' 없이 숫자만 입력하세요."); $phone.focus(); return false;
    }

    // 5) 주소 선택/상세
    if (!$sido.value.trim()) { displayError('주소 (도/시)를 선택하세요.'); $sido.focus(); return false; }
    if (!$sigg.value.trim()) { displayError('주소 (시/군/구)를 선택하세요.'); $sigg.focus(); return false; }
    if (!$addr3.value.trim()) { displayError('상세 주소를 입력하세요.'); $addr3.focus(); return false; }

    // 6) 이메일 ID/도메인 체크 (도메인은 handleEmailDomain에서 직접입력 전환 관리)
    if (!$mail1.value.trim()) { displayError('이메일 ID를 입력하세요.'); $mail1.focus(); return false; }
    if ($mail2.value === "") {
      // 직접 입력 모드 -> 동적 input 확인
      const wrap = document.getElementById('mail3-input-wrapper');
      const input = wrap ? wrap.querySelector('input[name="mail3-input"]') : null;
      if (!input || !input.value.trim()) {
        displayError('이메일 도메인을 직접 입력하세요.');
        if (input) input.focus();
        return false;
      }
    }

		// 통과 → 기본 제출 (return true)
		$sigg.disabled = false;   // ✅ 중요: disabled 해제해야 브라우저가 값 전송함
		return true;

  } catch (err) {
    console.error(err);
    displayError(err.message || '폼 처리 중 오류가 발생했습니다.');
    return false;
  }
}

// ================== 이메일 도메인 처리 ==================
function handleEmailDomain() {
  const mail2Select = document.querySelector('select[name="mail2"]');
  if (!mail2Select) return;

  // mail2Select가 들어있는 flex 컨테이너
  const container = mail2Select.closest('.flex.items-center.space-x-2');

  let mail3Wrapper = document.getElementById('mail3-input-wrapper');
  let mail3Input = null;

  // 최초 생성
  if (!mail3Wrapper) {
    mail3Wrapper = document.createElement('div');
    mail3Wrapper.id = 'mail3-input-wrapper';
    mail3Wrapper.className = 'w-2/3 flex items-center space-x-2';
    mail3Wrapper.style.display = 'none';

    const relativeContainer = document.createElement('div');
    relativeContainer.className = 'relative flex-grow';

    const input = document.createElement('input');
    input.type = 'text';
    input.name = 'mail3-input';
    input.maxLength = 50;
    input.placeholder = '도메인 직접 입력';
    input.className = 'w-full p-2 border border-gray-300 rounded-lg input-field';

    const buttonWrapper = document.createElement('div');
    buttonWrapper.className = 'absolute right-0 top-0 h-full flex items-center pr-1.5';

    const button = document.createElement('button');
    button.type = 'button';
    button.title = '도메인 선택으로 돌아가기';
    button.className = 'p-1';
    button.innerHTML =
      '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" ' +
      'viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" ' +
      'stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-chevron-down text-gray-500 hover:text-gray-700">' +
      '<path d="m6 9 6 6 6-6"/></svg>';

    button.addEventListener('click', () => toggleDomainInput(false));

    buttonWrapper.appendChild(button);
    relativeContainer.appendChild(input);
    relativeContainer.appendChild(buttonWrapper);
    mail3Wrapper.appendChild(relativeContainer);

    // select 바로 뒤에 붙이기
    mail2Select.after(mail3Wrapper);
    mail3Input = input;
  } else {
    mail3Input = mail3Wrapper.querySelector('input[name="mail3-input"]');
  }

  function toggleDomainInput(isDirect) {
    if (isDirect) {
      mail2Select.style.display = 'none';
      mail3Wrapper.style.display = 'flex';
      if (mail3Input) {
        mail3Input.required = true;
        setTimeout(() => mail3Input.focus(), 0);
      }
      mail2Select.required = false;
    } else {
      mail2Select.style.display = '';
      mail3Wrapper.style.display = 'none';
      if (mail3Input) {
        mail3Input.value = '';
        mail3Input.required = false;
      }
      mail2Select.required = true;
    }
  }

  // 선택 변경 시 직접입력 토글
  mail2Select.addEventListener('change', function () {
    toggleDomainInput(this.value === "");
  });

  // 초기 상태 반영
  toggleDomainInput(mail2Select.value === "");
}

// ================== 지역 AJAX (시/군/구) ==================
function initAreaAjax() {
  // JSP에서 주입한 전역 컨텍스트 경로 우선
  var ctx = (window.APP_CTX || '');
  if (!ctx) {
    // 비어있다면 URL에서 추론 (/myapp/user/add → /myapp)
    var parts = window.location.pathname.split('/');
    ctx = parts.length > 1 ? ('/' + parts[1]) : '';
  }

  const sido = document.getElementById('sido');
  const sigg = document.getElementById('sigg');
  if (!(sido && sigg)) return;

  function resetSigg() {
    sigg.innerHTML = '<option value="">선택</option>';
    sigg.disabled = true;
  }

  // 초기
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
        opt.value = it.id;      // Gson → {"id":..., "name":...}
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

// ================== 초기화 ==================
document.addEventListener('DOMContentLoaded', () => {
  initializeIcons();
  handleEmailDomain();
  initAreaAjax();
});
