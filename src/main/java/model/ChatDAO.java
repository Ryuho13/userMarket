package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.ChatRoom;
import model.Message;

public class ChatDAO {
	
	private Connection conn; 

    public ChatDAO(Connection conn) {
        this.conn = conn;
    }
	
	public ChatRoom findOrCreateRoom(long productId, long buyerId) {
	    ChatRoom room = null;
	    String selectSql = "SELECT * FROM chat_room WHERE products_id = ? AND buyer_id = ?";
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
	            // 없으면 새로 생성
	            String insertSql = "INSERT INTO chat_room (products_id, buyer_id, created_at) VALUES (?, ?, NOW())";
	            try (PreparedStatement pstmt2 = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
	                pstmt2.setLong(1, productId);
	                pstmt2.setLong(2, buyerId);
	                pstmt2.executeUpdate();
	                ResultSet keys = pstmt2.getGeneratedKeys();
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

}
