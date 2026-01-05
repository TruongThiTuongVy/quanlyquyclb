package main;

import controllers.UsersController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Users;
import models.UsersRole;

public class MainApp extends Application {

    private StackPane contentArea;
    private UsersController userController = new UsersController();
    private UsersRole selectedRole = UsersRole.MEMBER;
    private Button btnMember;
    private Button btnAdmin;

    @Override
    public void start(Stage primaryStage) {

        VBox leftPane = createLeftBanner();

        contentArea = new StackPane();
        contentArea.setPrefWidth(400);
        showSelectionScreen();

        HBox root = new HBox(leftPane, contentArea);
        Scene scene = new Scene(root, 850, 550);

        loadStyleSheet(scene);

        primaryStage.setTitle("VIC Funds Management");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private VBox createLeftBanner() {
        VBox pane = new VBox();
        pane.setAlignment(Pos.CENTER);
        pane.setPrefWidth(450);

        try {
            Image image = new Image(getClass().getResourceAsStream("/images/nen.png"));
            ImageView banner = new ImageView(image);
            banner.setFitWidth(450);
            banner.setFitHeight(550);
            banner.setPreserveRatio(false);
            pane.getChildren().add(banner);
        } catch (Exception e) {
            System.out.println("Lỗi: Không tìm thấy ảnh tại /images/nen.png");
            pane.setStyle("-fx-background-color: #2e7d32;"); // Màu xanh dự phòng
        }
        return pane;
    }
    private void showSelectionScreen() {
        VBox box = new VBox(25);
        box.getStyleClass().add("right-pane");
        box.setAlignment(Pos.CENTER);

        Label welcome = new Label("WELCOME TO VIC");
        welcome.getStyleClass().add("welcome-label");

        Button btnSignIn = new Button("SIGN IN");
        btnSignIn.getStyleClass().add("select-button-signin");
        btnSignIn.setOnAction(e -> showSignInForm());

        Button btnSignUp = new Button("SIGN UP");
        btnSignUp.getStyleClass().add("select-button-signup");
        btnSignUp.setOnAction(e -> showSignUpForm());

        box.getChildren().addAll(welcome, btnSignIn, btnSignUp);
        contentArea.getChildren().setAll(box);
    }

    private void showSignInForm() {
        VBox form = new VBox(15);
        form.getStyleClass().add("right-pane");
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30));

        Label title = new Label("SIGN IN TO VIC");
        title.getStyleClass().add("form-title");

        TextField emailField = new TextField();
        emailField.setPromptText("✉   Email");
        emailField.getStyleClass().add("input-field");

        PasswordField passField = new PasswordField();
        passField.setPromptText("🔒   Password");
        passField.getStyleClass().add("input-field");

        Label roleL = new Label("Select Your Role");
        HBox roles = createRoleSelectionBox();

        Button loginBtn = new Button("Sign in");
        loginBtn.getStyleClass().add("primary-button");

        loginBtn.setOnAction(e -> {

            Users user = userController.handleLogin(emailField.getText(), passField.getText(), selectedRole);

            if (user != null) {
                if ("PENDING".equals(user.getStatus())) {
                    showAlert(Alert.AlertType.WARNING, "Chờ xét duyệt", "Tài khoản Admin của bạn đang chờ duyệt!");
                    return;
                }
                UsersController.currentUser = user;
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Chào mừng " + user.getUserName() + "!");
                Stage currentStage = (Stage) loginBtn.getScene().getWindow();
                DashboardScene dashboard = new DashboardScene(currentStage, user);
                dashboard.show();

            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Sai tài khoản, mật khẩu hoặc vai trò!");
            }
        });

        Hyperlink switchL = new Hyperlink("Don't have an account? Sign up");
        switchL.setOnAction(e -> showSignUpForm());

        form.getChildren().addAll(title, emailField, passField, roleL, roles, loginBtn, switchL);
        contentArea.getChildren().setAll(form);
    }
    private void showSignUpForm() {
        VBox form = new VBox(12);
        form.getStyleClass().add("right-pane");
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30));

        Label title = new Label("CREATE ACCOUNT");
        title.getStyleClass().add("form-title");

        TextField nameField = new TextField();
        nameField.setPromptText("👤   Full Name");
        nameField.getStyleClass().add("input-field");

        TextField emailField = new TextField();
        emailField.setPromptText("✉   Email Address");
        emailField.getStyleClass().add("input-field");

        TextField phoneField = new TextField();
        phoneField.setPromptText("📞   Phone Number");
        phoneField.getStyleClass().add("input-field");

        PasswordField passField = new PasswordField();
        passField.setPromptText("🔒   Password");
        passField.getStyleClass().add("input-field");

        Label roleL = new Label("Select Your Role");
        HBox roles = createRoleSelectionBox();

        Button regBtn = new Button("Sign up");
        regBtn.getStyleClass().add("primary-button");
        regBtn.setOnAction(e -> {
            String result = userController.handleSignUp(
                    nameField.getText(), emailField.getText(),
                    phoneField.getText(), passField.getText(), selectedRole
            );

            if (result.equals("SUCCESS")) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký hoàn tất! Vui lòng đăng nhập.");
                showSignInForm();
            } else {
                String errorMsg = result.equals("EMAIL_DA_TON_TAI") ? "Email này đã được sử dụng!" : "Vui lòng điền đủ thông tin!";
                showAlert(Alert.AlertType.ERROR, "Lỗi", errorMsg);
            }
        });

        Hyperlink switchL = new Hyperlink("Already have an account? Sign in");
        switchL.setOnAction(e -> showSignInForm());

        form.getChildren().addAll(title, nameField, emailField, phoneField, passField, roleL, roles, regBtn, switchL);
        contentArea.getChildren().setAll(form);
    }
    private HBox createRoleSelectionBox() {
        btnMember = createRoleBtn("👥", "Member", UsersRole.MEMBER);
        btnAdmin = createRoleBtn("👤", "Admin", UsersRole.ADMIN);

        updateRoleButtonStyle();

        HBox box = new HBox(15, btnMember, btnAdmin);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Button createRoleBtn(String icon, String text, UsersRole role) {
        VBox v = new VBox(5, new Label(icon), new Label(text));
        v.setAlignment(Pos.CENTER);
        Button b = new Button();
        b.setGraphic(v);
        b.getStyleClass().add("role-button");
        b.setPrefSize(90, 70);

        b.setOnAction(e -> {
            selectedRole = role;
            updateRoleButtonStyle();
        });
        return b;
    }

    private void updateRoleButtonStyle() {
        String normal = "-fx-border-color: #ddd; -fx-background-color: transparent; -fx-border-radius: 8;";
        String active = "-fx-border-color: #2e7d32; -fx-background-color: #e8f5e9; -fx-border-width: 2px; -fx-border-radius: 8;";

        btnMember.setStyle(selectedRole == UsersRole.MEMBER ? active : normal);
        btnAdmin.setStyle(selectedRole == UsersRole.ADMIN ? active : normal);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadStyleSheet(Scene scene) {
        try {
            var url = getClass().getResource("/styles.css");
            if (url != null) scene.getStylesheets().add(url.toExternalForm());
        } catch (Exception e) {
            System.out.println("Không tìm thấy file styles.css");
        }
    }

    public static void main(String[] args) { launch(args); }
}