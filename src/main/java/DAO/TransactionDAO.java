package DAO;

import DB_Connect.Database;
import models.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private static TransactionDAO instance;
    public static TransactionDAO getInstance() {
        if (instance == null) instance = new TransactionDAO();
        return instance;
    }
    public boolean insert(Transaction t) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;
        if (con == null) return false;

        try {
            String sql = "INSERT INTO transactions(u_id, fc_id, t_type, t_amount, t_note, create_at) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, t.getUserId());
            pst.setInt(2, t.getFcId()); // Thường là 1
            pst.setString(3, t.getTType().toString()); // INCOME hoặc EXPENSE
            pst.setBigDecimal(4, t.getTAmount());
            pst.setString(5, t.getTNote());
            if (t.getCreateAt() == null) {
                pst.setDate(6, new Date(System.currentTimeMillis()));
            } else {
                pst.setDate(6, (Date) t.getCreateAt());
            }

            int result = pst.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { con.close(); } catch(Exception e){}
        }
    }
    public boolean createExpense(String content, java.math.BigDecimal amount) {
        Transaction t = new Transaction();
        t.setUserId(1);
        t.setFcId(1);
        t.setTType(models.transactionType.EXPENSE);
        t.setTAmount(amount);
        t.setTNote(content);
        t.setCreateAt(new Date(System.currentTimeMillis()));
        return insert(t);
    }
    public List<Transaction> getAllExpenses() {
        List<Transaction> list = new ArrayList<>();
        Database db = new Database();
        db.connect();
        try {
            String sql = "SELECT * FROM transactions WHERE t_type = 'EXPENSE' ORDER BY create_at DESC";
            PreparedStatement pst = db.con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setTId(rs.getInt("t_id"));
                t.setUserId(rs.getInt("u_id"));
                t.setFcId(rs.getInt("fc_id"));
                try {
                    t.setTType(models.transactionType.valueOf(rs.getString("t_type")));
                } catch (Exception e) {}
                t.setTAmount(rs.getBigDecimal("t_amount"));
                t.setTNote(rs.getString("t_note"));
                t.setCreateAt(rs.getDate("create_at"));
                list.add(t);
            }
            db.con.close();
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}