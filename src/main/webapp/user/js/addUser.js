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
		const $phone = byName(form, 'phone');
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


		// 통과 → 제출
		form.submit();
		return true;

	} catch (err) {
		console.error(err);
		displayError(err.message || '폼 처리 중 오류가 발생했습니다.');
		return false;
	}
}

// 회원수정 폼 유효성 검사 
function checkUpdateForm(e) {
	e.preventDefault();
	const form = e?.target || document.forms['updateUser'];
	if (!form) { displayError('폼을 찾을 수 없습니다.'); return false; }

	try {
		const $name = byName(form, 'name');
		const $nick = byName(form, 'nickname');
		const $pw = form.elements.namedItem('password');
		const $pw2 = form.elements.namedItem('password_confirm');

		if (!$name.value.trim()) { displayError('성명을 입력하세요.'); $name.focus(); return false; }
		if (!$nick.value.trim()) { displayError('닉네임을 입력하세요.'); $nick.focus(); return false; }

		if ($pw && $pw2 && ($pw.value || $pw2.value)) {
			if ($pw.value !== $pw2.value) {
				displayError('비밀번호와 비밀번호 확인이 일치하지 않습니다.');
				$pw2.focus(); return false;
			}
		}
		form.submit();
		return true;
	} catch (err) {
		console.error(err);
		displayError(err.message || '폼 처리 중 오류가 발생했습니다.');
		return false;
	}
}

// 바인딩 (inline 없어도 동작) 
document.addEventListener('DOMContentLoaded', () => {
	const addForm = document.forms['newUser'];
	if (addForm) addForm.addEventListener('submit', checkAddForm);

	const updForm = document.forms['updateUser'];
	if (updForm) updForm.addEventListener('submit', checkUpdateForm);
});


// ---------------- 마이페이지 ----------------

// 탭 변경 
function changeTab(tabName) {
	// 모든 탭 버튼 스타일 초기화
	document.querySelectorAll('.tab-button').forEach(button => {
		button.classList.remove('text-green-500', 'border-green-500');
		button.classList.add('text-gray-500', 'border-transparent', 'hover:border-gray-300');
	});

	// 모든 탭 콘텐츠 숨김
	document.querySelectorAll('.tab-content').forEach(content => {
		content.classList.add('hidden');
	});

	// 선택된 탭 활성화
	const selectedButton = document.getElementById(`tab-${tabName}`);
	if (selectedButton) {
		selectedButton.classList.add('text-green-500', 'border-green-500');
		selectedButton.classList.remove('text-gray-500', 'border-transparent', 'hover:border-gray-300');
	}

	// 선택된 콘텐츠 표시
	const selectedContent = document.getElementById(`content-${tabName}`);
	if (selectedContent) {
		selectedContent.classList.remove('hidden');
	}
}


// 전역 이벤트 리스너: 페이지 로드 후 실행
document.addEventListener('DOMContentLoaded', () => {
	initializeIcons();

	// 마이페이지의 경우 기본 탭 설정
	if (document.getElementById('tab-products')) {
		changeTab('products');
	}
    
    // --- 필수 수정: 이메일 도메인 입력 처리 초기화 호출 ---
    handleEmailDomain(); 
    // ----------------------------------------------------

	// 회원가입 폼의 경우 onSubmit 이벤트 재연결
	const addForm = document.querySelector('form[name="newUser"]');
	if (addForm) {
		addForm.onsubmit = checkAddForm;
	}

	// 회원수정 폼의 경우 onSubmit 이벤트 재연결
	const updateForm = document.querySelector('form[name="updateUser"]');
	if (updateForm) {
		updateForm.onsubmit = checkUpdateForm;
		}
});

// ---------------- 주소 데이터 및 로직 ----------------
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


// ---------------- 이메일 도메인 처리 (수정됨) ----------------

