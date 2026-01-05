package DAO;

import DB_Connect.Database;
import java.sql.*;
import java.util.*;

public class StatisticsDAO {
    private static StatisticsDAO instance;
    public static StatisticsDAO getInstance() {
        if (instance == null) instance = new StatisticsDAO();
        return instance;
    }

    public java.math.BigDecimal getTotalBalance() {
        String sqlIncome = "SELECT SUM(f.amount) FROM user_fees uf " +
                "JOIN fees f ON uf.f_id = f.f_id " +
                "WHERE uf.status = 'PAID'";
        java.math.BigDecimal income = getSingleDecimal(sqlIncome);
        String sqlExpense = "SELECT SUM(t_amount) FROM transactions WHERE t_type = 'EXPENSE'";
        java.math.BigDecimal expense = getSingleDecimal(sqlExpense);
        System.out.println("DEBUG STATS: Tổng Thu = " + income + " | Tổng Chi = " + expense);
        System.out.println("DEBUG STATS: Số dư thật = " + income.subtract(expense));
        return income.subtract(expense);
    }
    public java.math.BigDecimal getTotalTarget() {
        return getSingleDecimal("SELECT SUM(target_amount) FROM fees");
    }

    // 4. Tính tổng đã chi tiêu
    public java.math.BigDecimal getTotalExpense() {
        return getSingleDecimal("SELECT SUM(t_amount) FROM transactions WHERE t_type = 'EXPENSE'");
    }

    // 5. Đếm số khoản thu
    public int countFees() {
        return getSingleInt("SELECT COUNT(*) FROM fees");
    }

    // 6. LẤY DỮ LIỆU BIỂU ĐỒ
    public Map<String, double[]> getMonthlyStats() {
        Map<String, double[]> stats = new TreeMap<>();
        Database db = new Database();
        db.connect();

        try {
            // A. Mục tiêu
            String sqlTarget = "SELECT DATE_FORMAT(deadline, '%Y-%m') as m, SUM(target_amount) FROM fees GROUP BY m";
            fillMap(db.con, sqlTarget, stats, 0);

            // B. Thu
            String sqlIncome = "SELECT DATE_FORMAT(paid_date, '%Y-%m') as m, SUM(f.amount) " +
                    "FROM user_fees uf JOIN fees f ON uf.f_id = f.f_id " +
                    "WHERE uf.status='PAID' GROUP BY m";
            fillMap(db.con, sqlIncome, stats, 1);

            // C. Đã chi
            String sqlExpense = "SELECT DATE_FORMAT(create_at, '%Y-%m') as m, SUM(t_amount) " +
                    "FROM transactions WHERE t_type='EXPENSE' GROUP BY m";
            fillMap(db.con, sqlExpense, stats, 2);

            db.con.close();
        } catch (Exception e) { e.printStackTrace(); }

        return stats;
    }
    private void fillMap(Connection con, String sql, Map<String, double[]> stats, int index) throws SQLException {
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            String m = rs.getString(1);
            if (m == null) continue;
            stats.putIfAbsent(m, new double[]{0, 0, 0});
            stats.get(m)[index] = rs.getDouble(2);
        }
    }

    private int getSingleInt(String sql) {
        int val = 0;
        try {
            Database db = new Database(); db.connect();
            ResultSet rs = db.con.createStatement().executeQuery(sql);
            if(rs.next()) val = rs.getInt(1);
            db.con.close();
        } catch(Exception e){}
        return val;
    }

    private java.math.BigDecimal getSingleDecimal(String sql) {
        java.math.BigDecimal val = java.math.BigDecimal.ZERO;
        try {
            Database db = new Database(); db.connect();
            ResultSet rs = db.con.createStatement().executeQuery(sql);
            if(rs.next() && rs.getBigDecimal(1) != null) val = rs.getBigDecimal(1);
            db.con.close();
        } catch(Exception e){}
        return val;
    }
}