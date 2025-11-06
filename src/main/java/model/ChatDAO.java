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

    public ChatRoom findChatRoomById(long roomId) throws SQLException {
        String sql = "SELECT id, products_id, buyer_id, created_at FROM chat_room WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ChatRoom(
                        rs.getLong("id"),
                        rs.getLong("products_id"),
                        rs.getLong("buyer_id"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        }
        return null; // Chat room not found
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
            System.out.println("[DB] 메시지를 저장했습니다.");
        } catch (SQLException e) {
            System.out.println("[ERROR] ChatDAO.saveMessage() 에서 오류 발생");
            e.printStackTrace();
        }
    }

    public long[] getChatRoomParticipantIds(long roomId) throws SQLException {
        String sql = """
            SELECT cr.buyer_id, p.seller_id
            FROM chat_room cr
            JOIN products p ON cr.products_id = p.id
            WHERE cr.id = ?
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new long[]{rs.getLong("buyer_id"), rs.getLong("seller_id")};
                }
            }
        }
        return null; // Chat room not found
    }

    public List<ChatRoomDisplayDTO> getChatRoomsByUserId(long userId) {
        List<ChatRoomDisplayDTO> list = new ArrayList<>();
        String sql = """
            SELECT
                cr.id,
                cr.products_id,
                cr.buyer_id,
                cr.created_at,
                p.title AS productTitle,
                p.seller_id,
                ui_buyer.nickname AS buyerNickname,
                ui_seller.nickname AS sellerNickname
            FROM chat_room cr
            JOIN products p ON cr.products_id = p.id
            JOIN user_info ui_buyer ON cr.buyer_id = ui_buyer.u_id
            JOIN user_info ui_seller ON p.seller_id = ui_seller.u_id
            WHERE cr.buyer_id = ? OR p.seller_id = ?
            ORDER BY cr.created_at DESC
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setLong(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long roomId = rs.getLong("id");
                    long productId = rs.getLong("products_id");
                    long buyerId = rs.getLong("buyer_id");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    String productTitle = rs.getString("productTitle");
                    long sellerId = rs.getLong("seller_id");
                    String buyerNickname = rs.getString("buyerNickname");
                    String sellerNickname = rs.getString("sellerNickname");

                    String otherUserNickname;
                    long otherUserId;

                    if (userId == buyerId) { // Current user is the buyer, so the other user is the seller
                        otherUserNickname = sellerNickname;
                        otherUserId = sellerId;
                    } else { // Current user is the seller, so the other user is the buyer
                        otherUserNickname = buyerNickname;
                        otherUserId = buyerId;
                    }

                    ChatRoomDisplayDTO dto = new ChatRoomDisplayDTO(
                        roomId,
                        productId,
                        buyerId,
                        createdAt,
                        productTitle,
                        otherUserNickname,
                        otherUserId
                    );
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] ChatDAO.getChatRoomsByUserId() 예외 발생");
            e.printStackTrace();
        }
        return list;
    }
}
