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
        String checkSql = "SELECT * FROM chat_room WHERE products_id = ? AND buyer_id = ?";
        try (PreparedStatement pstmtCheck = conn.prepareStatement(checkSql)) {
            pstmtCheck.setLong(1, productId);
            pstmtCheck.setLong(2, buyerId);
            try (ResultSet rs = pstmtCheck.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[DEBUG] 기존 채팅방 존재 ");
                    return new ChatRoom(
                        rs.getLong("id"),
                        rs.getLong("products_id"),
                        rs.getLong("buyer_id"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] ChatDAO.findOrCreateRoom() - check 예외 발생 ❌");
            e.printStackTrace();
            return null; // Or throw a custom exception
        }

        // 없으면 새로 생성
        String insertSql = "INSERT INTO chat_room (products_id, buyer_id, created_at) VALUES (?, ?, NOW())";
        try (PreparedStatement pstmtInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmtInsert.setLong(1, productId);
            pstmtInsert.setLong(2, buyerId);

            int result = pstmtInsert.executeUpdate();
            if (result == 0) {
                System.out.println("[ERROR] chat_room INSERT 실패 ");
                return null;
            }

            try (ResultSet rs = pstmtInsert.getGeneratedKeys()) {
                if (rs.next()) {
                    long newId = rs.getLong(1);
                    System.out.println("[DEBUG] 새 채팅방 생성 성공 ID=" + newId);
                    return new ChatRoom(newId, productId, buyerId);
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] ChatDAO.findOrCreateRoom() - insert 예외 발생 ❌");
            e.printStackTrace();
        }
        return null;
    }

    // 메시지 목록 불러오기
    public List<Message> getMessages(long roomId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT * FROM chat_messages WHERE chat_room_id = ? ORDER BY created_at ASC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
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
        } catch (SQLException e) {
            System.out.println("[ERROR] ChatDAO.getMessages() 예외 발생");
            e.printStackTrace();
        }
        return list;
    }

    // 메시지 저장
    public void saveMessage(long roomId, long senderId, String message) {
        String sql = "INSERT INTO chat_messages (chat_room_id, sender_id, message, created_at) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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