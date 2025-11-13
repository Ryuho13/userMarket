document.addEventListener("DOMContentLoaded", () => {
  const chatBox = document.getElementById("chatBox");
  if (chatBox) {
    chatBox.scrollTop = chatBox.scrollHeight;
  }

  const roomId = document.body.dataset.roomId;
  const userId = document.body.dataset.userId;
  const contextPath = document.body.dataset.contextPath;

  // WebSocket 연결
  const ws = new WebSocket("ws://" + window.location.host + contextPath + "/chatSocket/" + roomId + "/" + userId);

  ws.onopen = function(e) {
    console.log("WebSocket 연결 성공:", e);
  };

  ws.onmessage = function(e) {
    const msg = JSON.parse(e.data);
    console.log("메시지 수신:", msg);

    if (msg.type === "system") {
      displaySystemMessage(msg.message);
    } else if (msg.type === "chat") {
      displayMessage(msg);
    }
  };

  ws.onclose = function(e) {
    console.log("WebSocket 연결 종료:", e);
  };

  ws.onerror = function(e) {
    console.error("WebSocket 오류:", e);
  };

  // 메시지 전송
  const msgInput = document.getElementById("msg");
  const sendBtn = document.getElementById("sendBtn");

  sendBtn.addEventListener("click", sendMessage);
  msgInput.addEventListener("keypress", (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  });

  function sendMessage() {
    const message = msgInput.value.trim();
    if (message) {
      ws.send(JSON.stringify({ message: message }));
      msgInput.value = "";
    }
  }

  // 이미지 업로드 처리
  const imageUpload = document.getElementById("imageUpload");
  if (imageUpload) {
    imageUpload.addEventListener("change", async (event) => {
      const file = event.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append("image", file);

      try {
        const response = await fetch(contextPath + "/imageUpload", {
          method: "POST",
          body: formData,
        });

        if (response.ok) {
          const result = await response.json();
          if (result.success) {
            const imageUrl = "IMG::" + result.imageUrl;
            ws.send(JSON.stringify({ message: imageUrl }));
          } else {
            alert("이미지 업로드 실패: " + result.message);
          }
        } else {
          alert("이미지 업로드 서버 오류");
        }
      } catch (error) {
        console.error("이미지 업로드 중 오류 발생:", error);
        alert("이미지 업로드 중 오류 발생");
      }
      imageUpload.value = "";
    });
  }
});

function displayMessage(data) {
  const chatBox = document.getElementById("chatBox");
  const isMine = (data.senderId == document.body.dataset.userId);

  const chatRow = document.createElement("div");
  chatRow.classList.add("chat-row", isMine ? "my-message" : "other-message");

  const bubble = document.createElement("div");
  bubble.classList.add("bubble");

  if (data.message.startsWith("IMG::")) {
    const imageUrl = document.body.dataset.contextPath + data.message.substring(5);
    const imgElement = document.createElement("img");
    imgElement.src = imageUrl;
    imgElement.classList.add("chat-image");
    imgElement.alt = "채팅 이미지";
    imgElement.onclick = () => openImageModal(imageUrl);
    bubble.appendChild(imgElement);
  } else {
    const messageText = document.createElement("span");
    messageText.classList.add("message-text");
    messageText.textContent = data.message;
    bubble.appendChild(messageText);
  }

  const time = document.createElement("span");
  time.classList.add("time");
  time.textContent = formatTime(data.createdAt);
  bubble.appendChild(time);

  chatRow.appendChild(bubble);
  chatBox.appendChild(chatRow);
  chatBox.scrollTop = chatBox.scrollHeight;
}

function displaySystemMessage(message) {
  const chatBox = document.getElementById("chatBox");
  const systemMessageDiv = document.createElement("div");
  systemMessageDiv.classList.add("system-message");
  systemMessageDiv.textContent = message;
  chatBox.appendChild(systemMessageDiv);
  chatBox.scrollTop = chatBox.scrollHeight;
}

function formatTime(timestamp) {
  const date = new Date(Number(timestamp));
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${hours}:${minutes}`;
}

// 이미지 모달 열기 함수 (전역 스코프)
window.openImageModal = function(imageUrl) {
  const modal = document.getElementById("imageModal");
  const modalImage = document.getElementById("modalImage");
  if (modal && modalImage) {
    modalImage.src = imageUrl;
    modal.classList.remove("hidden");
    document.body.style.overflow = "hidden";
  }
};

// 이미지 모달 닫기 함수
function closeImageModal() {
  const modal = document.getElementById("imageModal");
  if (modal) {
    modal.classList.add("hidden");
    document.body.style.overflow = "auto";
  }
}

// 모달 닫기 이벤트 리스너 추가
document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("imageModal");
    const closeButton = document.querySelector(".close-button");

    if(modal) {
        modal.addEventListener("click", (e) => {
            if (e.target === modal) {
                closeImageModal();
            }
        });
    }
    if(closeButton) {
        closeButton.addEventListener("click", closeImageModal);
    }
});
// JSP 환경에서는 버튼 크기가 로드 후 결정되므로, JS로 spacer 너비를 설정합니다.
window.onload = function() {
    const backButton = document.querySelector('.btn-outline-muted');
    const spacer = document.querySelector('.spacer');
    // 버튼의 실제 계산된 너비를 spacer에 적용하여 완벽한 중앙 정렬을 보장합니다.
    spacer.style.width = backButton.offsetWidth + 'px';

    // Font Awesome 아이콘으로 변경 (JSP 파일에는 Font Awesome이 link 태그로 추가되어 있으므로 적용)
    const backButtonText = document.querySelector('.btn-outline-muted');
    backButtonText.innerHTML = '<i class="fas fa-arrow-left"></i> 뒤로가기';
};
