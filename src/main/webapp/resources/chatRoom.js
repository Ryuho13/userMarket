window.addEventListener("DOMContentLoaded", () => {
  const box = document.querySelector("#chatBox");
  const msg = document.querySelector("#msg");
  const btn = document.querySelector("#sendBtn");
  const roomId = box.dataset.room;
  const userId = box.dataset.user;

  const ws = new WebSocket(`ws://${location.host}${window.location.pathname.replace("/chat/chatRoom.jsp", "")}/chatSocket/${roomId}/${userId}`);

  ws.onopen = () => console.log("✅ WebSocket 연결 성공");
  ws.onclose = () => console.log("🚪 WebSocket 연결 종료");
  ws.onerror = (e) => console.error("⚠️ WebSocket 오류", e);

  ws.onmessage = (e) => {
    const text = e.data;
    const div = document.createElement("div");
    const mine = text.startsWith(`[${userId}]`);
    div.className = mine ? "my-message" : "other-message";
    div.textContent = text.replace(`[${userId}] : `, "");
    box.append(div);
    box.scrollTop = box.scrollHeight;
  };

  const send = () => {
    const val = msg.value.trim();
    if (!val) return;
    ws.send(val);
    msg.value = "";
  };

  // Enter 전송 / Shift+Enter 줄바꿈
  msg.addEventListener("keydown", (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      send();
    }
  });

  btn.addEventListener("click", send);
});
