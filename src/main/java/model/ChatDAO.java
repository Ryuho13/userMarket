package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {
    private final Connection conn;

    public ChatDAO(Connection conn) {
        this.conn = conn;
    }

    // 방 찾거나 생성
    public ChatRoom findOrCreateRoom(long productId, long buyerId) {
        String sel = "SELECT id, products_id, buyer_id, created_at " +
                     "FROM chat_room WHERE products_id = ? AND buyer_id = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sel)) {
            ps.setLong(1, productId);
            ps.setLong(2, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ChatRoom(
                        rs.getLong("id"),
                        rs.getLong("products_id"),
                        rs.getLong("buyer_id"),
                        rs.getTimestamp("created_at")
                    );
                }
            }

            // 없으면 생성
            String ins = "INSERT INTO chat_room(products_id, buyer_id, created_at) VALUES(?, ?, NOW())";
            try (PreparedStatement ps2 = conn.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
                ps2.setLong(1, productId);
                ps2.setLong(2, buyerId);
                ps2.executeUpdate();
                try (ResultSet keys = ps2.getGeneratedKeys()) {
                    if (keys.next()) {
                        long id = keys.getLong(1);
                        return new ChatRoom(id, productId, buyerId, new Timestamp(System.currentTimeMillis()));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 메시지 저장
    public void saveMessage(long roomId, long senderId, String content) {
        String sql = "INSERT INTO chat_messages(chat_room_id, sender_id, message, created_at) " +
                     "VALUES (?, ?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roomId);
            ps.setLong(2, senderId);
            ps.setString(3, content);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 메시지 목록
    public List<Message> getMessages(long roomId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT id, chat_room_id, sender_id, message, created_at " +
                     "FROM chat_messages WHERE chat_room_id = ? ORDER BY id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Message(
                        rs.getLong("id"),
                        rs.getLong("chat_room_id"),
                        rs.getLong("sender_id"),
                        rs.getString("message"),
                        rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
