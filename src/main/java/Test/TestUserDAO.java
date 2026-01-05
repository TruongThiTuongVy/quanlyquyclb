package Test;

import DAO.UsersDAO;
import models.Users;
import models.UsersRole;

import java.util.List;

public class TestUserDAO {
    public static void main(String[] args) {

        UsersDAO usersDAO = UsersDAO.getInstance();

        // 1. INSERT
        Users u = new Users();
        u.setUserId(1);
        u.setUserName("Nguyen Van A");
        u.setEmail("a@gmail.com");
        u.setPhone("0123456789");
        u.setRole(UsersRole.MEMBER);
        u.setPassword("123456");

        System.out.println("Insert: " + usersDAO.insert(u));

        // 2. SELECT ALL
        List<Users> list = usersDAO.selectAll();
        for (Users user : list) {
            System.out.println(user.getUserId() + " - " + user.getUserName());
        }

        // 3. UPDATE
        u.setUserName("Nguyen Van A Updated");
        System.out.println("Update: " + usersDAO.update(u));

        // 4. DELETE
        System.out.println("Delete: " + usersDAO.delete(u));
    }
}

