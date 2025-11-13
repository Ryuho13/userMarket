# 채팅 기능 코드 동작 순서 설명

이 문서는 프로젝트의 채팅 기능이 어떻게 동작하는지 클라이언트부터 서버까지의 코드 흐름을 상세하게 설명합니다. 발표 자료로 활용하기 쉽도록 각 구성 요소의 역할과 코드 동작 순서를 이해하기 쉽게 정리했습니다.

---

## 1. 주요 파일 구성

채팅 기능은 다음과 같은 주요 파일들로 구성됩니다.

*   **클라이언트 측 (Frontend):**
    *   `src/main/webapp/chat/chatRoom.jsp`: 채팅방 UI를 렌더링하는 JSP 파일.
    *   `src/main/webapp/resources/js/chatRoom.js`: 채팅방의 클라이언트 측 로직(WebSocket 연결, 메시지 송수신, UI 업데이트, 스크롤 처리 등)을 담당하는 JavaScript 파일.
    *   `src/main/webapp/resources/css/chatRoom.css`: 채팅방의 스타일을 정의하는 CSS 파일.
*   **서버 측 (Backend):**
    *   `src/main/java/web/ChatRoomServlet.java`: 사용자가 채팅방에 입장할 때 초기 데이터를 준비하고 `chatRoom.jsp`로 포워딩하는 서블릿.
    *   `src/main/java/controller/ChatSocket.java`: WebSocket 통신을 처리하는 엔드포인트. 실시간 메시지 송수신 및 브로드캐스트를 담당합니다.
    *   `src/main/java/dao/ChatDAO.java`: 채팅방 및 메시지 데이터베이스 CRUD(생성, 읽기, 업데이트, 삭제) 작업을 처리하는 DAO(Data Access Object).
    *   `src/main/java/model/Message.java`: 채팅 메시지의 데이터 구조를 정의하는 POJO(Plain Old Java Object).
    *   `src/main/java/web/ImageUploadServlet.java`: 클라이언트에서 전송된 이미지를 서버에 업로드하고 URL을 반환하는 서블릿.

---

## 2. 채팅 기능 동작 순서 (Flow)

### 2.1. 사용자가 채팅방에 입장 (초기 로딩)

1.  **클라이언트 요청:** 사용자가 상품 상세 페이지에서 '채팅하기' 버튼을 클릭하거나, 마이페이지에서 기존 채팅방 목록을 통해 채팅방에 입장합니다.
    *   **상품 상세 페이지에서 새 채팅 시작:** `/chatRoom?productId={productId}&buyerId={buyerId}`
    *   **마이페이지에서 기존 채팅방 입장:** `/chatRoom?roomId={roomId}&currentUserId={currentUserId}`
2.  **`ChatRoomServlet.java` (서버):**
    *   `doGet()` 메서드가 요청을 받습니다.
    *   요청 파라미터(`roomId`, `productId`, `buyerId` 등)를 분석하여 기존 채팅방인지 새로운 채팅 시작인지 판단합니다.
    *   `ChatDAO`를 사용하여:
        *   기존 채팅방인 경우: `findChatRoomById()`를 통해 채팅방 정보를 조회합니다.
        *   새 채팅 시작인 경우: `findOrCreateRoom()`을 통해 채팅방을 찾거나 새로 생성합니다.
    *   `ProductDetailDAO`를 사용하여 채팅방과 관련된 상품 정보를 조회합니다.
    *   `UserDAO`를 사용하여 상대방 사용자의 닉네임을 조회합니다.
    *   조회된 `ChatRoom` 객체, `Message` 목록, `ProductDetail` 객체, 상대방 닉네임 등을 `request` 속성에 저장합니다.
    *   `request.getRequestDispatcher("/chat/chatRoom.jsp").forward(request, response);`를 통해 `chatRoom.jsp`로 포워딩합니다.
