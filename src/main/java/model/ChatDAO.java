package model;

import java.sql.*;
import java.util.*;
import dao.DBUtil; // DBUtil 임포트
import model.ChatRoom;
import model.Message;

public class ChatDAO {

    // 기본 생성자
    public ChatDAO() {
    }

    public ChatRoom findChatRoomById(int roomId) throws SQLException {
        String sql = "SELECT id, product_id, buyer_id, created_at FROM chat_room WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); // DBUtil 사용
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ChatRoom(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getInt("buyer_id"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        }
        return null; // Chat room not found
    }

    // 채팅방 찾거나 생성
    public ChatRoom findOrCreateRoom(int productId, int buyerId) {
        String checkSql = "SELECT * FROM chat_room WHERE product_id = ? AND buyer_id = ?";
        try (Connection conn = DBUtil.getConnection(); // DBUtil 사용
             PreparedStatement pstmtCheck = conn.prepareStatement(checkSql)) {
            pstmtCheck.setInt(1, productId);
            pstmtCheck.setInt(2, buyerId);
            try (ResultSet rs = pstmtCheck.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[DEBUG] 기존 채팅방 존재 ");
                    return new ChatRoom(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getInt("buyer_id"),
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
        String insertSql = "INSERT INTO chat_room (product_id, buyer_id, created_at) VALUES (?, ?, NOW())";
        try (Connection conn = DBUtil.getConnection(); // DBUtil 사용
             PreparedStatement pstmtInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmtInsert.setInt(1, productId);
            pstmtInsert.setInt(2, buyerId);

            int result = pstmtInsert.executeUpdate();
            if (result == 0) {
                System.out.println("[ERROR] chat_room INSERT 실패 ");
                return null;
            }

            try (ResultSet rs = pstmtInsert.getGeneratedKeys()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
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
    public List<Message> getMessages(int roomId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT * FROM chat_messages WHERE chat_room_id = ? ORDER BY created_at ASC";
        try (Connection conn = DBUtil.getConnection(); // DBUtil 사용
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Message(
                        rs.getInt("id"),
                        rs.getInt("chat_room_id"),
                        rs.getInt("sender_id"),
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
    public void saveMessage(int roomId, int senderId, String message) {
        String sql = "INSERT INTO chat_messages (chat_room_id, sender_id, message, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DBUtil.getConnection(); // DBUtil 사용
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.setInt(2, senderId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
            System.out.println("[DB] 메시지를 저장했습니다.");
        } catch (SQLException e) {
            System.out.println("[ERROR] ChatDAO.saveMessage() 에서 오류 발생");
            e.printStackTrace();
        }
    }

    public int[] getChatRoomParticipantIds(int roomId) throws SQLException {
        String sql = """
            SELECT cr.buyer_id, p.seller_id
            FROM chat_room cr
            JOIN products p ON cr.product_id = p.id
            WHERE cr.id = ?
        """;
        try (Connection conn = DBUtil.getConnection(); // DBUtil 사용
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new int[]{rs.getInt("buyer_id"), rs.getInt("seller_id")};
                }
            }
        }
        return null; // Chat room not found
    }

    public List<ChatRoomDisplayDTO> getChatRoomsByUserId(int userId) {
        List<ChatRoomDisplayDTO> list = new ArrayList<>();
        String sql = """
            SELECT
                cr.id,
                cr.product_id,
                cr.buyer_id,
                cr.created_at,
                p.title AS productTitle,
                p.seller_id,
                ui_buyer.nickname AS buyerNickname,
                ui_seller.nickname AS sellerNickname
            FROM chat_room cr
            JOIN products p ON cr.product_id = p.id
            LEFT JOIN user_info ui_buyer ON cr.buyer_id = ui_buyer.u_id
            LEFT JOIN user_info ui_seller ON p.seller_id = ui_seller.u_id
            WHERE cr.buyer_id = ? OR p.seller_id = ?
            ORDER BY cr.created_at DESC
        """;
        try (Connection conn = DBUtil.getConnection(); // DBUtil 사용
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int roomId = rs.getInt("id");
                    int productId = rs.getInt("product_id");
                    int buyerId = rs.getInt("buyer_id");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    String productTitle = rs.getString("productTitle");
                    int sellerId = rs.getInt("seller_id");
                    String buyerNickname = rs.getString("buyerNickname");
                    String sellerNickname = rs.getString("sellerNickname");

                    String otherUserNickname;
                    int otherUserId;

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
