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
    // 동일한 파일을 다시 선택할 수 있도록 입력 값을 초기화
    e.target.value = null;
  });

  // 페이지 로드 시 스크롤을 가장 아래로 이동
  const chatBox = document.getElementById("chatBox");
  if (chatBox) {
    chatBox.scrollTop = chatBox.scrollHeight;
  }
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
  .then(response => response.json())
  .then(data => {
    if (data.success && data.imageUrl) {
      sendImageMessage(data.imageUrl);
    } else {
      alert("이미지 업로드에 실패했습니다: " + (data.error || "알 수 없는 오류"));
    }
  })
  .catch(error => {
    console.error("이미지 업로드 중 오류 발생:", error);
    alert("이미지 업로드 중 오류가 발생했습니다.");
  });
}


// 채팅창에 새 메시지를 추가하는 함수
function appendMessage(data) {
  const chatBox = document.getElementById("chatBox");
  const isMine = data.senderId == currentUserId;
  const message = data.message;

  const chatRow = document.createElement("div");
  chatRow.classList.add("chat-row", isMine ? "my-message" : "other-message");

  const bubble = document.createElement("div");
  bubble.classList.add("bubble");

  // 메시지 내용 처리 (이미지 또는 텍스트)
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