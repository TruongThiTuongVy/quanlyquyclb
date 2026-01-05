package controllers;

import DAO.UsersDAO;
import models.Users;
import models.UsersRole;

public class UsersController {
    private UsersDAO usersDAO = UsersDAO.getInstance();
    public static Users currentUser = null;
    public Users handleLogin(String email, String password, UsersRole selectedRole) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        Users user = usersDAO.selectByEmailAndPassword(email, password);
        if (user != null && user.getRole() == selectedRole) {
            return user;
        }
        return null;
    }
    public String handleSignUp(String name, String email, String phone, String password, UsersRole role) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return "VUI_LONG_DIEN_DU";
        }
        String error = utils.Validator.validateRegister(name, email, phone, password);
        if (!error.equals("OK")) {
            utils.Validator.showError(error);
            return error;
        }
        if (usersDAO.checkEmailExists(email)) {
            return "EMAIL_DA_TON_TAI";
        }

        String status = (role == UsersRole.ADMIN) ? "PENDING" : "ACTIVE";

        Users newUser = new Users(name, email, phone, password, role);
        if (role == UsersRole.ADMIN) {
            newUser.setStatus("PENDING");
        } else {
            newUser.setStatus("ACTIVE");
        }
        int result = usersDAO.insert(newUser);
        newUser.setStatus(status);
        if (result > 0) {
            return "SUCCESS";
        } else {
            return "LOI_HE_THONG";
        }
    }

}