3.  **`chatRoom.jsp` (클라이언트):**
    *   서버로부터 전달받은 데이터를 기반으로 채팅방 UI를 렌더링합니다.
    *   `c:forEach` JSTL 태그를 사용하여 기존 메시지 목록을 화면에 표시합니다.
    *   `<body>` 태그의 `data-room-id`, `data-user-id`, `data-context-path` 속성에 채팅방 ID, 현재 사용자 ID, 컨텍스트 경로를 설정합니다. 이는 JavaScript에서 사용됩니다.
    *   `chatRoom.css` (스타일), `bootstrap.min.css` (Bootstrap 스타일), `bootstrap-icons.min.css` (아이콘), `bootstrap.bundle.min.js` (Bootstrap JS), `chatRoom.js` (클라이언트 로직) 등의 리소스를 로드합니다.
4.  **`chatRoom.js` (클라이언트):**
    *   `DOMContentLoaded` 이벤트 발생 시 초기화 로직을 실행합니다.
    *   `chatBox.scrollTop = chatBox.scrollHeight;`를 통해 채팅창을 최하단으로 스크롤합니다 (입장 시 스크롤).
    *   `new WebSocket(...)`을 사용하여 `ws://{host}:{port}/{contextPath}/chatSocket/{roomId}/{userId}` 경로로 WebSocket 연결을 시도합니다.

### 2.2. 텍스트 메시지 전송

1.  **클라이언트 입력:** 사용자가 메시지 입력창(`id="msg"`)에 텍스트를 입력하고 '보내기' 버튼(`id="sendBtn"`)을 클릭하거나 `Enter` 키를 누릅니다.
2.  **`chatRoom.js` (클라이언트):**
    *   `sendMessage()` 함수가 호출됩니다.
    *   입력된 메시지 텍스트를 가져와 `trim()` 처리합니다.
    *   메시지를 JSON 형식(`{"message": "..."}`)으로 변환하여 `ws.send()`를 통해 WebSocket 서버로 전송합니다.
    *   메시지 입력창을 비웁니다.
3.  **`ChatSocket.java` (서버):**
    *   `@OnMessage` 메서드가 클라이언트로부터 JSON 형식의 메시지를 수신합니다.
    *   수신된 JSON에서 실제 메시지 텍스트를 추출합니다.
    *   **DB 저장:** `ChatDAO.saveMessage(roomId, userId, message)`를 호출하여 메시지를 데이터베이스에 저장합니다.
    *   **브로드캐스트:** `broadcastMessage()` 메서드를 호출하여 현재 채팅방(`roomId`)에 연결된 모든 클라이언트(`Set<Session>`)에게 새 메시지 정보를 JSON 형식으로 브로드캐스트합니다.
    *   **알림 전송:** `ChatDAO.getChatRoomParticipantIds()`를 통해 채팅방 참여자 ID를 얻고, 메시지를 보낸 사람을 제외한 상대방에게 `NotificationSocket.sendNotification()`을 통해 새 메시지 알림을 전송합니다.
4.  **`chatRoom.js` (클라이언트):**
    *   `ws.onmessage` 이벤트 핸들러가 서버로부터 브로드캐스트된 메시지 JSON을 수신합니다.
    *   수신된 JSON을 파싱하여 메시지 데이터(`msg`)를 추출합니다.
    *   `msg.type`이 `"chat"`인 경우 `displayMessage(msg)` 함수를 호출하여 메시지를 화면에 표시합니다.

### 2.3. 이미지 메시지 전송

1.  **클라이언트 입력:** 사용자가 이미지 아이콘 버튼(`label for="imageUpload"`)을 클릭하여 파일 선택 대화상자를 열고 이미지를 선택합니다.
2.  **`chatRoom.js` (클라이언트):**
    *   `imageUpload` 요소의 `change` 이벤트 리스너가 트리거됩니다.
    *   선택된 이미지 파일을 `FormData` 객체에 추가합니다.
    *   `fetch(contextPath + "/imageUpload", ...)`를 사용하여 `ImageUploadServlet`으로 이미지 파일을 비동기적으로 전송합니다.
3.  **`ImageUploadServlet.java` (서버):**
    *   `@MultipartConfig` 어노테이션으로 파일 업로드를 처리할 준비가 되어 있습니다.
    *   `doPost()` 메서드가 이미지 파일을 수신합니다.
    *   업로드 디렉토리(`uploads/chat_images`)가 없으면 생성합니다.
    *   수신된 파일에 `UUID`를 사용하여 고유한 파일 이름을 생성하고, 서버의 지정된 업로드 디렉토리에 저장합니다.
    *   저장된 이미지의 상대 URL(`imageUrl`)을 생성합니다.
    *   클라이언트에게 `{"success": true, "imageUrl": "/uploads/chat_images/unique_filename.jpg"}` 형태의 JSON 응답을 보냅니다.