// 이메일 도메인 선택 시 직접 입력 처리 함수
function handleEmailDomain() {
    const mail2Select = document.querySelector('select[name="mail2"]');
    
    // mail2Select 요소의 부모(flex items-center space-x-2)를 찾습니다.
    const container = mail2Select?.closest('.flex.items-center.space-x-2');
    if (!container) return; // 컨테이너가 없으면 실행 중지

    let mail3Wrapper = document.getElementById('mail3-input-wrapper');
    let mail3Input = null; // 실제 input 요소

    // 1. mail3Input wrapper가 없으면 생성하고 container에 추가 (display: none 상태로)
    if (!mail3Wrapper) {
        // <div id="mail3-input-wrapper" class="w-2/3 flex items-center space-x-2 hidden">
        mail3Wrapper = document.createElement('div');
        mail3Wrapper.id = 'mail3-input-wrapper';
        // select 요소와 동일한 w-2/3 클래스를 유지하면서 flex 레이아웃으로 변경
        mail3Wrapper.className = 'w-2/3 flex items-center space-x-2'; 
        mail3Wrapper.style.display = 'none';

        const input = document.createElement('input');
        input.type = 'text';
        input.name = 'mail3-input';
        input.maxLength = 50;
        input.placeholder = '도메인 직접 입력';
        // 버튼 공간 확보를 위해 너비를 줄임 (예: w-3/4)
        input.className = 'w-full p-2 border border-gray-300 rounded-lg input-field';
        
        // 버튼을 감싸는 div (버튼 클릭 시 mail2Select 표시)
        const buttonWrapper = document.createElement('div');
        buttonWrapper.className = 'absolute right-0 top-0 h-full flex items-center pr-1.5';

        const button = document.createElement('button');
        button.type = 'button';
        // chevron-down 아이콘 (lucide 아이콘을 인라인 SVG로 사용)
        button.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-chevron-down text-gray-500 hover:text-gray-700 transition duration-150"><path d="m6 9 6 6 6-6"/></svg>';
        button.className = 'p-1';
        button.title = '도메인 선택으로 돌아가기';
        
        buttonWrapper.appendChild(button);
        
        // input과 button을 포함할 상대적 위치의 컨테이너
        const relativeContainer = document.createElement('div');
        // w-full 클래스를 삭제하고 flex item의 너비를 상속받도록 함.
        relativeContainer.className = 'relative flex-grow'; 
        relativeContainer.appendChild(input);
        relativeContainer.appendChild(buttonWrapper);


        // mail3Wrapper는 w-2/3 이므로, 그 안에 input(w-full)을 넣고, select와 동일한 너비를 차지하도록 조정
        // 여기서는 mail2Select와 mail3Wrapper가 같은 w-2/3 공간을 차지하도록 토글만 합니다.
        mail3Wrapper.appendChild(relativeContainer);
        
        // mail2Select 옆에 mail3Wrapper를 추가
        mail2Select.after(mail3Wrapper);
        
        // 실제 input 요소 참조 업데이트
        mail3Input = input;

        // 버튼 클릭 이벤트: mail2Select를 다시 보이게 함
        button.addEventListener('click', () => {
            // mail2를 보이게 하고, mail3Wrapper를 숨김
            toggleDomainInput(false); 
        });

    } else {
        // 이미 생성된 경우 input 요소 참조 업데이트
        mail3Input = mail3Wrapper.querySelector('input[name="mail3-input"]');
    }
    
    // 토글 함수 정의
    function toggleDomainInput(isDirectInput) {
        // isDirectInput이 true면 mail2 숨김/mail3 보임
        if (isDirectInput) {
            mail2Select.style.display = 'none';
            // mail3Wrapper의 display를 'flex'로 변경 (생성 시 'flex'를 사용했으므로)
            mail3Wrapper.style.display = 'flex'; 
            mail3Input.required = true;
            mail2Select.required = false;
            // 포커스
            setTimeout(() => {
                mail3Input.focus();
            }, 0); 
        } else {
            // mail2 보임/mail3 숨김
            // **수정된 부분: display를 빈 문자열로 설정하여 select의 기본 display 속성(대부분 inline-block 또는 block)을 복원**
            mail2Select.style.display = ''; 
            mail3Wrapper.style.display = 'none';
            mail3Input.value = ''; // 값 초기화
            mail3Input.required = false;
            mail2Select.required = true;
            // ************ 이 부분을 제거하여 사용자가 자유롭게 선택하도록 합니다. ************
            // mail2Select를 초기값 ('naver.com')으로 돌려놓기 (선택하지 않은 상태를 방지)
            // mail2Select.value = mail2Select.options[0].value;
        }
    }


    // mail2가 변경될 때마다 실행
    mail2Select.addEventListener('change', function() {
        // '직접 입력' 옵션 선택 시 (value="")
        const isDirect = this.value === "";
        toggleDomainInput(isDirect);
    });

    // 초기 로드 시 상태 설정
    const isDirectInitially = mail2Select.value === "";
    
    // 만약 mail2Select의 초기값이 '직접 입력'이면, 직접 입력 모드로 시작
    if (isDirectInitially) {
        toggleDomainInput(true);
    } else {
        // 기본 상태에서는 mail2Select 보이고, mail3Wrapper 숨김
        mail2Select.style.display = ''; // 기본값으로 설정
        mail3Wrapper.style.display = 'none';
        mail2Select.required = true;
    }
}
