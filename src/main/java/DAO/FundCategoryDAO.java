package DAO;

import DB_Connect.Database;
import models.FundCategory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FundCategoryDAO implements DAOinterface <FundCategory>{
    public static FundCategoryDAO getInstance() {
        return new FundCategoryDAO();
    }
    @Override
    public int insert(FundCategory fundCategory) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        PreparedStatement pst = null;
        int result = 0;

        try {
            String sql = "INSERT INTO fund_categories(fc_id, fc_name, type) VALUES(?,?,?)";

            pst = con.prepareStatement(sql);

            pst.setInt(1, fundCategory.getFcId());
            pst.setString(2, fundCategory.getFcName());
            pst.setString(3, fundCategory.getType());

            result = pst.executeUpdate();
        }catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    //UPDATE

    @Override
    public int update(FundCategory fundCategory) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        PreparedStatement pst = null;
        int result = 0;

        try{

            String sql="UPDATE fund_categories SET fc_name=?, type=? WHERE fc_id=?";

            pst = con.prepareStatement(sql);

            pst.setString(1, fundCategory.getFcName());
            pst.setString(2, fundCategory.getType());

            result = pst.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    //DELETE

    @Override
    public boolean delete(FundCategory fundCategory) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        PreparedStatement pst = null;
        int result = 0;

        try{
            String sql =  "DELETE FROM fund_categories WHERE fc_id=?";

            pst = con.prepareStatement(sql);

            pst.setInt(1, fundCategory.getFcId());

            result = pst.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result>0;
    }

    // SELECT ALL

    @Override
    public ArrayList<FundCategory> selectAll() {
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        ArrayList<FundCategory> list = new ArrayList<>();
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM fund_category";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                FundCategory fc = new FundCategory();
                fc.setFcId(rs.getInt("fc_id"));
                fc.setFcName(rs.getString("fc_name"));
                fc.setType(rs.getString("type"));
                list.add(fc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //SELECT BY ID

    @Override
    public FundCategory selectById(FundCategory category) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        PreparedStatement pst = null;
        ResultSet rs = null;
        FundCategory fc = null;

        try {
            String sql = "SELECT * FROM fund_category WHERE fc_id = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, category.getFcId());
            rs = pst.executeQuery();

            if (rs.next()) {
                fc = new FundCategory();
                fc.setFcId(rs.getInt("fc_id"));
                fc.setFcName(rs.getString("fc_name"));
                fc.setType(rs.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return fc;
    }

    //SELECT BY TYPE

    public ArrayList<FundCategory> selectByType(String type) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        ArrayList<FundCategory> list = new ArrayList<>();
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM fund_category WHERE type = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, type);
            rs = pst.executeQuery();

            while (rs.next()) {
                FundCategory fc = new FundCategory();
                fc.setFcId(rs.getInt("fc_id"));
                fc.setFcName(rs.getString("fc_name"));
                fc.setType(rs.getString("type"));
                list.add(fc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