4.  **`chatRoom.js` (클라이언트):**
    *   `fetch` 요청의 `response.json()`을 통해 서버로부터 이미지 URL을 포함한 JSON 응답을 받습니다.
    *   `imageUrl`을 `IMG::` 접두사와 함께 WebSocket 서버로 전송합니다 (예: `ws.send(JSON.stringify({ message: "IMG::/uploads/chat_images/unique_filename.jpg" }));`).
5.  **`ChatSocket.java` (서버):**
    *   텍스트 메시지 전송과 동일하게 `@OnMessage` 메서드가 `IMG::` 접두사가 붙은 메시지를 수신합니다.
    *   `ChatDAO.saveMessage()`를 통해 메시지(이미지 URL)를 DB에 저장합니다.
    *   `broadcastMessage()`를 통해 `IMG::` 접두사가 붙은 메시지를 모든 클라이언트에게 브로드캐스트합니다.
    *   상대방에게 알림을 전송합니다.
6.  **`chatRoom.js` (클라이언트):**
    *   `ws.onmessage` 이벤트 핸들러가 `IMG::` 접두사가 붙은 메시지를 수신합니다.
    *   `displayMessage(msg)` 함수를 호출합니다.
    *   `displayMessage` 함수 내에서 메시지가 `IMG::`로 시작하는 것을 감지하고, `<img>` 태그를 생성하여 `src` 속성에 이미지 URL을 설정합니다.
    *   **스크롤 처리:** `imgElement.onload` 이벤트 리스너를 통해 이미지가 완전히 로드된 후 `setTimeout`을 사용하여 `chatBox.scrollTop = chatBox.scrollHeight;`를 호출하여 채팅창을 최하단으로 스크롤합니다. (이미지 로드 실패 시 `onerror`에서도 스크롤을 시도합니다.)

### 2.4. 메시지 수신 및 UI 업데이트

1.  **`ChatSocket.java` (서버):**
    *   `broadcastMessage()` 또는 `broadcastSystemMessage()` 메서드를 통해 특정 `roomId`의 모든 `Session`에 메시지 JSON을 전송합니다.
2.  **`chatRoom.js` (클라이언트):**
    *   `ws.onmessage` 이벤트 핸들러가 서버로부터 메시지 JSON을 수신합니다.
    *   `msg.type`에 따라 `displayMessage()` 또는 `displaySystemMessage()` 함수를 호출합니다.
    *   **`displayMessage(data)` 함수:**
        *   수신된 `data` (메시지 객체)를 기반으로 `div.chat-row` (메시지 행)와 `div.bubble` (말풍선) 요소를 생성합니다.
        *   `data.senderId`와 현재 로그인 사용자 ID를 비교하여 `my-message` 또는 `other-message` 클래스를 추가합니다.
        *   메시지 내용이 `IMG::`로 시작하면 `<img>` 태그를 생성하여 이미지로 표시하고, 그렇지 않으면 `<span>` 태그에 텍스트로 표시합니다.
        *   메시지 전송 시간을 `<span>` 태그로 추가합니다.
        *   생성된 메시지 요소를 `chatBox`에 추가합니다.
        *   `chatBox.scrollTop = chatBox.scrollHeight;`를 호출하여 채팅창을 최하단으로 스크롤합니다. (이미지의 경우 로드 완료 후 지연 스크롤).
    *   **`displaySystemMessage(message)` 함수:**
        *   시스템 메시지(`"입장했습니다"`, `"퇴장했습니다"`)를 표시하는 `div.system-message`를 생성하여 `chatBox`에 추가하고 최하단으로 스크롤합니다.

### 2.5. 이미지 모달 기능

