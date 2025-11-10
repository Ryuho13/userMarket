<%-- Global CSS and JS includes for notification system --%>

<%-- 1. Toast Notification CSS --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/toast.css">

<%-- 2. Notification WebSocket JavaScript --%>
<script>
    // 현재 로그인한 사용자 ID를 JavaScript 전역 변수로 설정합니다.
    // 서블릿에서 `session.setAttribute("loginUserId", ...)`로 설정된 값을 사용합니다.
    var currentUserId = "${sessionScope.loginUserId}";
</script>
<script src="${pageContext.request.contextPath}/resources/js/notification.js"></script>
