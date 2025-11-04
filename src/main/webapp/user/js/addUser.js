// 아이콘 초기화
function initializeIcons() {
  if (typeof lucide !== "undefined") {
    lucide.createIcons();
  }
}

// 에러 메시지 출력
function displayError(message, duration = 3000) {
  const box = document.getElementById("error-message");
  if (!box) return alert(message);

  const span = box.querySelector("span") || box;
  span.textContent = message;

  box.classList.remove("hidden");
  box.classList.add("flex");

  setTimeout(() => {
    box.classList.add("hidden");
    box.classList.remove("flex");
  }, duration);
}

// 프로필 이미지 미리보기
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

function byName(form, name) {
  const el = form && form.elements && form.elements.namedItem(name);
  if (!el) throw new Error(`필드 '${name}'를 찾을 수 없습니다.`);
  return el;
}

/* 회원가입 폼 유효성 검사 */
function checkAddForm(e) {
	e.preventDefault();

	const form = e?.target || document.forms['newUser'];
	if (!form) { displayError('폼을 찾을 수 없습니다.'); return false; }

	try {
		const $id = byName(form, 'id');
		const $pw = byName(form, 'pw');
		const $pw2 = byName(form, 'password_confirm');
		const $name = byName(form, 'name');
		const $nick = byName(form, 'nickname');
		const $phone = byName(form, 'phone');
		const $addr = byName(form, 'address');
		const $mail1 = byName(form, 'mail1');
		const $mail2 = byName(form, 'mail2');

		// 1) 기본 필수값
		if (!$id.value.trim()) { displayError('아이디를 입력하세요.'); $id.focus(); return false; }
		const idRegex = /^[A-Za-z0-9]{6,12}$/;
		if (!idRegex.test($id.value.trim())) {
			displayError("아이디는 영문 또는 숫자 6~12자리로 입력하세요.");
			$id.focus();
			return false;
		}

		if (!$pw.value) { displayError('비밀번호를 입력하세요.'); $pw.focus(); return false; }
		if ($pw.value !== $pw2.value) { displayError('비밀번호가 일치하지 않습니다.'); $pw2.focus(); return false; }
		if (!$name.value.trim()) { displayError('성명을 입력하세요.'); $name.focus(); return false; }
		if (!$nick.value.trim()) { displayError('닉네임을 입력하세요.'); $nick.focus(); return false; }

		// 2) 전화번호: 숫자만
		const phoneRegex = /^\d+$/;
		if (!$phone.value.trim() || !phoneRegex.test($phone.value.trim())) {
			displayError("전화번호를 '-' 없이 숫자만 입력하세요."); $phone.focus(); return false;
		}

		// 3) 주소
		if (!$addr.value.trim()) { displayError('주소를 입력하세요.'); $addr.focus(); return false; }

		// 4) 이메일 합쳐 hidden 'em' 생성/설정
		let emHidden = form.elements.namedItem('em');
		if (!emHidden) {
			emHidden = document.createElement('input');
			emHidden.type = 'hidden';
			emHidden.name = 'em';
			form.appendChild(emHidden);
		}
		const idPart = ($mail1.value || '').trim();
		const domPart = ($mail2.value || '').trim();
		emHidden.value = (idPart && domPart) ? `${idPart}@${domPart}` : '';

		// 통과 → 제출
		form.submit();
		return true;

	} catch (err) {
		console.error(err);
		displayError(err.message || '폼 처리 중 오류가 발생했습니다.');
		return false;
	}
}

/* 회원수정 폼 유효성 검사 */
function checkUpdateForm(e) {
	const form = e?.target || document.forms['updateUser'];
	if (!form) { displayError('폼을 찾을 수 없습니다.'); e.preventDefault(); return false; }

	try {
		const $name = byName(form, 'name');
		const $nick = byName(form, 'nickname');
		const $pw = form.elements.namedItem('password');
		const $pw2 = form.elements.namedItem('password_confirm');

		if (!$name.value.trim()) { displayError('성명을 입력하세요.'); e.preventDefault(); $name.focus(); return false; }
		if (!$nick.value.trim()) { displayError('닉네임을 입력하세요.'); e.preventDefault(); $nick.focus(); return false; }

		if ($pw && $pw2 && ($pw.value || $pw2.value)) {
			if ($pw.value !== $pw2.value) {
				displayError('비밀번호와 비밀번호 확인이 일치하지 않습니다.');
				e.preventDefault(); $pw2.focus(); return false;
			}
		}
		return true;
	} catch (err) {
		console.error(err);
		displayError(err.message || '폼 처리 중 오류가 발생했습니다.');
		e.preventDefault(); return false;
	}
}

/* 바인딩 (inline 없어도 동작) */
document.addEventListener('DOMContentLoaded', () => {
	const addForm = document.forms['newUser'];
	if (addForm) addForm.addEventListener('submit', checkAddForm);

	const updForm = document.forms['updateUser'];
	if (updForm) updForm.addEventListener('submit', checkUpdateForm);
});