package controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/notifications/{userId}")
public class NotificationSocket {

    // 사용자 ID와 웹소켓 세션을 매핑하여 관리
    private static final Map<Long, Session> userSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") long userId) {
        if (userId > 0) {
            userSessions.put(userId, session);
            System.out.println("▶ 알림 소켓 연결: userId=" + userId);
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") long userId) {
        if (userId > 0) {
            userSessions.remove(userId);
            System.out.println("■ 알림 소켓 연결 종료: userId=" + userId);
        }
    }

    /**
     * 특정 사용자에게 알림 메시지를 전송합니다.
     * @param userId  알림을 받을 사용자의 ID
     * @param message 전송할 메시지 (JSON 형식)
     */
    public static void sendNotification(long userId, String message) {
        Session session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                System.out.println("알림 전송: userId=" + userId + ", message=" + message);
            } catch (IOException e) {
                System.out.println("[ERROR] 알림 전송 실패: userId=" + userId);
                e.printStackTrace();
            }
        }
    }
}
