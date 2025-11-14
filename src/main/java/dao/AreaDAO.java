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
                list.add(new SidoArea(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
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
                    list.add(new SiggArea(
                            rs.getInt("id"),
                            rs.getString("name"),
                            sidoId
                    ));
                }
            }
        }
        return list;
    }

    public List<SidoArea> getAllSidoAreas() throws Exception {
        return getAllSidos();
    }

    public List<SiggArea> getAllSiggAreas() throws Exception {
        List<SiggArea> list = new ArrayList<>();
        String sql = "SELECT id, name, sido_area_id FROM sigg_areas ORDER BY name";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new SiggArea(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("sido_area_id")
                ));
            }
        }
        return list;
    }
    
    public String getSidoNameById(int sidoId) throws SQLException {
        String sql = "SELECT name FROM sido_areas WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sidoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("name") : null;
            }
        }
    }

    public String getSiggNameById(int siggId) throws SQLException {
        String sql = "SELECT name FROM sigg_areas WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, siggId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("name") : null;
            }
        }
    }
    
    public Integer getSidoIdBySiggId(int siggId) throws SQLException {
        String sql = "SELECT sido_area_id FROM sigg_areas WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, siggId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }
    
    public SiggArea getSiggAreaById(int id) throws Exception {
        String sql = "SELECT id, name, sido_area_id FROM sigg_areas WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SiggArea(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("sido_area_id")
                    );
                }
            }
        }
        return null;
    }


}
