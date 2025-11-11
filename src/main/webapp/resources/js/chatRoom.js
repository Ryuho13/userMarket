let ws;

// 페이지 로드 시 실행될 초기화 함수
window.onload = function() {
  const roomIdInput = document.getElementById("roomId");

  if (!roomIdInput) {
    console.error("채팅방 정보를 찾을 수 없습니다. (roomId 요소가 없음)");
    return;
  }

  const roomId = roomIdInput.value;
  
  if(roomId && currentUserId) {
    connect(roomId, currentUserId);
  }

  const msgInput = document.getElementById("msg");
  const sendBtn = document.getElementById("sendBtn");
  const imageUploadInput = document.getElementById("imageUpload");
  const chatBox = document.getElementById("chatBox"); // chatBox를 여기서 가져옴

  // chatBox가 없으면 이후 로직을 실행하지 않음
  if (!chatBox) {
      console.log("chatBox 요소를 찾을 수 없어 일부 기능이 비활성화됩니다.");
      return; 
  }

  // 엔터 키로 메시지 전송 (Shift+Enter는 줄바꿈)
  msgInput.addEventListener("keydown", function(e) {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      sendTextMessage();
    }
  });

  // 보내기 버튼 클릭으로 메시지 전송
  sendBtn.addEventListener("click", function() {
    sendTextMessage();
  });

  // 이미지 파일 선택 시 업로드 처리
  imageUploadInput.addEventListener("change", function(e) {
    const file = e.target.files[0];
    if (file) {
      uploadImage(file);
    }
    e.target.value = null;
  });

  // 페이지 로드 시 스크롤을 가장 아래로 이동
  chatBox.scrollTop = chatBox.scrollHeight;

  // ===== 이미지 확대 모달 기능 초기화 =====
  initializeImageModal(chatBox);
};

// WebSocket 서버에 연결하는 함수
function connect(roomId, userId) {
  const contextPath = document.body.dataset.contextPath || '';
  const wsUrl = `ws://${window.location.host}${contextPath}/chatSocket/${roomId}/${userId}`;
  console.log("WebSocket 연결 시도:", wsUrl);

  ws = new WebSocket(wsUrl);

  ws.onopen = () => console.log("✅ WebSocket 연결 성공");
  ws.onclose = () => console.log("🔌 WebSocket 연결 종료");
  ws.onerror = (e) => console.error("❌ WebSocket 오류:", e);
  
  ws.onmessage = (event) => {
    try {
      const messageData = JSON.parse(event.data);
      appendMessage(messageData);
    } catch (e) {
      console.error("수신 데이터 파싱 오류:", event.data, e);
    }
  };
}

// 텍스트 메시지를 전송하는 함수
function sendTextMessage() {
  const input = document.getElementById("msg");
  const msg = input.value.trim();

  if (msg === "") return;

  sendMessage(msg);
  input.value = "";
  input.focus();
}

// 이미지 메시지를 전송하는 함수
function sendImageMessage(imageUrl) {
    const imageMsg = `IMG::${imageUrl}`;
    sendMessage(imageMsg);
}

// 서버에 메시지를 전송하는 공통 함수
function sendMessage(message) {
  if (!ws || ws.readyState !== WebSocket.OPEN) {
    console.error("⚠ WebSocket이 연결되지 않았습니다.");
    return;
  }

  const payload = {
    message: message
  };

  ws.send(JSON.stringify(payload));
}

