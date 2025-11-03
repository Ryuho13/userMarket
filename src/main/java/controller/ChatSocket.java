package controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.*;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import model.ChatDAO;
import model.DBConnection;

@ServerEndpoint("/chatSocket/{roomId}/{userId}")
public class ChatSocket {

    // 방 ID별로 세션 목록 저장
    private static final Map<Long, Set<Session>> roomSessions = new HashMap<>();

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("roomId") long roomId,
                       @PathParam("userId") long userId) {
        roomSessions.computeIfAbsent(roomId, k -> new HashSet<>()).add(session);
        System.out.println("[입장] Room " + roomId + " | User " + userId);
    }

    /**
     * 클라이언트 메시지 수신 시 실행
     * - DB 저장
     * - 해당 채팅방 모든 세션에 브로드캐스트
     */
    @OnMessage
    public void onMessage(Session session,
                          String message,
                          @PathParam("roomId") long roomId,
                          @PathParam("userId") long userId) {

        System.out.println("[메시지 수신] Room " + roomId + " | User " + userId + " : " + message);

        // 1️⃣ DB에 메시지 저장
        try (Connection conn = DBConnection.getConnection()) {
            ChatDAO dao = new ChatDAO(conn);
            dao.saveMessage(roomId, userId, message);
        } catch (Exception e) {
            System.err.println("[DB 오류] 메시지 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }

        // 2️⃣ 같은 방에 있는 모든 세션에게 전송
        String formattedMsg = "[" + userId + "] : " + message;
        Set<Session> sessions = roomSessions.get(roomId);

        if (sessions != null) {
            for (Session s : sessions) {
                if (s.isOpen()) {
                    try {
                        s.getBasicRemote().sendText(formattedMsg);
                    } catch (IOException e) {
                        System.err.println("[전송 실패] 세션 오류: " + e.getMessage());
                    }
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session,
                        @PathParam("roomId") long roomId,
                        @PathParam("userId") long userId) {
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            System.out.println("[퇴장] Room " + roomId + " | User " + userId);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("[에러 발생] " + error.getMessage());
        error.printStackTrace();
    }
}
