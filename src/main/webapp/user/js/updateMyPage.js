// Lucide Icons 초기화
		lucide.createIcons();

		// 에러 메시지 표시 함수
		function displayError(message) {
			const errorDiv = document.getElementById('error-message');
			const errorText = document.getElementById('error-text');
			errorText.textContent = message;
			errorDiv.classList.remove('hidden');
		}

		// 폼 유효성 검사 함수
		function checkForm(e) {
			const form = document.updateMember;
			const password = form.password.value;
			const passwordConfirm = form.password_confirm.value;

			// 필수 필드 확인 (성명, 닉네임, 아이디는 readonly라 제외)
			if (!form.name.value) {
				displayError("성명을 입력하세요.");
				e.preventDefault();
				return false;
			}
			if (!form.nickname.value) {
				displayError("닉네임을 입력하세요.");
				e.preventDefault();
				return false;
			}

			// 비밀번호 확인 로직 (비어있지 않다면 확인)
			if (password || passwordConfirm) {
				if (password !== passwordConfirm) {
					displayError("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
					e.preventDefault();
					return false;
				}
			}

			// 기타 추가 유효성 검사 (생일, 이메일, 전화번호 형식 등)를 여기에 추가하세요.
			// 성공하면 폼 제출
			return true;
		}

		// 프로필 이미지 미리보기 함수 (마이페이지에서 가져옴)
		function previewImage(event) {
			const file = event.target.files[0];
			const reader = new FileReader();

			reader.onload = function(e) {
				const imgElement = document.getElementById('profile-image');
				imgElement.src = e.target.result;
			};

			if (file) {
				reader.readAsDataURL(file);
			}
		}