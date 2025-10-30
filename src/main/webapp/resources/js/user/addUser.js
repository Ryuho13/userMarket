// Lucide Icons 초기화 (myPage, updateMember에서 사용)
function initializeIcons() {
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
}

// ** alert() 대신 사용할 커스텀 에러 메시지 함수 (addMember, updateMember에서 사용) **
function displayError(message) {
    const errorBox = document.getElementById('error-message');
    const errorText = errorBox.querySelector('span') || errorBox;

    if (errorBox && errorText) {
        errorText.textContent = message;
        errorBox.classList.remove('hidden');
        errorBox.classList.add('flex'); // addMember.jsp 스타일 고려

        // 5초 후 자동 숨김 (addMember.jsp에 있는 기능)
        setTimeout(() => {
            errorBox.classList.add('hidden');
            errorBox.classList.remove('flex');
        }, 5000);
    }
}

// ---------------- 회원가입/수정 폼 관련 함수 ----------------

// 프로필 이미지 미리보기 함수 (myPage, updateMember에서 사용)
function previewImage(event) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = function(e) {
        const imgElement = document.getElementById('profile-image');
        if (imgElement) {
            imgElement.src = e.target.result;
        }
    };

    if (file) {
        reader.readAsDataURL(file);
    }
}

// 폼 유효성 검사 함수 - 회원가입용 (addMember.jsp에서 사용)
function checkAddForm(e) {
    e.preventDefault(); // 기본 폼 제출 동작을 막습니다.

    const form = document.newMember;
    
    // 1. 아이디
    if (!form.id.value.trim()) {
        displayError("아이디를 입력하세요.");
        form.id.focus();
        return;
    }

    // 2. 비밀번호
    if (!form.password.value) {
        displayError("비밀번호를 입력하세요.");
        form.password.focus();
        return;
    }

    // 3. 비밀번호 확인
    if (form.password.value !== form.password_confirm.value) {
        displayError("비밀번호가 일치하지 않습니다.");
        form.password_confirm.focus();
        return;
    }
    
    // 4. 성명
    if (!form.name.value.trim()) {
        displayError("성명을 입력하세요.");
        form.name.focus();
        return;
    }
    
    // 5. 닉네임
    if (!form.nickname.value.trim()) {
        displayError("닉네임을 입력하세요.");
        form.nickname.focus();
        return;
    }


    // 6. 생일 년도
    const birthyy = form.birthyy.value;
    const currentYear = new Date().getFullYear();
    if (birthyy.length !== 4 || isNaN(birthyy) || parseInt(birthyy) < 1900 || parseInt(birthyy) > currentYear) {
        displayError(`유효한 태어난 년도 4자리를 입력하세요 (1900년 ~ ${currentYear}).`);
        form.birthyy.focus();
        return;
    }

    // 7. 생일 월
    if (!form.birthmm.value) {
        displayError("태어난 월을 선택하세요.");
        form.birthmm.focus();
        return;
    }
    
/*    // 8. 생일 일
    const birthdd = form.birthdd.value;
    if (birthdd.length === 0 || isNaN(birthdd) || parseInt(birthdd) < 1 || parseInt(birthdd) > 31) {
        displayError("유효한 태어난 일을 입력하세요 (1~31).");
        form.birthdd.focus();
        return;
    }*/

    // 9. 전화번호 ('-' 없이 숫자만 확인)
    const phoneRegex = /^\d+$/;
    if (!form.phone.value.trim() || !phoneRegex.test(form.phone.value.trim())) {
        displayError("전화번호를 '-' 없이 숫자만 입력하세요.");
        form.phone.focus();
        return;
    }
    
    // 10. 주소
    if (!form.address.value.trim()) {
        displayError("주소를 입력하세요.");
        form.address.focus();
        return;
    }
    

    // 모든 유효성 검사 통과 시, 폼 제출
    displayError("회원 가입 정보를 서버로 전송합니다...");
    setTimeout(() => {
        form.submit();
    }, 500); // 사용자에게 메시지를 보여준 후 제출
}

// 폼 유효성 검사 함수 - 회원수정용 (updateMember.jsp에서 사용)
function checkUpdateForm(e) {
    const form = document.updateMember;
    const password = form.password.value;
    const passwordConfirm = form.password_confirm.value;
    
    // 필수 필드 확인 (성명, 닉네임)
    if (!form.name.value.trim()) {
        displayError("성명을 입력하세요.");
        e.preventDefault();
        return false;
    }
    if (!form.nickname.value.trim()) {
        displayError("닉네임을 입력하세요.");
        e.preventDefault();
        return false;
    }

    // 비밀번호 확인 로직 (새 비밀번호를 입력했다면 확인)
    if (password || passwordConfirm) {
        if (password !== passwordConfirm) {
            displayError("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            e.preventDefault();
            return false;
        }
    }
    
    // 기타 필수 필드에 대한 유효성 검사는 updateMember.jsp에서는 서버에서 처리한다고 가정하고 여기서는 생략합니다.
    // (예: 생일, 이메일, 전화번호 형식 등)

    // 성공하면 폼 제출
    return true;
}

// ---------------- 마이페이지 탭 관련 함수 ----------------

// 탭 변경 함수 (myPage.jsp에서 사용)
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
    
    // 회원가입 폼의 경우 onSubmit 이벤트 재연결
    const addForm = document.querySelector('form[name="newMember"]');
    if (addForm) {
        addForm.onsubmit = checkAddForm;
    }
    
    // 회원수정 폼의 경우 onSubmit 이벤트 재연결
    const updateForm = document.querySelector('form[name="updateMember"]');
    if (updateForm) {
        updateForm.onsubmit = checkUpdateForm;
    }
});