1.  **`chatRoom.js` (클라이언트):**
    *   `displayMessage` 함수에서 이미지 메시지를 표시할 때, `imgElement.onclick = () => openImageModal(imageUrl);`를 통해 이미지 클릭 시 `openImageModal` 함수가 호출되도록 설정합니다.
    *   **`openImageModal(imageUrl)` 함수:**
        *   `id="imageModal"`인 모달 `div`와 `id="modalImage"`인 `<img>` 태그를 가져옵니다.
        *   `modalImage.src`에 클릭된 이미지의 `imageUrl`을 설정합니다.
        *   모달 `div`에서 `hidden` 클래스를 제거하여 모달을 화면에 표시합니다.
        *   `document.body.style.overflow = "hidden";`을 설정하여 배경 스크롤을 비활성화합니다.
    *   **모달 닫기:**
        *   `id="imageModal"` 모달 `div` 또는 `class="close-button"`을 클릭하면 `closeImageModal()` 함수가 호출됩니다.
        *   `closeImageModal()` 함수는 모달 `div`에 `hidden` 클래스를 다시 추가하여 모달을 숨기고, `document.body.style.overflow = "auto";`를 설정하여 배경 스크롤을 다시 활성화합니다.

---

## 3. 코드 상세 설명

### 3.1. `src/main/java/web/ChatRoomServlet.java`

*   **역할:** 채팅방 진입점. 초기 채팅방 정보, 메시지 목록, 상품 정보 등을 준비하여 클라이언트 JSP로 전달합니다.
*   **주요 메서드:** `doGet()`
*   **동작:**
    *   요청 파라미터(`roomId`, `productId`, `buyerId`)를 기반으로 `ChatDAO`를 통해 채팅방을 조회하거나 생성합니다.
    *   `ProductDetailDAO`를 통해 상품 정보를 가져옵니다.
    *   `UserDAO`를 통해 상대방 닉네임을 가져옵니다.
    *   모든 정보를 `request.setAttribute()`로 저장한 후 `/chat/chatRoom.jsp`로 포워딩합니다.
    *   사용자 권한 확인 및 예외 처리 로직을 포함합니다.

### 3.2. `src/main/java/controller/ChatSocket.java`

*   **역할:** WebSocket 서버 엔드포인트. 클라이언트와 실시간 양방향 통신을 담당합니다.
*   **주요 어노테이션:** `@ServerEndpoint("/chatSocket/{roomId}/{userId}")`
*   **주요 메서드:**
    *   `@OnOpen`: 클라이언트가 WebSocket 연결을 열 때 호출됩니다.
        *   `roomSessions` 맵에 현재 세션을 추가하여 방별로 세션을 관리합니다.
        *   `UserDAO`를 통해 입장한 사용자의 닉네임을 가져와 시스템 메시지(`"OOO님이 입장했습니다."`)를 해당 방의 모든 클라이언트에게 브로드캐스트합니다.
    *   `@OnMessage`: 클라이언트로부터 메시지를 수신할 때 호출됩니다.
        *   수신된 JSON 메시지에서 실제 메시지 내용을 추출합니다.
        *   `ChatDAO.saveMessage()`를 호출하여 메시지를 DB에 저장합니다.
        *   `broadcastMessage()`를 호출하여 메시지를 보낸 클라이언트를 포함한 해당 방의 모든 클라이언트에게 메시지를 브로드캐스트합니다.
        *   `NotificationSocket.sendNotification()`을 통해 메시지를 받은 상대방에게 알림을 전송합니다.
    *   `@OnClose`: 클라이언트의 WebSocket 연결이 닫힐 때 호출됩니다.
        *   `roomSessions` 맵에서 해당 세션을 제거합니다.
        *   퇴장 시스템 메시지를 브로드캐스트합니다.
    *   `broadcastMessage(int roomId, Message message)`: 특정 방의 모든 클라이언트에게 일반 채팅 메시지를 JSON 형식으로 전송합니다.
    *   `broadcastSystemMessage(int roomId, String message)`: 특정 방의 모든 클라이언트에게 시스템 메시지를 JSON 형식으로 전송합니다.

### 3.3. `src/main/java/dao/ChatDAO.java`

