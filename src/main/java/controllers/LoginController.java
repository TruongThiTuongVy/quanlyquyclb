package controllers;

import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import main.DashboardScene; // Import màn hình chính
import main.MainApp;
import models.Users;
import models.UsersRole;
import views.LoginView;
import javafx.stage.Stage;

public class LoginController {
    private final LoginView view;
    private final MainApp mainApp;
    private final UsersController usersController = new UsersController();
    public LoginController(LoginView view, MainApp mainApp) {
        this.view = view;
        this.mainApp = mainApp;
        attachEvents();
    }

    private void attachEvents() {
        view.getBtnSignIn().setOnAction(e -> handleLogin());
        view.getPasswordField().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
    }

    private void handleLogin() {
        String email = view.getEmail();
        String pass = view.getPassword();
        String roleString = view.getSelectedRole();
        UsersRole selectedRole;
        try {
            selectedRole = UsersRole.valueOf(roleString);
        } catch (IllegalArgumentException e) {
            selectedRole = UsersRole.MEMBER;
        }
        Users user = usersController.handleLogin(email, pass, selectedRole);
        if (user != null) {
            UsersController.currentUser = user;
            showAlert("Thành công", "Xin chào " + user.getUserName(), Alert.AlertType.INFORMATION);
            openDashboard(user);
        } else {
            showAlert("Thất bại", "Sai email, mật khẩu hoặc sai Vai trò!", Alert.AlertType.ERROR);
        }
    }
    private void openDashboard(Users user) {
        try {
            Stage currentStage = (Stage) view.getBtnSignIn().getScene().getWindow();
            DashboardScene dashboard = new DashboardScene(currentStage, user);
            dashboard.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở Dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}