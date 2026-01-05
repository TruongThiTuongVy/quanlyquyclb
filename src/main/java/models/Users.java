package models;

public class Users {
    private int userId;
    private String userName;
    private String email;
    private String phone;
    private String password;
    private UsersRole role;
    private String status;
    private java.math.BigDecimal totalPaid;
    public Users() {

    }

    public Users(String userName, UsersRole role) {
        this.userName = userName;
        this.role = role;
    }

    public Users(int userId, String userName, String email, String phone,
                 String password, UsersRole role, String status) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.status = status; // Thêm dòng này
    }
    public Users(String user_name, String email, String phone,
                 String password, UsersRole role) {
        this.userName = user_name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    //Getter, Setter

    public java.math.BigDecimal getTotalPaid() {
        return totalPaid != null ? totalPaid : java.math.BigDecimal.ZERO;
    }

    public void setTotalPaid(java.math.BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UsersRole getRole() {
        return role;
    }

    public void setRole(UsersRole role) {
        this.role = role;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "users{" + "user_id: " + userId +
                ", user_name: " + userName +
                ", email: " + email +
                ", phone: " + phone +
                ", role: " + role + '}';
    }
    public boolean isAdmin() {
        return this.role == UsersRole.ADMIN;
    }
}




