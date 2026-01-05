package services;

import DAO.UsersDAO;
import models.Users;

import java.util.List;

public class UsersService_Impl implements UsersService {
    private UsersDAO usersDAO = UsersDAO.getInstance();

    @Override
    public Users login(String email, String password) {
        if (email == null || password == null) return null;
        return usersDAO.selectByEmailAndPassword(email, password);
    }

    @Override
    public boolean createUser(Users user) {
        if (user == null) return false;
        if (user.getUserName() == null || user.getEmail() == null) return false;
        return usersDAO.insert(user) > 0;

    }

    @Override
    public boolean updateUser(Users user) {
        if (user == null || user.getUserId() <= 0) return false;
        return usersDAO.update(user) > 0;
    }

    @Override
    public boolean deleteUser(int userId) {
        if (userId <= 0) return false;

        Users u = new Users();
        u.setUserId(userId);

        return usersDAO.delete(u);
    }

    @Override
    public Users getUserById(int userId) {
        Users u = new Users();
        u.setUserId(userId);
        return usersDAO.selectById(userId);
    }
    public boolean approveUser(int userId) {
        Users u = getUserById(userId);
        if (u != null) {
            u.setStatus("ACTIVE");
            return updateUser(u);
        }
        return false;
    }

    @Override
    public List<Users> getAllUsers() {
        return usersDAO.selectAll();
    }

    @Override
    public boolean updateUserStatus(int userId, String status) {
        return usersDAO.updateStatus(userId, status);
    }
}