// 이미지를 서버에 업로드하는 함수
function uploadImage(file) {
  const formData = new FormData();
  formData.append("image", file);
  
  const contextPath = document.body.dataset.contextPath || '';

  fetch(`${contextPath}/uploadImage`, {
    method: "POST",
    body: formData
  })
  .then(response => {
    if (!response.ok) {
        throw new Error(`서버 응답 오류: ${response.status} ${response.statusText}`);
    }
    const contentType = response.headers.get("content-type");
    if (!contentType || !contentType.includes("application/json")) {
      return response.text().then(text => {
        console.error("서버가 JSON이 아닌 응답을 반환했습니다:", text);
        throw new TypeError("서버가 JSON이 아닌 응답을 반환했습니다. 서버 로그를 확인하세요.");
      });
    }
    return response.json();
  })
  .then(data => {
    if (data.success && data.imageUrl) {
      sendImageMessage(data.imageUrl);
    } else {
      alert("이미지 업로드에 실패했습니다: " + (data.error || "알 수 없는 오류"));
    }
  })
  .catch(error => {
    console.error("이미지 업로드 중 오류 발생:", error);
    alert("이미지 업로드 중 오류가 발생했습니다. 개발자 콘솔을 확인하세요.");
  });
}


// 채팅창에 새 메시지를 추가하는 함수
function appendMessage(data) {
  const chatBox = document.getElementById("chatBox");
  if (!chatBox) return; // chatBox가 없으면 함수 종료

  const isMine = data.senderId == currentUserId;
  const message = data.message;

  const chatRow = document.createElement("div");
  chatRow.classList.add("chat-row", isMine ? "my-message" : "other-message");

  const bubble = document.createElement("div");
  bubble.classList.add("bubble");

  if (message.startsWith("IMG::")) {
    const imageUrl = message.substring(5);
    const contextPath = document.body.dataset.contextPath || '';
    const img = document.createElement("img");
    img.src = `${contextPath}${imageUrl}`;
    img.classList.add("chat-image");
    bubble.appendChild(img);
  } else {
    const messageText = document.createElement("span");
    messageText.classList.add("message-text");
    messageText.textContent = message;
    bubble.appendChild(messageText);
  }

  const time = document.createElement("span");
  time.classList.add("time");
  time.textContent = data.createdAt;

  bubble.appendChild(time);
  chatRow.appendChild(bubble);
  chatBox.appendChild(chatRow);

  chatBox.scrollTop = chatBox.scrollHeight;
}

// ===== 이미지 확대 모달 기능 =====
function initializeImageModal(chatBox) {
    // 모달이 이미 생성되었는지 확인
    if (document.getElementById('imageModal')) {
        return;
    }

    // 1. 모달 HTML 요소를 동적으로 생성하고 body에 추가
    const modalHTML = `
        <div id="imageModal" class="image-modal">
            <span class="image-modal-close">&times;</span>
            <img class="image-modal-content" id="modalImage">
        </div>
    `;
    document.body.insertAdjacentHTML('beforeend', modalHTML);

    // 2. 모달 관련 CSS를 동적으로 생성하고 head에 추가
    const modalStyle = `
        .image-modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            overflow: auto;
            background-color: rgba(0,0,0,0.7); 
            justify-content: center;
            align-items: center;
        }
        .image-modal-content {
            margin: auto;
            display: block;
            max-width: 80%;
            max-height: 80%;
        }
        .image-modal-close {
            position: absolute;
            top: 15px;
            right: 35px;
            color: #f1f1f1;
            font-size: 40px;
            font-weight: bold;
            transition: 0.3s;
            cursor: pointer;
        }
        .image-modal-close:hover,
        .image-modal-close:focus {
            color: #bbb;
            text-decoration: none;
        }
    `;
    const styleSheet = document.createElement("style");
    styleSheet.type = "text/css";
    styleSheet.innerText = modalStyle;
    document.head.appendChild(styleSheet);

    // 3. 모달 요소 가져오기
    const modal = document.getElementById('imageModal');
    const modalImg = document.getElementById('modalImage');
    const closeModal = document.querySelector('.image-modal-close');

    // 4. 이벤트 리스너 설정 (이벤트 위임 사용)
    chatBox.addEventListener('click', function(event) {
        if (event.target.classList.contains('chat-image')) {
            modal.style.display = 'flex';
            modalImg.src = event.target.src;
        }
    });

    // 5. 모달 닫기 이벤트
    closeModal.onclick = function() {
        modal.style.display = "none";
    }

    modal.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    }
}