*   **역할:** 채팅방 및 메시지 관련 데이터베이스 작업을 수행합니다.
*   **주요 메서드:**
    *   `findChatRoomById(int roomId)`: ID로 채팅방을 조회합니다.
    *   `findOrCreateRoom(int productId, int buyerId)`: 특정 상품과 구매자 간의 채팅방을 찾거나 없으면 새로 생성합니다.
    *   `getMessages(int roomId)`: 특정 채팅방의 모든 메시지를 `created_at` 순으로 조회합니다.
    *   `saveMessage(int roomId, int senderId, String message)`: 새 메시지를 데이터베이스에 저장합니다.
    *   `getChatRoomParticipantIds(int roomId)`: 채팅방의 구매자 ID와 판매자 ID를 조회합니다.
    *   `getChatRoomsByUserId(int userId)`: 특정 사용자가 참여하고 있는 모든 채팅방 목록을 조회합니다. (마이페이지 채팅 목록에 사용)

### 3.4. `src/main/java/model/Message.java`

*   **역할:** 채팅 메시지의 데이터를 담는 POJO(Plain Old Java Object)입니다.
*   **주요 필드:** `id`, `chatRoomId`, `senderId`, `message`, `createdAt`
*   **생성자:** DB 조회용 및 새 메시지 저장용 생성자를 제공합니다.

### 3.5. `src/main/java/web/ImageUploadServlet.java`

*   **역할:** 클라이언트로부터 전송된 이미지 파일을 서버에 저장하고, 저장된 이미지의 URL을 클라이언트에게 반환합니다.
*   **주요 어노테이션:** `@WebServlet("/imageUpload")`, `@MultipartConfig`
*   **동작:**
    *   `doPost()` 메서드가 이미지 파일을 수신합니다.
    *   `UPLOAD_DIR` (예: `uploads/chat_images`) 경로에 파일을 저장합니다.
    *   `UUID.randomUUID()`를 사용하여 파일명 충돌을 방지하는 고유한 파일 이름을 생성합니다.
    *   저장 성공 시, 이미지의 상대 URL을 포함하는 JSON 응답을 클라이언트에게 보냅니다.

### 3.6. `src/main/webapp/chat/chatRoom.jsp`

*   **역할:** 채팅방의 사용자 인터페이스를 제공합니다.
*   **주요 요소:**
    *   **헤더:** 뒤로가기 버튼, 상대방 닉네임(`<h2>`), `spacer` div.
    *   **상품 정보 바:** 채팅 중인 상품의 이미지, 제목, 가격을 표시합니다.
    *   **채팅 메시지 영역 (`id="chatBox"`):** 메시지들이 동적으로 추가되는 공간입니다.
    *   **메시지 입력 영역 (`class="input-area"`):** 이미지 업로드 버튼, 텍스트 입력창(`id="msg"`), 보내기 버튼(`id="sendBtn"`).
    *   **이미지 모달 (`id="imageModal"`):** 클릭된 이미지를 크게 보여주는 모달창.
*   **JSTL:** `c:if`, `c:forEach`, `fmt:formatNumber` 등을 사용하여 조건부 렌더링, 반복, 숫자 포맷팅을 수행합니다.
*   **JavaScript 로드:** `chatRoom.js`를 포함하여 클라이언트 로직을 실행합니다.

### 3.7. `src/main/webapp/resources/js/chatRoom.js`

