
// ---------- JavaScript Logic ----------

// 전역 변수 설정: 주소 데이터
const regions = {
    '서울특별시': ['강남구', '강동구', '강북구', '강서구', '관악구', '광진구', '구로구', '금천구', '노원구', '도봉구', '동대문구', '동작구', '마포구', '서대문구', '서초구', '성동구', '성북구', '송파구', '양천구', '영등포구', '용산구', '은평구', '종로구', '중구', '중랑구'],
    '경기도': ['수원시', '고양시', '용인시', '성남시', '부천시', '화성시', '안산시', '안양시', '평택시', '시흥시'],
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

/**
 * 폼 요소 이름으로 접근하는 헬퍼 함수
 * @param {HTMLFormElement} form 폼 요소
 * @param {string} name 요소의 name 속성 값
 * @returns {HTMLElement} 찾은 폼 요소
 */
function byName(form, name) {
  // name이 'password'이거나 'password_confirm'인 경우, optional이므로 예외 처리하지 않음
  if (name === 'password' || name === 'password_confirm') {
      return form.elements.namedItem(name);
  }
  const el = form && form.elements && form.elements.namedItem(name);
  if (!el) {
     console.error(`필드 '${name}'를 찾을 수 없습니다.`);
     throw new Error(`필드 '${name}'를 찾을 수 없습니다.`);
  }
  return el;
}

/**
 * 에러 메시지를 표시하는 함수
 * @param {string} message 표시할 메시지
 * @param {number} duration 메시지 유지 시간 (밀리초)
 */
function displayError(message, duration = 3000) {
  const box = document.getElementById("error-message");
  const textSpan = document.getElementById("error-text");
  
  if (!box || !textSpan) return console.error("Error box or text element not found:", message);

  textSpan.textContent = message;

  box.classList.remove("hidden");
  box.classList.add("flex"); // Tailwind flex 클래스를 사용하여 표시

  setTimeout(() => {
    box.classList.add("hidden");
    box.classList.remove("flex");
  }, duration);
}

/**
 * 프로필 이미지 미리보기 함수
 * @param {Event} event file input change 이벤트
 */
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

/**
 * 주소(도/시) 선택 시 시/군/구 목록을 업데이트하는 로직
 */
function setupAddressSelects() {
    const addr1Select = document.getElementById('addr1-select');
    const addr2Select = document.getElementById('addr2-select');
    
    if (!addr1Select || !addr2Select) return; 
    
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
    
    // 초기 로딩 시 시/군/구 선택 비활성화 (주소 데이터가 로드된 상태가 아니라고 가정)
    if (!addr1Select.value) {
        addr2Select.disabled = true;
    }
}


// ---------------- 이메일 도메인 처리 로직 ----------------

/**
 * 이메일 도메인 선택 시 "직접 입력"을 처리하기 위한 로직
 */
function handleEmailDomain() {
    const mail2Select = document.querySelector('select[name="mail2"]');
    
    // mail2Select 요소의 부모를 찾습니다.
    const container = mail2Select?.closest('.flex.space-x-2.items-center');
    if (!container) return; 

    // mail2Select와 동일한 너비를 차지하는 mail3Wrapper를 생성/찾기
    let mail3Wrapper = document.getElementById('mail3-input-wrapper');
    let mail3Input = null; 

    // 1. mail3Input wrapper가 없으면 생성하고 container에 추가
    if (!mail3Wrapper) {
        mail3Wrapper = document.createElement('div');
        mail3Wrapper.id = 'mail3-input-wrapper';
        // mail2Select와 같은 너비 클래스를 사용하고 flex로 배치
        mail3Wrapper.className = 'w-full md:w-5/12 flex items-center'; 
        mail3Wrapper.style.display = 'none'; 

        const relativeContainer = document.createElement('div');
        relativeContainer.className = 'relative flex-grow'; 
        
        const input = document.createElement('input');
        input.type = 'text';
        input.name = 'mail3-input';
        input.maxLength = 50;
        input.placeholder = '도메인 직접 입력';
        // focus 스타일을 맞추기 위해 class를 적용
        input.className = 'form-input w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500';
        mail3Input = input; 

        const button = document.createElement('button');
        button.type = 'button';
        button.title = '도메인 선택으로 돌아가기';
        button.className = 'absolute right-0 top-0 h-full flex items-center pr-1.5 focus:outline-none';
        // Lucide Chevron Down 아이콘을 인라인 SVG로 사용
        button.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-chevron-down text-gray-500 hover:text-gray-700 transition duration-150"><path d="m6 9 6 6 6-6"/></svg>';
        
        relativeContainer.appendChild(input);
        relativeContainer.appendChild(button);
        mail3Wrapper.appendChild(relativeContainer);
        
        // mail2Select 옆에 mail3Wrapper를 추가
        mail2Select.after(mail3Wrapper);
        
        // 버튼 클릭 이벤트: mail2Select를 다시 보이게 함
        button.addEventListener('click', () => {
            toggleDomainInput(false); 
        });

    } else {
        mail3Input = mail3Wrapper.querySelector('input[name="mail3-input"]');
    }
    
    // 토글 함수 정의
    function toggleDomainInput(isDirectInput) {
        if (isDirectInput) {
            mail2Select.style.display = 'none';
            mail3Wrapper.style.display = 'flex'; 
            mail3Input.required = true;
            mail2Select.required = false;
            mail2Select.value = ""; // select 값을 "직접 입력"으로 고정
            setTimeout(() => {
                mail3Input.focus();
            }, 0); 
        } else {
            mail2Select.style.display = ''; // 기본 display 속성으로 복원
            mail3Wrapper.style.display = 'none';
            mail3Input.value = ''; // 값 초기화
            mail3Input.required = false;
            mail2Select.required = true;
            // Select가 '직접 입력' 상태인 경우 기본값으로 돌림
            if(mail2Select.value === "") {
                mail2Select.value = mail2Select.options[0].value;
            }
        }
    }

    // mail2가 변경될 때마다 실행
    mail2Select.addEventListener('change', function() {
        const isDirect = this.value === "";
        toggleDomainInput(isDirect);
    });

    // 초기 로드 시 상태 설정 (DB에서 로드한 초기값에 따라)
    const isDirectInitially = mail2Select.value === "";
    if (isDirectInitially) {
        toggleDomainInput(true);
    } else {
        mail2Select.style.display = '';
        mail3Wrapper.style.display = 'none';
        mail2Select.required = true;
    }
}


/**
 * 회원 정보 수정 폼 유효성 검사 및 제출 처리
 * onsubmit="return checkForm(event)"와 연결됩니다.
 * @param {Event} e 폼 제출 이벤트
 * @returns {boolean} 유효성 검사 통과 여부
 */
function checkForm(e) {
	e.preventDefault();
	
	const form = e?.target || document.forms['updateMember'];
	if (!form) { displayError('폼을 찾을 수 없습니다.'); return false; }

	try {
		const $name = byName(form, 'name');
		const $nick = byName(form, 'nickname');
		const $pw = byName(form, 'password');
		const $pw2 = byName(form, 'password_confirm');
		const $phone = byName(form, 'phone');
		
		const $mail1 = byName(form, 'mail1'); 
		const $mail2 = byName(form, 'mail2'); 
        const $mail3Wrapper = document.getElementById('mail3-input-wrapper');
        const $mail3 = $mail3Wrapper ? $mail3Wrapper.querySelector('input[name="mail3-input"]') : null; 

		const $addr1 = byName(form, 'addr1'); 
		const $addr2 = byName(form, 'addr2'); 
		const $addr3 = byName(form, 'addr3');
		
		
		// 1) 필수값 검사
		if (!$name.value.trim()) { displayError('성명을 입력하세요.'); $name.focus(); return false; }
		if (!$nick.value.trim()) { displayError('닉네임을 입력하세요.'); $nick.focus(); return false; }
		
		// 2) 비밀번호 수정 검사 (비밀번호는 선택사항이지만 입력했다면 확인해야 함)
		if ($pw.value || $pw2.value) {
			if ($pw.value.length < 6) { displayError('비밀번호는 최소 6자 이상이어야 합니다.'); $pw.focus(); return false; }
			if ($pw.value !== $pw2.value) { displayError('비밀번호와 비밀번호 확인이 일치하지 않습니다.'); $pw2.focus(); return false; }
		}

		// 3) 전화번호: 숫자만
		const phoneRegex = /^\d+$/;
		if (!$phone.value.trim() || !phoneRegex.test($phone.value.trim())) {
			displayError("전화번호를 '-' 없이 숫자만 입력하세요."); $phone.focus(); return false;
		}

		// 4) 주소 유효성 검사
		if (!$addr1.value.trim()) { displayError('주소 (도/시)를 선택하세요.'); $addr1.focus(); return false; }
		if (!$addr2.value.trim()) { displayError('주소 (시/군/구)를 선택하세요.'); $addr2.focus(); return false; }
		if (!$addr3.value.trim()) { displayError('상세 주소를 입력하세요.'); $addr3.focus(); return false; }


		// 5) 이메일 도메인 처리 및 Hidden 'em' 설정
        let domPart = '';
        const idPart = ($mail1.value || '').trim();
        
        const isDirectInputMode = $mail3Wrapper && $mail3Wrapper.style.display !== 'none';

        if (isDirectInputMode) { 
            if (!$mail3 || !$mail3.value.trim()) {
                displayError("이메일 도메인을 직접 입력하세요.");
                $mail3.focus(); 
                return false;
            }
            domPart = $mail3.value.trim();
        } else { 
             if ($mail2.value === "") { 
                 displayError("이메일 도메인을 선택하거나 직접 입력으로 전환하세요.");
                 $mail2.focus();
                 return false;
             }
            domPart = $mail2.value;
        }

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


		// 통과 → 제출
		form.submit();
		return true;

	} catch (err) {
		console.error('Validation Error:', err);
		displayError(err.message || '폼 처리 중 오류가 발생했습니다.'); 
		return false;
	}
}


// 전역 이벤트 리스너: 페이지 로드 후 실행
document.addEventListener('DOMContentLoaded', () => {
    // Lucide 아이콘 초기화
    if (typeof lucide !== "undefined") {
        lucide.createIcons();
    }
    
    // 주소 선택 로직 설정
    setupAddressSelects();
    
    // 이메일 도메인 로직 설정
    handleEmailDomain(); 
    
    // 폼 제출 이벤트 바인딩 (HTML에서 inline으로 정의되어 있지만, 재확인)
    const updateForm = document.forms['updateMember'];
    if (updateForm) {
        updateForm.onsubmit = checkForm;
    }
});