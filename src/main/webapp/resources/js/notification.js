let notificationSound; // Declare globally

document.addEventListener("DOMContentLoaded", function() {
    // JSP에서 설정한 전역 변수에서 사용자 ID를 가져옵니다.
    // 이 변수가 없거나 비어있으면 (비로그인 상태) 아무 작업도 하지 않습니다.
    if (typeof currentUserId === 'undefined' || !currentUserId) {
        console.log("사용자가 로그인하지 않았으므로 알림 소켓을 연결하지 않습니다.");
        return;
    }

    const contextPath = document.body.getAttribute('data-context-path') || '';
    notificationSound = new Audio(`${contextPath}/resources/sounds/notification.mp3`);
    notificationSound.load(); // Preload the sound

    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsHost = window.location.host;
    const wsUrl = `${wsProtocol}//${wsHost}${contextPath}/notifications/${currentUserId}`;

    const currentChatRoomId = document.body.getAttribute('data-room-id'); // 현재 채팅방 ID 가져오기

    let socket;

    function connect() {
        socket = new WebSocket(wsUrl);

        socket.onopen = function() {
            console.log("알림 소켓에 연결되었습니다.");
        };

        socket.onmessage = function(event) {
            console.log("알림 수신:", event.data);
            try {
                const data = JSON.parse(event.data);
                if (data.type === 'newMessage') {
                    // 현재 보고 있는 채팅방의 메시지가 아닐 경우에만 알림 표시
                    if (!currentChatRoomId || data.roomId != currentChatRoomId) {
                        showToast(data.senderNickname, data.message, data.roomId);
                    } else {
                        console.log("현재 채팅방 메시지이므로 알림을 표시하지 않습니다.");
                    }
                }
            } catch (e) {
                console.error("알림 메시지 파싱 오류:", e);
            }
        };

        socket.onclose = function(event) {
            console.log("알림 소켓 연결이 끊겼습니다. 5초 후 재연결을 시도합니다.");
            setTimeout(connect, 5000); // 5초 후 재연결 시도
        };

        socket.onerror = function(error) {
            console.error("웹소켓 오류:", error);
            socket.close(); // 오류 발생 시 연결을 명시적으로 닫아 onclose 핸들러가 재연결을 시도하도록 함
        };
    }

    connect();
});

/**
 * 화면에 토스트 알림을 표시하는 함수
 * @param {string} sender - 메시지를 보낸 사람의 닉네임
 * @param {string} message - 메시지 내용
 * @param {number} roomId - 채팅방 ID
 */
function showToast(sender, message, roomId) {
    // 알림 소리 재생
    notificationSound.play().catch(e => console.error("알림 소리 재생 실패:", e));

    // 토스트 컨테이너 생성
    const toast = document.createElement('div');
    toast.className = 'toast-notification';

    // 토스트 내용 구성
    toast.innerHTML = `
        <div class="toast-header">${sender}님의 새 메시지!</div>
        <div>${message}</div>
    `;

    // 클릭 이벤트 추가
    toast.addEventListener('click', function() {
        console.log('Toast clicked!');
        console.log('roomId:', roomId);
        console.log('currentUserId:', currentUserId);
        
        if (roomId && currentUserId) {
            const contextPath = document.body.getAttribute('data-context-path') || '';
            const url = `${contextPath}/chatRoom?roomId=${roomId}&currentUserId=${currentUserId}`;
            console.log('Redirecting to:', url);
            window.location.href = url;
        } else {
            console.error('roomId or currentUserId is missing.');
        }
    });

    // body에 토스트 추가
    document.body.appendChild(toast);

    // 잠시 후 'show' 클래스를 추가하여 나타나는 애니메이션 효과 적용
    setTimeout(() => {
        toast.classList.add('show');
    }, 100); // 100ms 딜레이

    // 3초 후에 사라지도록 설정
    setTimeout(() => {
        toast.classList.remove('show');
        // 애니메이션이 끝난 후 DOM에서 제거
        setTimeout(() => {
            if (document.body.contains(toast)) {
                document.body.removeChild(toast);
            }
        }, 300); // CSS transition 시간과 일치시키는 것이 좋음
    }, 3000);
}