*   **역할:** 채팅방의 모든 클라이언트 측 동적 동작을 제어합니다.
*   **주요 기능:**
    *   **초기 스크롤:** `DOMContentLoaded` 시 `chatBox.scrollTop = chatBox.scrollHeight;`를 통해 채팅창을 최하단으로 스크롤합니다.
    *   **WebSocket 연결:** `ChatSocket` 엔드포인트로 WebSocket 연결을 설정합니다.
    *   **`ws.onopen`:** 연결 성공 시 콘솔에 로그를 출력합니다.
    *   **`ws.onmessage`:** 서버로부터 메시지를 수신할 때 호출됩니다.
        *   수신된 JSON 메시지를 파싱하여 `displayMessage()` 또는 `displaySystemMessage()`를 호출합니다.
    *   **`ws.onclose`, `ws.onerror`:** 연결 종료 및 오류 처리 로직을 포함합니다.
    *   **메시지 전송 (`sendMessage()`):**
        *   입력창의 텍스트를 WebSocket을 통해 서버로 전송합니다.
        *   `Enter` 키 또는 '보내기' 버튼 클릭 시 트리거됩니다.
    *   **이미지 업로드 처리:**
        *   `imageUpload` `change` 이벤트 발생 시 `ImageUploadServlet`으로 이미지를 `fetch` API를 통해 비동기 업로드합니다.
        *   업로드 성공 시, 서버로부터 받은 이미지 URL을 `IMG::` 접두사와 함께 WebSocket을 통해 서버로 전송합니다.
    *   **`displayMessage(data)` 함수:**
        *   수신된 메시지 데이터를 기반으로 HTML 요소를 생성하여 `chatBox`에 추가합니다.
        *   `senderId`를 비교하여 `my-message` 또는 `other-message` 클래스를 적용합니다.
        *   메시지 내용이 `IMG::`로 시작하면 `<img>` 태그를 생성하고, `onload` 및 `onerror` 이벤트 리스너를 추가하여 이미지가 로드된 후 스크롤이 최하단으로 내려가도록 합니다.
        *   텍스트 메시지의 경우 즉시 `chatBox.scrollTop = chatBox.scrollHeight;`를 호출합니다.
    *   **`displaySystemMessage(message)` 함수:** 시스템 메시지를 `chatBox`에 추가하고 스크롤합니다.
    *   **`formatTime(timestamp)`:** 타임스탬프를 "HH:mm" 형식으로 변환합니다.
    *   **이미지 모달 제어 (`openImageModal`, `closeImageModal`):** 이미지 클릭 시 모달을 열고 닫는 기능을 구현합니다.

---

## 4. 전체적인 데이터 흐름 예시

**시나리오: 사용자 A가 사용자 B에게 이미지 메시지를 보냄**

1.  **클라이언트 (사용자 A):**
    *   A가 채팅방에서 이미지 아이콘을 클릭하고 이미지를 선택합니다.
    *   `chatRoom.js`의 `imageUpload` `change` 이벤트가 발생합니다.
    *   `fetch` API를 사용하여 이미지를 `ImageUploadServlet`으로 `POST` 요청합니다.
2.  **서버 (`ImageUploadServlet.java`):**
    *   `doPost()` 메서드가 이미지를 수신하고, 고유한 파일명으로 서버의 `uploads/chat_images` 디렉토리에 저장합니다.
    *   저장된 이미지의 URL을 포함하는 JSON 응답을 A의 클라이언트에 반환합니다.
3.  **클라이언트 (사용자 A):**
    *   `chatRoom.js`가 `ImageUploadServlet`으로부터 이미지 URL을 받습니다.
    *   `IMG::` 접두사를 붙여 `ws.send()`를 통해 WebSocket 서버(`ChatSocket`)로 메시지(이미지 URL)를 전송합니다.
4.  **서버 (`ChatSocket.java`):**
    *   `@OnMessage` 메서드가 A로부터 `IMG::` 메시지를 수신합니다.
    *   `ChatDAO.saveMessage()`를 호출하여 이 메시지(이미지 URL)를 데이터베이스에 저장합니다.
    *   `broadcastMessage()`를 호출하여 A와 B가 연결된 모든 세션에 메시지 JSON을 브로드캐스트합니다.
    *   `NotificationSocket.sendNotification()`을 통해 B에게 새 메시지 알림을 전송합니다.
5.  **클라이언트 (사용자 A 및 사용자 B):**
    *   각 클라이언트의 `chatRoom.js`에서 `ws.onmessage` 이벤트가 발생하여 브로드캐스트된 메시지 JSON을 수신합니다.
    *   `displayMessage()` 함수가 호출됩니다.
    *   `IMG::` 접두사를 감지하여 `<img>` 태그를 생성하고 `src`에 이미지 URL을 설정합니다.
    *   `imgElement.onload` 이벤트 리스너가 이미지가 완전히 로드된 후 `setTimeout`을 통해 `chatBox.scrollTop = chatBox.scrollHeight;`를 호출하여 채팅창을 최하단으로 스크롤합니다.
    *   이미지가 화면에 표시됩니다.

---


