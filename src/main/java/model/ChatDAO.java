package model;

import java.sql.*;
import java.util.*;
import model.ChatRoom;
import model.Message;

public class ChatDAO {

    private Connection conn;

    public ChatDAO(Connection conn) {
        this.conn = conn;
    }

    // 채팅방 찾거나 생성
    public ChatRoom findOrCreateRoom(long productId, long buyerId) {
        try {
            // 기존 채팅방 있는지 확인
            String checkSql = "SELECT * FROM chat_room WHERE products_id = ? AND buyer_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(checkSql);
            pstmt.setLong(1, productId);
            pstmt.setLong(2, buyerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("[DEBUG] 기존 채팅방 존재 ");
                return new ChatRoom(
                    rs.getLong("id"),
                    rs.getLong("products_id"),
                    rs.getLong("buyer_id"),
                    rs.getTimestamp("created_at")
                );
            }

            //  없으면 새로 생성
            String insertSql = "INSERT INTO chat_room (products_id, buyer_id, created_at) VALUES (?, ?, NOW())";
            pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, productId);
            pstmt.setLong(2, buyerId);

            int result = pstmt.executeUpdate();
            if (result == 0) {
                System.out.println("[ERROR] chat_room INSERT 실패 ");
                return null;
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                long newId = rs.getLong(1);
                System.out.println("[DEBUG] 새 채팅방 생성 성공 ID=" + newId);
                return new ChatRoom(newId, productId, buyerId);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] ChatDAO.findOrCreateRoom() 예외 발생 ❌");
            e.printStackTrace();
        }
        return null;
    }

    // 메시지 목록 불러오기
    public List<Message> getMessages(long roomId) {
        List<Message> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM chat_messages WHERE chat_room_id = ? ORDER BY created_at ASC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new Message(
                    rs.getLong("id"),
                    rs.getLong("chat_room_id"),
                    rs.getLong("sender_id"),
                    rs.getString("message"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] ChatDAO.getMessages() 예외 발생");
            e.printStackTrace();
        }
        return list;
    }

    // 메시지 저장
    public void saveMessage(long roomId, long senderId, String message) {
        try {
            String sql = "INSERT INTO chat_messages (chat_room_id, sender_id, message, created_at) VALUES (?, ?, ?, NOW())";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, roomId);
            pstmt.setLong(2, senderId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
            System.out.println("[DB] 메시지 저장 완료");
        } catch (SQLException e) {
            System.out.println("[ERROR] ChatDAO.saveMessage() 예외 발생");
            e.printStackTrace();
        }
    }
}
