package DAO;

import DB_Connect.Database;
import models.FeeStatus;
import models.status;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeStatusDAO {
    private static FeeStatusDAO instance;

    public static FeeStatusDAO getInstance() {
        if (instance == null) instance = new FeeStatusDAO();
        return instance;
    }
    public int insert(FeeStatus fs) {
        Connection con = Database.getConnection();
        if (con == null) return 0;
        try {
            String sql = "INSERT INTO user_fees(u_id, f_id, status) VALUES(?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, fs.getUserId());
            pst.setInt(2, fs.getFId());
            pst.setString(3, fs.getStatus().toString());

            int result = pst.executeUpdate();
            Database.closeConnection(con);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public int update(FeeStatus fs) {
        Connection con = Database.getConnection();
        if (con == null) return 0;
        try {
            String sql = "UPDATE user_fees SET status=?, paid_date=? WHERE u_id=? AND f_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, fs.getStatus().toString());
            if (fs.getStatus() == status.PAID) {
                pst.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            } else {
                pst.setDate(2, null);
            }
            pst.setInt(3, fs.getUserId());
            pst.setInt(4, fs.getFId());
            int result = pst.executeUpdate();
            Database.closeConnection(con);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public List<FeeStatus> selectByUserId(int userId) {
        List<FeeStatus> list = new ArrayList<>();
        Connection con = Database.getConnection();
        if (con == null) return list;
        try {
            String sql = "SELECT * FROM user_fees WHERE u_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                FeeStatus fs = new FeeStatus();
                fs.setUfId(rs.getInt("uf_id"));
                fs.setUserId(rs.getInt("u_id"));
                fs.setFId(rs.getInt("f_id"));
                try {
                    fs.setStatus(status.valueOf(rs.getString("status")));
                } catch (Exception e) {
                    fs.setStatus(status.UNPAID);
                }
                fs.setPaidDate(rs.getDate("paid_date"));
                list.add(fs);
            }
            Database.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<FeeStatus> selectUnpaidByUserId(int userId) {
        List<FeeStatus> list = new ArrayList<>();
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        if (con == null) return list;

        try {
            String sql = "SELECT * FROM user_fees WHERE u_id=? AND status='UNPAID'";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                FeeStatus fs = new FeeStatus();
                fs.setUfId(rs.getInt("uf_id"));
                fs.setUserId(rs.getInt("u_id"));
                fs.setFId(rs.getInt("f_id"));
                try { fs.setStatus(models.status.valueOf(rs.getString("status"))); } catch (Exception e) {}
                list.add(fs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { con.close(); } catch(Exception e){}
        }
        return list;
    }
    public FeeStatus selectById(int ufId) {
        Connection con = Database.getConnection();
        FeeStatus fs = null;
        if (con == null) return null;
        try {
            String sql = "SELECT * FROM user_fees WHERE uf_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, ufId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                fs = new FeeStatus();
                fs.setUfId(rs.getInt("uf_id"));
                fs.setUserId(rs.getInt("u_id"));
                fs.setFId(rs.getInt("f_id"));
                // Xử lý Enum an toàn
                try {
                    fs.setStatus(models.status.valueOf(rs.getString("status")));
                } catch (Exception e) {
                    fs.setStatus(models.status.UNPAID);
                }
                fs.setPaidDate(rs.getDate("paid_date"));
            }
            Database.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fs;
    }
    public List<FeeStatus> selectPaidByUserId(int userId) {
        List<FeeStatus> list = new ArrayList<>();
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        if (con == null) return list;

        try {
            String sql = "SELECT * FROM user_fees WHERE u_id=? AND status='PAID' ORDER BY paid_date DESC";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                FeeStatus fs = new FeeStatus();
                fs.setUfId(rs.getInt("uf_id"));
                fs.setUserId(rs.getInt("u_id"));
                fs.setFId(rs.getInt("f_id"));
                try { fs.setStatus(models.status.valueOf(rs.getString("status"))); } catch (Exception e) {}
                fs.setPaidDate(rs.getDate("paid_date"));
                list.add(fs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { con.close(); } catch(Exception e){}
        }
        return list;
    }


}