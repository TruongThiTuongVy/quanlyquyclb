package DAO;

import DB_Connect.Database;
import models.Users;
import models.UsersRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class UsersDAO implements DAOinterface <Users> {
    public static UsersDAO getInstance() {
        return new UsersDAO();
    }


    //INSERT
    @Override
    public int insert(Users users) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        PreparedStatement pst = null;
        int result = 0;

        try {

            String sql = "INSERT INTO users(u_name,email,phone,role,password, status) VALUES(?,?,?,?,?,?)";

            pst = con.prepareStatement(sql);

            pst.setString(1, users.getUserName());
            pst.setString(2, users.getEmail());
            pst.setString(3, users.getPhone());
            pst.setString(4, String.valueOf(users.getRole()));
            pst.setString(5, users.getPassword());

            if (users.getStatus() == null) {
                pst.setString(6, "PENDING");
            } else {
                pst.setString(6, users.getStatus());
            }
            result = pst.executeUpdate();
        } catch (SQLException e) {
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
    @Override
    public int update(Users users) {
        Connection con = Database.getConnection();
        if (con == null) return 0;
        PreparedStatement pst = null;
        int result = 0;

        try {
            String sql = "UPDATE users SET u_name=?, email=?, phone=?, role=?, password=?, status=? WHERE u_id=?";

            pst = con.prepareStatement(sql);

            pst.setString(1, users.getUserName());
            pst.setString(2, users.getEmail());
            pst.setString(3, users.getPhone());
            pst.setString(4, String.valueOf(users.getRole()));
            pst.setString(5, users.getPassword());
            pst.setString(6, users.getStatus() != null ? users.getStatus() : "ACTIVE");
            pst.setInt(7, users.getUserId());

            result = pst.executeUpdate();
            System.out.println("DEBUG UPDATE User " + users.getUserId() + ": Result = " + result);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pst != null) pst.close();
                Database.closeConnection(con);
            } catch (Exception e) { e.printStackTrace(); }
        }
        return result;
    }

    //DELETE
    @Override
    public boolean delete(Users users) {
        Connection con = Database.getConnection();
        if (con == null) return false;
        PreparedStatement pst1 = null;
        PreparedStatement pst2 = null;

        try {

            con.setAutoCommit(false);
            String sqlFees = "DELETE FROM user_fees WHERE u_id=?";
            pst1 = con.prepareStatement(sqlFees);
            pst1.setInt(1, users.getUserId());
            pst1.executeUpdate();
            String sqlUser = "DELETE FROM users WHERE u_id=?";
            pst2 = con.prepareStatement(sqlUser);
            pst2.setInt(1, users.getUserId());
            int result = pst2.executeUpdate();
            con.commit();
            return result > 0;

        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pst1 != null) pst1.close();
                if (pst2 != null) pst2.close();
                Database.closeConnection(con);
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public ArrayList<Users> selectAll() {
        ArrayList<Users> list = new ArrayList<>();
        Connection con = Database.getConnection();
        if (con == null) return list;

        try {
            String sql = "SELECT u.*, " +
                    "(SELECT SUM(f.amount) " +
                    " FROM user_fees uf " +
                    " JOIN fees f ON uf.f_id = f.f_id " +
                    " WHERE uf.u_id = u.u_id AND uf.status = 'PAID') as total_paid " +
                    "FROM users u";

            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Users u = new Users();
                u.setUserId(rs.getInt("u_id"));
                u.setUserName(rs.getString("u_name"));
                u.setEmail(rs.getString("email"));
                u.setPhone(rs.getString("phone"));
                u.setPassword(rs.getString("password"));

                try {
                    u.setRole(models.UsersRole.valueOf(rs.getString("role").toUpperCase()));
                } catch (Exception e) { u.setRole(models.UsersRole.MEMBER); }

                String st = rs.getString("status");
                u.setStatus(st != null ? st : "ACTIVE");
                java.math.BigDecimal total = rs.getBigDecimal("total_paid");
                if (total == null) total = java.math.BigDecimal.ZERO;
                u.setTotalPaid(total);

                list.add(u);
            }
            Database.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //SELECT BY ID

    @Override
    public Users selectById(Users users) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        PreparedStatement pst = null;
        ResultSet rs = null;
        Users u = null;

        try {
            String sql = "SELECT * FROM users WHERE u_id = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, users.getUserId());

            rs = pst.executeQuery();

            if (rs.next()) {
                u = new Users();
                u.setUserId(rs.getInt("u_id"));
                u.setUserName(rs.getString("u_name"));
                u.setEmail(rs.getString("email"));
                u.setPhone(rs.getString("phone"));
                u.setPassword(rs.getString("password"));

                try {
                    String roleStr = rs.getString("role");
                    if (roleStr != null) {
                        u.setRole(UsersRole.valueOf(roleStr));
                    } else {
                        u.setRole(UsersRole.MEMBER);
                    }
                } catch (IllegalArgumentException e) {
                    u.setRole(UsersRole.MEMBER);
                }
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
        return u;
    }

    //SELECT BY EMAIL
    public Users selectByEmail(String email) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        PreparedStatement pst = null;
        ResultSet rs = null;
        Users u = null;

        try {
            String sql = "SELECT * FROM users WHERE email = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, email);

            rs = pst.executeQuery();

            if (rs.next()) {
                u = new Users();
                u.setUserId(rs.getInt("u_id"));
                u.setUserName(rs.getString("u_name"));
                u.setEmail(rs.getString("email"));
                u.setPhone(rs.getString("phone"));
                u.setPassword(rs.getString("password"));

                try {
                    u.setRole(UsersRole.valueOf(rs.getString("role")));
                } catch (Exception e) {
                    u.setRole(UsersRole.MEMBER);
                }
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
        return u;
    }
    public boolean checkEmailExists(String email) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            String sql = "SELECT u_id FROM users WHERE email = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, email);

            rs = pst.executeQuery();
            exists = rs.next();
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
        return exists;
    }


    public Users selectByEmailAndPassword(String email, String password) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;
        Users user = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

            pst = con.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, password);
            rs = pst.executeQuery();

            if (rs.next()) {
                user = new Users();
                user.setUserId(rs.getInt("u_id"));
                user.setUserName(rs.getString("u_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setPassword(rs.getString("password"));
                user.setStatus(rs.getString("status"));

                try {
                    String roleStr = rs.getString("role");
                    if (roleStr != null) {
                        user.setRole(UsersRole.valueOf(roleStr));
                    } else {
                        user.setRole(UsersRole.MEMBER);
                    }
                } catch (IllegalArgumentException e) {
                    user.setRole(UsersRole.MEMBER);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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
        return user;
    }
    public boolean updateStatus(int userId, String newStatus) {
        Database db = new Database();
        db.connect();
        Connection con = db.con;

        if (con == null) {
            System.out.println("Lỗi kết nối Database");
            return false;
        }

        PreparedStatement pst = null;
        boolean isSuccess = false;

        try {
            String sql = "UPDATE users SET status = ? WHERE u_id = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, newStatus);
            pst.setInt(2, userId);
            int result = pst.executeUpdate();

            if (result > 0) {
                System.out.println("Thành công");
                isSuccess = true;
            } else {
                System.out.println("Không tìm thấy ID " + userId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
        return isSuccess;
    }
    public Users selectById(int id) {
        Users u = null;
        try {
            Database db = new Database();
            db.connect();
            String sql = "SELECT * FROM users WHERE u_id = ?";
            PreparedStatement pst = db.con.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                u = new Users();
                u.setUserId(rs.getInt("u_id"));
                u.setUserName(rs.getString("u_name"));
                u.setEmail(rs.getString("email"));
                u.setPhone(rs.getString("phone"));
                u.setPassword(rs.getString("password"));
                u.setRole(UsersRole.valueOf(rs.getString("role")));
            }
            db.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }
}