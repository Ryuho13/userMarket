// 아이콘 초기화
function initializeIcons() {
	if (typeof lucide !== "undefined") {
		lucide.createIcons();
	}
}

// 에러 메시지 출력
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

//회원가입 폼 유효성 검사 
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
		const $phone = byName(form, 'phn');
		// NOTE: $addr은 addr3 (상세 주소)를 의미한다고 가정
		const $addr = byName(form, 'addr3');

		const $mail1 = byName(form, 'mail1');
		const $mail2 = byName(form, 'mail2');
		// mail3Input은 동적으로 생성되므로, ID를 통해 참조 (div wrapper가 아닌 실제 input 요소)
		const $mail3Wrapper = document.getElementById('mail3-input-wrapper');
		const $mail3 = $mail3Wrapper ? $mail3Wrapper.querySelector('input[name="mail3-input"]') : null;

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

		// 3) 주소 (addr1, addr2 필수 체크 추가)
		const $addr1 = byName(form, 'addr1');
		const $addr2 = byName(form, 'addr2');
		if (!$addr1.value.trim()) { displayError('주소 (도/시)를 선택하세요.'); $addr1.focus(); return false; }
		if (!$addr2.value.trim()) { displayError('주소 (시/군/구)를 선택하세요.'); $addr2.focus(); return false; }
		if (!$addr.value.trim()) { displayError('상세 주소를 입력하세요.'); $addr.focus(); return false; }


		// 4) 이메일 도메인 처리 및 Hidden 'em' 설정
		let domPart = '';
		const idPart = ($mail1.value || '').trim();

		// mail3Input wrapper가 화면에 보이는지 확인
		const isDirectInputMode = $mail3Wrapper && $mail3Wrapper.style.display !== 'none';

		if (isDirectInputMode) { // '직접 입력' 모드일 때
			if (!$mail3 || !$mail3.value.trim()) {
				displayError("이메일 도메인을 직접 입력하세요.");
				$mail3.focus();
				return false;
			}
			domPart = $mail3.value.trim();
		} else { // 선택된 도메인 모드일 때
			if ($mail2.value === "") { // '직접 입력'을 선택했지만 모드가 전환되지 않은 경우
				displayError("이메일 도메인을 선택하거나 직접 입력하세요.");
				$mail2.focus();
				return false;
			}
			domPart = $mail2.value;
		}

		// 이메일 ID 필수값 체크
		if (!idPart) { displayError('이메일 ID를 입력하세요.'); $mail1.focus(); return false; }

		// 최종 이메일 주소 합쳐서 hidden 'em' 생성/설정
		let emHidden = form.elements.namedItem('em');
		if (!emHidden) {
			emHidden = document.createElement('input');
			emHidden.type = 'hidden';
			emHidden.name = 'em';
			form.appendChild(emHidden);
		}
		emHidden.value = `${idPart}@${domPart}`;

		// 주소 합치기 → hidden 'addr_detail' 설정
		const a1 = byName(form, 'addr1').value.trim();
		const a2 = byName(form, 'addr2').value.trim();
		const a3 = byName(form, 'addr3').value.trim();
		const fullAddr = [a1, a2, a3].filter(Boolean).join(' ');

		let addrHidden = form.elements.namedItem('addr_detail');
		if (!addrHidden) {
			addrHidden = document.createElement('input');
			addrHidden.type = 'hidden';
			addrHidden.name = 'addr_detail';
			form.appendChild(addrHidden);
		}
		addrHidden.value = fullAddr;



		// 통과 → 제출
		form.submit();
		return true;

	} catch (err) {
		console.error(err);
		displayError(err.message || '폼 처리 중 오류가 발생했습니다.');
		return false;
	}
}

// ---------------- 주소 데이터 및 로직 ----------------
const regions = {
	'서울특별시': ['강남구', '강동구', '강북구', '강서구', '관악구', '광진구', '구로구', '금천구', '노원구', '도봉구', '동대문구', '동작구', '마포구', '서대문구', '서초구', '성동구', '성북구', '송파구', '양천구', '영등포구', '용산구', '은평구', '종로구', '중구', '중랑구'],
	'경기도': ['수원시', '고양시', '용인시', '성남시', '부천시', '화성시', '안산시', '안양시', '평택시', '시흥시', '하남시'],
	'부산광역시': ['강서구', '금정구', '남구', '동구', '동래구', '부산진구', '북구', '사상구', '사하구', '서구', '수영구', '연제구', '영도구', '중구', '해운대구', '기장군'],
	'대구광역시': ['남구', '달서구', '달성군', '동구', '북구', '서구', '수성구', '중구'],
	'인천광역시': ['계양구', '남동구', '동구', '미추홀구', '부평구', '서구', '연수구', '중구', '강화군', '옹진군'],
	'광주광역시': ['광산구', '남구', '동구', '북구', '서구'],
	'대전광역시': ['대덕구', '동구', '서구', '유성구', '중구'],
	'울산광역시': ['남구', '동구', '북구', '울주군', '중구'],
	'세종특별자치시': ['세종시'],
	'강원특별자치도': ['춘천시', '원주시', '강릉시', '동해시', '태백시'],
	'충청북도': ['청주시', '충주시', '제천시', '보은군', '옥천군'],
	'충청남도': ['천안시', '공주시', '보령시', '아산시', '서산시'],
	'전라북도': ['전주시', '군산시', '익산시', '정읍시', '남원시'],
	'전라남도': ['목포시', '여수시', '순천시', '나주시', '광양시'],
	'경상북도': ['포항시', '경주시', '김천시', '안동시', '구미시'],
	'경상남도': ['창원시', '진주시', '통영시', '사천시', '김해시'],
	'제주특별자치도': ['제주시', '서귀포시']
};

document.addEventListener('DOMContentLoaded', function() {
	const addr1Select = document.getElementById('addr1-select');
	const addr2Select = document.getElementById('addr2-select');

	// 1. 도/시 선택 시 시/군/구 목록 업데이트
	addr1Select.addEventListener('change', function() {
		const selectedCity = this.value;
		// 기존 옵션 제거
		addr2Select.innerHTML = '<option value="">시/군/구 선택</option>';

		if (selectedCity && regions[selectedCity]) {
			regions[selectedCity].forEach(function(district) {
				const option = document.createElement('option');
				option.value = district;
				option.textContent = district;
				addr2Select.appendChild(option);
			});
			addr2Select.disabled = false;
		} else {
			addr2Select.disabled = true;
		}
	});

	// 초기 로딩 시 시/군/구 선택 비활성화
	if (!addr1Select.value) {
		addr2Select.disabled = true;
	}
});
