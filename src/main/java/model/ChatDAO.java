package model;

import java.sql.*;
import java.util.*;

public class ChatDAO {
    private Connection conn;

    public ChatDAO(Connection conn) {
        this.conn = conn;
    }

    // ✅ 메시지 저장
    public void saveMessage(long roomId, long senderId, String message) {
        String sql = "INSERT INTO chat_messages (chat_room_id, sender_id, message, created_at) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, roomId);
            pstmt.setLong(2, senderId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ 메시지 조회
    public List<Message> getMessages(long roomId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT id, chat_room_id, sender_id, message, created_at FROM chat_messages WHERE chat_room_id = ? ORDER BY id ASC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message(
                        rs.getLong("id"),
                        rs.getLong("chat_room_id"),
                        rs.getLong("sender_id"),
                        rs.getString("message"),
                        rs.getTimestamp("created_at")
                    );
                    list.add(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ 채팅방 찾기/생성 (기존 그대로)
    public ChatRoom findOrCreateRoom(long productId, long buyerId) {
        try {
            String findSql = "SELECT * FROM chat_room WHERE products_id=? AND buyer_id=?";
            PreparedStatement pstmt = conn.prepareStatement(findSql);
            pstmt.setLong(1, productId);
            pstmt.setLong(2, buyerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new ChatRoom(
                    rs.getLong("id"),
                    rs.getLong("products_id"),
                    rs.getLong("buyer_id"),
                    rs.getTimestamp("created_at")
                );
            }

            // 없으면 새로 생성
            String insertSql = "INSERT INTO chat_room (products_id, buyer_id, created_at) VALUES (?, ?, NOW())";
            pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, productId);
            pstmt.setLong(2, buyerId);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
                return new ChatRoom(rs.getLong(1), productId, buyerId, new Timestamp(System.currentTimeMillis()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
