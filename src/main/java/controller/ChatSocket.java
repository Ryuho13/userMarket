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

    private static final Map<Long, Set<Session>> roomSessions = new HashMap<>();

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("roomId") long roomId,
                       @PathParam("userId") long userId) {
        roomSessions.computeIfAbsent(roomId, k -> new HashSet<>()).add(session);
        System.out.println("▶ 입장 : room=" + roomId + ", user=" + userId);
    }

    @OnMessage
    public void onMessage(String message,
                          @PathParam("roomId") long roomId,
                          @PathParam("userId") long userId) {

        System.out.println("📩 수신 : [" + userId + "] " + message);

        // DB 저장
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                new ChatDAO(conn).saveMessage(roomId, userId, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 같은 방 전체 브로드캐스트
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (Session s : sessions) {
                if (s.isOpen()) {
                    try {
                        s.getBasicRemote().sendText("[" + userId + "] : " + message);
                    } catch (IOException e) {
                        e.printStackTrace();
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
        if (sessions != null) sessions.remove(session);
        System.out.println("■ 퇴장 : room=" + roomId + ", user=" + userId);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }
}
