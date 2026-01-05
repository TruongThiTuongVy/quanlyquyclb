package DAO;

import DB_Connect.Database;
import models.Fee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeesDAO {
    private static FeesDAO instance;
    public static FeesDAO getInstance() {
        if (instance == null) instance = new FeesDAO();
        return instance;
    }
    public List<Fee> selectAll() {
        List<Fee> list = new ArrayList<>();
        Database db = new Database();
        db.connect();
        Connection con = db.con;
        if (con == null) return list;

        try {
            String sql = "SELECT f.*, " +
                    "(SELECT COUNT(*) FROM user_fees uf WHERE uf.f_id = f.f_id AND uf.status = 'PAID') as paid_count_db " +
                    "FROM fees f";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Fee f = new Fee();
                f.setFId(rs.getInt("f_id"));
                f.setTitle(rs.getString("title"));
                f.setAmount(rs.getBigDecimal("amount"));
                f.setDeadline(rs.getDate("deadline"));
                f.setDescription(rs.getString("description"));
                f.setTargetAmount(rs.getBigDecimal("target_amount"));
                f.setPaidCount(rs.getInt("paid_count_db"));
                list.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { con.close(); } catch(Exception e){}
        }
        return list;
    }
    public int insert(Fee fee) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;
        if (con == null) return 0;
        try {
            String sql = "INSERT INTO fees(title, amount, target_amount, deadline, description) VALUES(?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, fee.getTitle());
            pst.setBigDecimal(2, fee.getAmount());
            pst.setBigDecimal(3, fee.getTargetAmount()); // Lưu cột mới
            pst.setDate(4, fee.getDeadline());
            pst.setString(5, fee.getDescription());
            return pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try { con.close(); } catch(Exception e){}
        }
    }
    public int update(Fee fee) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;
        if (con == null) return 0;

        try {

            String sql = "UPDATE fees SET title=?, amount=?, target_amount=?, deadline=?, description=? WHERE f_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, fee.getTitle());
            pst.setBigDecimal(2, fee.getAmount());
            pst.setBigDecimal(3, fee.getTargetAmount());
            pst.setDate(4, fee.getDeadline());
            pst.setString(5, fee.getDescription());
            pst.setInt(6, fee.getFId());

            return pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try { con.close(); } catch(Exception e){}
        }
    }
    public int delete(int feeId) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;
        if (con == null) return 0;

        try {
            con.setAutoCommit(false);
            String sql1 = "DELETE FROM user_fees WHERE f_id=?";
            PreparedStatement pst1 = con.prepareStatement(sql1);
            pst1.setInt(1, feeId);
            pst1.executeUpdate();
            String sql2 = "DELETE FROM fees WHERE f_id=?";
            PreparedStatement pst2 = con.prepareStatement(sql2);
            pst2.setInt(1, feeId);
            int result = pst2.executeUpdate();
            con.commit();
            return result;
        } catch (SQLException e) {
            try { con.rollback(); } catch(Exception ex){}
            e.printStackTrace();
            return 0;
        } finally {
            try { con.close(); } catch(Exception e){}
        }
    }
    public Fee selectById(int feeId) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;
        if (con == null) return null;
        Fee f = null;
        try {
            String sql = "SELECT * FROM fees WHERE f_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, feeId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                f = new Fee();
                f.setFId(rs.getInt("f_id"));
                f.setTitle(rs.getString("title"));
                f.setAmount(rs.getBigDecimal("amount"));
                f.setTargetAmount(rs.getBigDecimal("target_amount"));
                f.setDeadline(rs.getDate("deadline"));
                f.setDescription(rs.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { con.close(); } catch(Exception e){}
        }
        return f;
    }
    public List<MemberFeeStatusRow> getMembersByFeeId(int feeId) {
        List<MemberFeeStatusRow> list = new ArrayList<>();
        Database db = new Database();
        db.connect();
        try {
            String sql = "SELECT u.u_name, u.email, uf.status, uf.paid_date " +
                    "FROM user_fees uf " +
                    "JOIN users u ON uf.u_id = u.u_id " +
                    "WHERE uf.f_id = ?";
            PreparedStatement pst = db.con.prepareStatement(sql);
            pst.setInt(1, feeId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String name = rs.getString("u_name");
                String email = rs.getString("email");
                String status = rs.getString("status");
                Date date = rs.getDate("paid_date");
                list.add(new MemberFeeStatusRow(name, email, status, date));
            }
            db.con.close();
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static class MemberFeeStatusRow {
        String name, email, status;
        Date paidDate;
        public MemberFeeStatusRow(String n, String e, String s, Date d) {
            name = n; email = e; status = s; paidDate = d;
        }
        // Getter
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getStatus() { return status; }
        public Date getPaidDate() { return paidDate; }
    }
}