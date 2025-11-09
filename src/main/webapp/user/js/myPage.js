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
  if (typeof initializeIcons === 'function') {
    initializeIcons();
  } else if (window.lucide) {
    // initializeIcons가 없다면 직접 아이콘 초기화
    lucide.createIcons();
  }
});

	// 마이페이지의 경우 기본 탭 설정
	if (document.getElementById('tab-products')) {
		changeTab('products');
	}

	// 회원가입 폼의 경우 onSubmit 이벤트 재연결
	const addForm = document.querySelector('form[name="newUser"]');
	if (addForm) {
		addForm.onsubmit = checkAddForm;
	}

	// 회원수정 폼의 경우 onSubmit 이벤트 재연결
	const updateForm = document.querySelector('form[name="updateUser"]');
	if (updateForm) {
		updateForm.onsubmit = checkUpdateForm;
	};
