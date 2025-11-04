package dao;

import model.SidoArea;
import model.SiggArea;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {
	public List<SidoArea> getAllSidos() throws Exception {
	    List<SidoArea> list = new ArrayList<>();
	    String sql = "SELECT id, name FROM sido_areas ORDER BY id";
	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            list.add(new SidoArea(rs.getInt("id"), rs.getString("name")));
	        }
	    }
	    return list;
	}

	public List<SiggArea> getSiggsBySido(int sidoId) throws Exception {
	    List<SiggArea> list = new ArrayList<>();
	    String sql = "SELECT id, name FROM sigg_areas WHERE sido_area_id = ? ORDER BY name";
	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, sidoId);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(new SiggArea(rs.getInt("id"), rs.getString("name")));
	            }
	        }
	    }
	    return list;
	}

}
