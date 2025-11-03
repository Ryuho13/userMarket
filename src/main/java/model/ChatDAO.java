package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {
    private Connection conn;

    public ChatDAO(Connection conn) {
        this.conn = conn;
    }

    // 채팅방 존재 여부 확인 및 생성
    public ChatRoom findOrCreateRoom(long productId, long buyerId) {
        ChatRoom room = null;

        String selectSql = "SELECT * FROM chat_room WHERE products_id=? AND buyer_id=?";
        String insertSql = "INSERT INTO chat_room (products_id, buyer_id, created_at) VALUES (?, ?, NOW())";

        try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            pstmt.setLong(1, productId);
            pstmt.setLong(2, buyerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                room = new ChatRoom(
                    rs.getLong("id"),
                    rs.getLong("products_id"),
                    rs.getLong("buyer_id"),
                    rs.getTimestamp("created_at")
                );
            } else {
                try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertPstmt.setLong(1, productId);
                    insertPstmt.setLong(2, buyerId);
                    insertPstmt.executeUpdate();

                    ResultSet keys = insertPstmt.getGeneratedKeys();
                    if (keys.next()) {
                        room = new ChatRoom(keys.getLong(1), productId, buyerId, new Timestamp(System.currentTimeMillis()));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return room;
    }

    // 메시지 저장
    public void saveMessage(long roomId, long senderId, String message) {
        String sql = "INSERT INTO chat_messages (chat_room_id, sender_id, message, created_at) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, roomId);
            pstmt.setLong(2, senderId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 메시지 목록 불러오기
    public List<Message> getMessages(long roomId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT * FROM chat_messages WHERE chat_room_id=? ORDER BY created_at ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Message msg = new Message();
                msg.setId(rs.getLong("id"));
                msg.setChatRoomId(rs.getLong("chat_room_id"));
                msg.setSenderId(rs.getLong("sender_id"));
                msg.setMessage(rs.getString("message"));
                msg.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
