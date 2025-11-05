package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import model.ChatDAO;
import model.DBConnection;
import model.Message; // Message 클래스를 사용하기 위해 import

@ServerEndpoint("/chatSocket/{roomId}/{userId}")
public class ChatSocket {

    private static final Map<Long, Set<Session>> roomSessions = new HashMap<>();

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("roomId") long roomId,
                       @PathParam("userId") long userId) {
        roomSessions.computeIfAbsent(roomId, k -> new HashSet<>()).add(session);
        System.out.println("▶ 입장 : room=" + roomId + ", user=" + userId + ", session=" + session.getId());
    }

    @OnMessage
    public void onMessage(String messageJson, // 이제 JSON 형태의 문자열을 받음
                          @PathParam("roomId") long roomId,
                          @PathParam("userId") long userId) {

        // 클라이언트가 보낸 JSON에서 실제 메시지 추출 (정규식 사용으로 안정성 향상)
        String message = "";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"message\":\"(.*?)\"");
        java.util.regex.Matcher matcher = pattern.matcher(messageJson);
        if (matcher.find()) {
            message = matcher.group(1);
        } else {
            System.out.println("[ERROR] Malformed JSON received: " + messageJson);
            return; // 잘못된 형식의 데이터는 처리하지 않음
        }

        System.out.println("수신 : [" + userId + "] " + message);

        // 1. DB에 메시지 저장
        Message newMessage = saveMessageToDB(roomId, userId, message);
        if (newMessage == null) {
            System.out.println("[ERROR] 메시지 DB 저장 실패");
            return;
        }

        // 2. 같은 방의 모든 클라이언트에게 새 메시지 정보 브로드캐스트
        broadcastMessage(roomId, newMessage);
    }

    private Message saveMessageToDB(long roomId, long userId, String message) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                ChatDAO dao = new ChatDAO(conn);
                // saveMessage가 이제 저장된 Message 객체를 반환하도록 수정했다고 가정
                // 지금은 새 객체를 직접 생성하여 반환
                dao.saveMessage(roomId, userId, message);
                return new Message(0, roomId, userId, message, new Timestamp(System.currentTimeMillis()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void broadcastMessage(long roomId, Message message) {
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions == null) return;

        // 클라이언트에 보낼 JSON 생성
        String jsonMessage = String.format(
            "{\"senderId\":%d, \"message\":\"%s\", \"createdAt\":\"%s\"}",
            message.getSenderId(),
            message.getMessage().replace("\"", "\\\""), // 메시지 내 큰따옴표 이스케이프
            new java.text.SimpleDateFormat("HH:mm").format(message.getCreatedAt())
        );

        System.out.println("방송 : " + jsonMessage);

        for (Session s : sessions) {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(jsonMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session,
                        @PathParam("roomId") long roomId,
                        @PathParam("userId") long userId) {
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) sessions.remove(session);
        System.out.println("■ 퇴장 : room=" + roomId + ", user=" + userId + ", session=" + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }
}