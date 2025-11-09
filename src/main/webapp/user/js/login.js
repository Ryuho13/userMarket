// 에러 메시지 표시 (Bootstrap alert)
function showError(message) {
  let alertBox = document.querySelector(".login-card .alert.alert-danger");
  if (!alertBox) {
    alertBox = document.createElement("div");
    alertBox.className = "alert alert-danger";
    alertBox.setAttribute("role", "alert");
    document.querySelector(".login-card").prepend(alertBox);
  }
  alertBox.textContent = message;
  alertBox.classList.remove("d-none");
}

// 에러 숨기기
function hideError() {
  const alertBox = document.querySelector(".login-card .alert.alert-danger");
  if (alertBox) alertBox.classList.add("d-none");
}

// 아이디 정규식 검사 (영문/숫자 6~12자)
function isValidId(id) {
  return /^[A-Za-z0-9]{6,12}$/.test(id);
}

// 비밀번호 간단 검사
function isValidPw(pw) {
  return typeof pw === "string" && pw.length >= 4;
}

document.addEventListener("DOMContentLoaded", () => {
  const form = document.querySelector("form.form-signin");
  if (!form) return;

  const idInput = form.elements.namedItem("id");
  const pwInput = form.elements.namedItem("pw");
  const submitBtn = form.querySelector("button[type='submit']");

  // 기존 alert 숨기기
  hideError();

  // ---------- [비밀번호 표시 + 아이디 저장 영역 생성] ----------
  (function createOptionRow() {
    const wrapper = pwInput?.closest(".form-floating") || pwInput?.parentElement;
    if (!wrapper) return;

    const rowDiv = document.createElement("div");
    rowDiv.className = "d-flex justify-content-between align-items-center mt-2";

    rowDiv.innerHTML = `
      <div class="form-check">
        <input class="form-check-input" type="checkbox" id="saveIdChk">
        <label class="form-check-label" for="saveIdChk">아이디 저장</label>
      </div>
      <div class="form-check">
        <input class="form-check-input" type="checkbox" id="showPwToggle">
        <label class="form-check-label" for="showPwToggle">비밀번호 표시</label>
      </div>
    `;

    wrapper.after(rowDiv);

    // 비밀번호 표시 토글
    const showPwToggle = rowDiv.querySelector("#showPwToggle");
    showPwToggle.addEventListener("change", (e) => {
      pwInput.type = e.target.checked ? "text" : "password";
    });

    // 아이디 저장 기능 로드
    const saveChk = rowDiv.querySelector("#saveIdChk");
    const savedId = localStorage.getItem("savedUserId");
    if (savedId) {
      idInput.value = savedId;
      saveChk.checked = true;
    }

    // 체크박스 이벤트
    saveChk.addEventListener("change", () => {
      if (saveChk.checked) {
        localStorage.setItem("savedUserId", idInput.value.trim());
      } else {
        localStorage.removeItem("savedUserId");
      }
    });

    // 아이디 입력 시 자동 저장 갱신
    idInput.addEventListener("input", () => {
      if (saveChk.checked) {
        localStorage.setItem("savedUserId", idInput.value.trim());
      }
    });
  })();
  // ------------------------------------------------------------

  // 아이디 input 제약 설정
  idInput.setAttribute("pattern", "[A-Za-z0-9]{6,12}");
  idInput.setAttribute("maxlength", "12");
  idInput.setAttribute("title", "아이디는 영문/숫자 6~12자");

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    hideError();

    const id = (idInput?.value || "").trim();
    const pw = pwInput?.value || "";

    if (!id) {
      showError("아이디를 입력하세요.");
      idInput?.focus();
      return;
    }
    if (!isValidId(id)) {
      showError("아이디는 영문/숫자 6~12자로 입력하세요.");
      idInput?.focus();
      return;
    }
    if (!pw) {
      showError("비밀번호를 입력하세요.");
      pwInput?.focus();
      return;
    }
    if (!isValidPw(pw)) {
      showError("비밀번호는 4자 이상으로 입력하세요.");
      pwInput?.focus();
      return;
    }

    // 중복 제출 방지
    if (submitBtn) {
      submitBtn.disabled = true;
      submitBtn.innerText = "로그인 중...";
    }

    form.submit();
  });
});
