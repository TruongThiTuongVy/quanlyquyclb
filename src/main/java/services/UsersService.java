package services;


import models.Users;

import java.util.List;

public interface UsersService {

    Users login(String email, String password);

    boolean createUser(Users user);

    boolean updateUser(Users user);

    boolean deleteUser(int userId);

    Users getUserById(int userId);

    List<Users> getAllUsers();
    boolean updateUserStatus(int userId, String status);
}

