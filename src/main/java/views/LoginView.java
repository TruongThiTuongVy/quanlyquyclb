package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.util.Objects;

public class LoginView {

    private TextField txtEmail;
    private PasswordField txtPassword;
    private ToggleGroup roleGroup;
    private Button btnSignIn;
    private Hyperlink linkForgot;

    public Scene getScene() {
        HBox root = new HBox();
        root.setPrefSize(1000, 600);

        // --- 1. ẢNH BÊN TRÁI ---
        StackPane leftPane = new StackPane();
        leftPane.setPrefWidth(450);
        leftPane.getStyleClass().add("left-pane"); // CSS gradient tím

        try {
            // Đảm bảo bạn có file banner.png trong thư mục resources/images
            ImageView img = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/nen.png"))));
            img.setFitWidth(400); img.setPreserveRatio(true);
            leftPane.getChildren().add(img);
        } catch (Exception e) { /* Bỏ qua nếu chưa có ảnh */ }

        // --- 2. FORM BÊN PHẢI ---
        VBox rightPane = new VBox(20);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(40, 60, 40, 60));
        rightPane.setStyle("-fx-background-color: white;");
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        // Header
        Label lblTitle = new Label("WELCOME TO\nFUNDS MANAGEMENT VIC");
        lblTitle.getStyleClass().add("title-text");
        lblTitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label lblSub = new Label("Sign in to VIC");
        lblSub.getStyleClass().add("subtitle-text");

        // Inputs
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email");
        txtEmail.getStyleClass().add("input-field");
        this.txtEmail = txtEmail;

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Password");
        txtPass.getStyleClass().add("input-field");
        this.txtPassword = txtPass;

        // --- ROLE SELECTION (Giống ảnh) ---
        Label lblRole = new Label("Select Your Role");
        lblRole.setStyle("-fx-text-fill: #666;");

        HBox roleBox = new HBox(15);
        roleBox.setAlignment(Pos.CENTER);

        ToggleButton btnMember = createRoleButton("Member", "MEMBER");
        ToggleButton btnAdmin = createRoleButton("Admin", "ADMIN");
        btnMember.setSelected(true); // Mặc định chọn Member

        roleGroup = new ToggleGroup();
        btnMember.setToggleGroup(roleGroup);
        btnAdmin.setToggleGroup(roleGroup);

        roleBox.getChildren().addAll(btnMember, btnAdmin);

        // Button Sign In
        btnSignIn = new Button("Sign in");
        btnSignIn.setMaxWidth(Double.MAX_VALUE);
        btnSignIn.getStyleClass().add("btn-signin");

        linkForgot = new Hyperlink("Forgot your password ?");
        linkForgot.getStyleClass().add("link-forgot");

        // Ghép nối
        rightPane.getChildren().addAll(lblTitle, lblSub, txtEmail, txtPass, lblRole, roleBox, btnSignIn, linkForgot);
        root.getChildren().addAll(leftPane, rightPane);

        Scene scene = new Scene(root);
        try {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        } catch (Exception e) { e.printStackTrace(); }
        return scene;
    }

    private ToggleButton createRoleButton(String text, String userData) {
        ToggleButton btn = new ToggleButton(text);
        btn.setUserData(userData);
        btn.setPrefSize(120, 80);
        btn.getStyleClass().add("role-button");
        // Bạn có thể thêm setGraphic(ImageView) ở đây nếu muốn icon
        return btn;
    }

    // --- Getters ---
    public String getEmail() { return txtEmail.getText(); }
    public String getPassword() { return txtPassword.getText(); }
    public Button getBtnSignIn() { return btnSignIn; }
    public PasswordField getPasswordField() { return txtPassword; }
    public String getSelectedRole() {
        if (roleGroup.getSelectedToggle() != null) {
            return roleGroup.getSelectedToggle().getUserData().toString();
        }
        return "MEMBER";
    }
}