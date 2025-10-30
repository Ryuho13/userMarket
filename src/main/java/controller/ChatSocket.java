package controller;

import java.io.IOException;
import java.util.*;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/chatSocket/{roomId}/{userId}")
public class ChatSocket {

    private static Map<Long, Set<Session>> roomSessions = new HashMap<>();

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("roomId") long roomId,
                       @PathParam("userId") long userId) {

        roomSessions.computeIfAbsent(roomId, k -> new HashSet<>()).add(session);
        System.out.println("[입장] Room " + roomId + " | User " + userId);
    }

    @OnMessage
    public void onMessage(String message,
                          @PathParam("roomId") long roomId,
                          @PathParam("userId") long userId) throws IOException {

       
        String formattedMsg = "[" + userId + "] : " + message;
        System.out.println( formattedMsg);

        for (Session s : roomSessions.get(roomId)) {
            if (s.isOpen()) {
                s.getBasicRemote().sendText(formattedMsg);
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
        error.printStackTrace();
    }
}
