package DAO;

import DB_Connect.Database;
import models.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private static NotificationDAO instance;
    public static NotificationDAO getInstance() {
        if (instance == null) instance = new NotificationDAO();
        return instance;
    }

    // HÀM GỬI THÔNG BÁO (INSERT)
    public boolean insert(Notification n) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;
        if (con == null) return false;
        PreparedStatement pst = null;

        try {
            String sql = "INSERT INTO notifications(u_id, title, message, created_at) VALUES(?,?,?,?)";
            pst = con.prepareStatement(sql);
            if (n.getUId() == 0) {
                pst.setNull(1, Types.INTEGER);
            } else {
                pst.setInt(1, n.getUId());
            }
            pst.setString(2, n.getTitle());
            pst.setString(3, n.getMessage());
            pst.setDate(4, n.getCreatedAt());

            int result = pst.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (Exception e) {}
        }
    }
    public List<Notification> selectByUserId(int userId) {
        List<Notification> list = new ArrayList<>();
        Database db = new Database();
        db.connect();
        Connection con = db.con;
        if (con == null) return list;

        try {
            String sql = "SELECT * FROM notifications WHERE u_id = ? OR u_id IS NULL OR u_id = 0 ORDER BY n_id DESC";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);

            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                Notification n = new Notification();
                n.setNId(rs.getInt("n_id"));
                n.setUId(rs.getInt("u_id"));
                n.setTitle(rs.getString("title"));
                n.setMessage(rs.getString("message"));
                n.setCreatedAt(rs.getDate("created_at"));
                list.add(n);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
        return list;
    }
}