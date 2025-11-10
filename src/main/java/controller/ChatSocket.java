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
import controller.NotificationSocket;
import dao.UserDAO;
import model.UserProfile;

@ServerEndpoint("/chatSocket/{roomId}/{userId}")
public class ChatSocket {

    private static final Map<Integer, Set<Session>> roomSessions = new HashMap<>();

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("roomId") int roomId,
                       @PathParam("userId") int userId) {
        roomSessions.computeIfAbsent(roomId, k -> new HashSet<>()).add(session);
        System.out.println("▶ 입장 : room=" + roomId + ", user=" + userId + ", session=" + session.getId());
    }

    @OnMessage
    public void onMessage(String messageJson, // 이제 JSON 형태의 문자열을 받음
                          @PathParam("roomId") int roomId,
                          @PathParam("userId") int userId) {

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
        // saveMessageToDB가 Message 객체를 반환하도록 수정했다고 가정
        // 현재는 void이므로, Message 객체를 직접 생성하여 사용.
        Message newMessage = null;
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                ChatDAO chatDAO = new ChatDAO(conn);
                chatDAO.saveMessage(roomId, userId, message); // 메시지 저장
                newMessage = new Message(0, roomId, userId, message, new Timestamp(System.currentTimeMillis()));
            } else {
                System.out.println("[ERROR] DB 연결 실패");
                return;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] 메시지 DB 저장 실패");
            e.printStackTrace();
            return;
        }
        if (newMessage == null) {
            System.out.println("[ERROR] 메시지 객체 생성 실패");
            return;
        }

        // 2. 같은 방의 모든 클라이언트에게 새 메시지 정보 브로드캐스트
        broadcastMessage(roomId, newMessage);

        // 3. 상대방에게 알림 전송
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                ChatDAO chatDAO = new ChatDAO(conn);
                int[] participantIds = chatDAO.getChatRoomParticipantIds(roomId); // [buyerId, sellerId]

                if (participantIds != null) {
                    int buyerId = participantIds[0];
                    int sellerId = participantIds[1];

                    int recipientId = (userId == buyerId) ? sellerId : buyerId; // 메시지 보낸 사람이 아니면 수신자

                    // 수신자가 현재 채팅방에 없는 경우에만 알림을 보냄 (선택 사항, UX에 따라 조절)
                    // 현재는 그냥 보냄. NotificationSocket에서 세션이 없으면 안 보냄.

                    UserDAO userDAO = new UserDAO(); // UserDAO는 Connection을 받지 않는 기본 생성자 사용 가정
                    UserProfile senderProfile = userDAO.findProfileByUserId(userId);
                    String senderNickname = (senderProfile != null) ? senderProfile.getNickname() : "알 수 없음";

                    String notificationJson = String.format(
                        "{\"type\":\"newMessage\", \"senderNickname\":\"%s\", \"message\":\"%s\", \"roomId\":%d}",
                        senderNickname.replace("\"", "\\\""), // 닉네임 내 큰따옴표 이스케이프
                        message.replace("\"", "\\\""), // 메시지 내 큰따옴표 이스케이프
                        roomId
                    );
                    NotificationSocket.sendNotification(recipientId, notificationJson);
                }
            } else {
                System.out.println("[ERROR] 알림 전송을 위한 DB 연결 실패");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] 알림 전송 중 오류 발생");
            e.printStackTrace();
        }
    }

    private void broadcastMessage(int roomId, Message message) {
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
                        @PathParam("roomId") int roomId,
                        @PathParam("userId") int userId) {
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) sessions.remove(session);
        System.out.println("■ 퇴장 : room=" + roomId + ", user=" + userId + ", session=" + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }
}