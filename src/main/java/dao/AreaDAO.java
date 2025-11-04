package dao;

import dao.DBUtil;

import model.SidoArea;
import model.SiggArea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {

    public List<SidoArea> getUserSidos(int userId) throws Exception {
        List<SidoArea> list = new ArrayList<>();
        String sql = "SELECT s.id AS sido_id, s.name AS sido_name FROM users u JOIN activity_areas aa ON u.id = aa.user_id JOIN sigg_areas sa ON aa.id2 = sa.id JOIN sido_areas s ON sa.sido_area_id = s.id WHERE u.id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SidoArea(rs.getInt("sido_id"), rs.getString("sido_name")));
                }
            }
        }
        return list;
    }

    public List<SiggArea> getSiggsByUser(int userId) throws Exception {
        List<SiggArea> list = new ArrayList<>();
        String sql = "SELECT sa.id AS sigg_id, sa.name AS sigg_name FROM sigg_areas sa JOIN sido_areas s ON sa.sido_area_id = s.id WHERE s.id = ( SELECT s_inner.id FROM users u JOIN activity_areas aa ON u.id = aa.user_id JOIN sigg_areas sa_inner ON aa.id2 = sa_inner.id JOIN sido_areas s_inner ON sa_inner.sido_area_id = s_inner.id WHERE u.id = ? )";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SiggArea(rs.getInt("sigg_id"), rs.getString("sigg_name")));
                }
            }
        }
        return list;
    }
}
