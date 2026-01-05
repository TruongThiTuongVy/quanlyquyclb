package main;

import DAO.UsersDAO;
import DB_Connect.Database;
import controllers.FeeController;
import controllers.UsersController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Fee;
import models.Users;
import models.UsersRole;
import services.UsersService_Impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.zone.ZoneRulesProvider;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DashboardScene {

    private Stage stage;
    private Users currentUser;
    private BorderPane mainLayout;
    private StackPane contentArea;
    private FeeController feeController = new FeeController();
    private Button btnDashboard, btnProfile, btnMyFees, btnNotifications, btnHistory;
    private Button btnAdminMembers, btnAdminFunds, btnAdminNotifs;
    private java.time.YearMonth currentChartMonth = java.time.YearMonth.now();
    public DashboardScene(Stage stage, Users user) {
        this.stage = stage;
        this.currentUser = user;
    }
    public void show() {
        mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("dashboard-background");
        mainLayout.setLeft(createSidebar());
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.setAlignment(Pos.TOP_LEFT);
        showHomeDashboard();
        mainLayout.setCenter(contentArea);
        Scene scene = new Scene(mainLayout, 1100, 700);
        try {
            scene.getStylesheets().add(getClass().getResource("/stylescene2.css").toExternalForm());
        } catch (Exception e) { System.out.println("Chưa có CSS"); }
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    private VBox createSidebar() {
        VBox box = new VBox(10);
        box.setPrefWidth(240);
        box.getStyleClass().add("sidebar");
        box.setPadding(new Insets(20, 0, 20, 0));

        // Logo
        ImageView logo = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo.png"))));
        logo.setFitWidth(250); logo.setPreserveRatio(true);

        /*Label logo = new Label("VKU IT CLUB");
        logo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #7c73e6;");
        */

        VBox logoBox = new VBox(logo);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(0, 0, 30, 0));
        btnDashboard = createMenuButton("📊  Tổng quan", true);
        btnDashboard.setOnAction(e -> showHomeDashboard());

        btnProfile = createMenuButton("👤  Hồ sơ cá nhân", false);
        btnProfile.setOnAction(e -> showProfileView());

        btnMyFees = createMenuButton("💰  Đóng quỹ", false);
        btnMyFees.setOnAction(e -> showMyFeesView());

        btnHistory = createMenuButton("📜  Lịch sử nộp", false);
        btnHistory.setOnAction(e -> showHistoryView());

        btnNotifications = createMenuButton("🔔  Thông báo", false);
        btnNotifications.setOnAction(e -> showNotificationView());

        box.getChildren().addAll(logoBox, btnDashboard, btnProfile, btnMyFees, btnHistory, btnNotifications);

        // --- MENU ADMIN ---
        if (currentUser.getRole() == UsersRole.ADMIN) {
            Label lblAdmin = new Label("QUẢN TRỊ VIÊN");
            lblAdmin.setPadding(new Insets(20, 0, 5, 20));
            lblAdmin.setStyle("-fx-text-fill: #999; -fx-font-size: 12px; -fx-font-weight: bold;");

            btnAdminMembers = createMenuButton("👥  Quản lý Thành viên", false);
            btnAdminMembers.setOnAction(e -> showAdminMemberManagement());

            btnAdminFunds = createMenuButton("💵  Quản lý Thu Chi", false);
            btnAdminFunds.setOnAction(e -> showAdminFundManagement());

            btnAdminNotifs = createMenuButton("📢  Gửi Thông báo", false);
            btnAdminNotifs.setOnAction(e -> showAdminSendNotification());

            box.getChildren().addAll(lblAdmin, btnAdminMembers, btnAdminFunds, btnAdminNotifs);
        }
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Hyperlink logoutLink = new Hyperlink("Đăng xuất");
        logoutLink.setPadding(new Insets(0, 0, 20, 20));
        logoutLink.setOnAction(e -> {
            try { new MainApp().start(stage); } catch (Exception ex) { ex.printStackTrace(); }
        });
        box.getChildren().addAll(spacer, logoutLink);
        return box;
    }
    private void showAdminFundManagement() {
        resetActiveButtons(btnAdminFunds);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        VBox layout = new VBox(25);
        layout.setPadding(new Insets(10));
        Label titleIncome = new Label("1. Quản lý Quỹ & Khoản thu");
        titleIncome.getStyleClass().add("section-title");

        Button btnAddFee = new Button("+ Tạo khoản thu mới");
        btnAddFee.setStyle("-fx-background-color: #7c73e6; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAddFee.setOnAction(e -> showCreateFeeDialog());
        TableView<models.Fee> tableFee = new TableView<>();
        tableFee.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableFee.setPrefHeight(200);
        TableColumn<models.Fee, String> colTitle = new TableColumn<>("Tên quỹ");
        colTitle.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));

        TableColumn<models.Fee, String> colTarget = new TableColumn<>("Mục tiêu");
        colTarget.setCellValueFactory(cell -> new SimpleStringProperty(java.text.NumberFormat.getCurrencyInstance(new Locale("vi","VN")).format(cell.getValue().getTargetAmount())));

        TableColumn<models.Fee, String> colAmount = new TableColumn<>("Tiền thu");
        colAmount.setCellValueFactory(cell -> new SimpleStringProperty(java.text.NumberFormat.getCurrencyInstance(new Locale("vi","VN")).format(cell.getValue().getAmount())));

        TableColumn<models.Fee, Void> colProgress = new TableColumn<>("Tiến độ");
        colProgress.setCellFactory(param -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink();
            {
                link.setOnAction(e -> {
                    models.Fee fee = getTableView().getItems().get(getIndex());
                    showFeeDetailPopup(fee);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); }
                else {
                    models.Fee f = getTableView().getItems().get(getIndex());
                    link.setText(f.getPaidCount() + " người (chi tiết)");
                    setGraphic(link);
                }
            }
        });

        TableColumn<models.Fee, Void> colAction = new TableColumn<>("Xóa");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDel = new Button("X");
            {
                btnDel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnDel.setOnAction(e -> deleteFee(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDel);
            }
        });
        tableFee.getColumns().addAll(colTitle, colTarget, colAmount, colProgress, colAction);
        try { tableFee.setItems(FXCollections.observableArrayList(DAO.FeesDAO.getInstance().selectAll())); } catch(Exception e){}
        HBox summaryBox = new HBox(30);
        summaryBox.setAlignment(Pos.CENTER);
        summaryBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 10;");
        DAO.StatisticsDAO stats = DAO.StatisticsDAO.getInstance();
        java.text.NumberFormat vnMoney = java.text.NumberFormat.getCurrencyInstance(new Locale("vi","VN"));
        VBox sum1 = createSummaryLabel("Tổng Mục Tiêu", vnMoney.format(stats.getTotalTarget()), "#2980b9");
        VBox sum2 = createSummaryLabel("Quỹ Hiện Có", vnMoney.format(stats.getTotalBalance()), "#27ae60");
        VBox sum3 = createSummaryLabel("Tổng Đã Chi", vnMoney.format(stats.getTotalExpense()), "#e74c3c");

        summaryBox.getChildren().addAll(sum1, sum2, sum3);

        // --- PHẦN 3: QUẢN LÝ CHI TIÊU (MỚI) ---

        Label titleExpense = new Label("2. Quản lý Chi tiêu");
        titleExpense.getStyleClass().add("section-title");

        Button btnAddExpense = new Button("- Tạo khoản chi mới");
        btnAddExpense.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAddExpense.setOnAction(e -> showCreateExpenseDialog()); // Hàm mới

        TableView<models.Transaction> tableExpense = new TableView<>();
        tableExpense.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableExpense.setPrefHeight(200);

        TableColumn<models.Transaction, String> colExContent = new TableColumn<>("Nội dung chi");
        colExContent.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTNote()));

        TableColumn<models.Transaction, String> colExAmount = new TableColumn<>("Số tiền");
        colExAmount.setCellValueFactory(cell -> new SimpleStringProperty(vnMoney.format(cell.getValue().getTAmount())));
        colExAmount.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;"); // Màu đỏ cho tiền chi

        TableColumn<models.Transaction, String> colExDate = new TableColumn<>("Ngày chi");
        colExDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCreateAt().toString()));

        tableExpense.getColumns().addAll(colExContent, colExAmount, colExDate);
        try {
            tableExpense.setItems(FXCollections.observableArrayList(DAO.TransactionDAO.getInstance().getAllExpenses()));
        } catch(Exception e){}
        layout.getChildren().addAll(titleIncome, btnAddFee, tableFee, summaryBox, titleExpense, btnAddExpense, tableExpense);

        scrollPane.setContent(layout);
        contentArea.getChildren().setAll(scrollPane);
    }

    private void showFeeDetailPopup(models.Fee fee) {
        Stage dialog = new Stage();
        dialog.setTitle("Quản lý đóng quỹ: " + fee.getTitle());
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setPrefWidth(600);
        List<DAO.FeesDAO.MemberFeeStatusRow> allMembers = DAO.FeesDAO.getInstance().getMembersByFeeId(fee.getFId());
        ObservableList<DAO.FeesDAO.MemberFeeStatusRow> pendingList = FXCollections.observableArrayList();
        ObservableList<DAO.FeesDAO.MemberFeeStatusRow> paidList = FXCollections.observableArrayList();
        ObservableList<DAO.FeesDAO.MemberFeeStatusRow> unpaidList = FXCollections.observableArrayList();

        for (DAO.FeesDAO.MemberFeeStatusRow row : allMembers) {
            String s = row.getStatus() != null ? row.getStatus().trim() : "UNPAID";
            if ("PAID".equals(s)) paidList.add(row);
            else if ("PENDING".equals(s)) pendingList.add(row);
            else unpaidList.add(row);
        }

        Label lblPending = new Label("⏳ Yêu cầu chờ duyệt (" + pendingList.size() + ")");
        lblPending.setStyle("-fx-font-weight: bold; -fx-text-fill: #e67e22;"); // Màu cam

        TableView<DAO.FeesDAO.MemberFeeStatusRow> tablePending = createDetailTable();
        // Thêm cột Duyệt cho bảng Pending
        TableColumn<DAO.FeesDAO.MemberFeeStatusRow, Void> colApprove = new TableColumn<>("Hành động");
        colApprove.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Duyệt");
            {
                btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                btn.setOnAction(e -> {
                    DAO.FeesDAO.MemberFeeStatusRow row = getTableView().getItems().get(getIndex());
                    handleApprovePayment(row, fee, dialog); // HÀM DUYỆT
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        tablePending.getColumns().add(colApprove);
        tablePending.setItems(pendingList);
        tablePending.setPrefHeight(150);

        // 2. BẢNG ĐÃ NỘP
        Label lblPaid = new Label("✅ Đã nộp (" + paidList.size() + ")");
        lblPaid.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");
        TableView<DAO.FeesDAO.MemberFeeStatusRow> tablePaid = createDetailTable();
        tablePaid.setItems(paidList);
        tablePaid.setPrefHeight(150);

        // 3. BẢNG CHƯA NỘP
        Label lblUnpaid = new Label("❌ Chưa nộp (" + unpaidList.size() + ")");
        lblUnpaid.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        TableView<DAO.FeesDAO.MemberFeeStatusRow> tableUnpaid = createDetailTable();
        tableUnpaid.setItems(unpaidList);
        tableUnpaid.setPrefHeight(150);

        layout.getChildren().addAll(lblPending, tablePending, lblPaid, tablePaid, lblUnpaid, tableUnpaid);

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);
        dialog.setScene(new Scene(scroll));
        dialog.show();
    }

    // --- HÀM XỬ LÝ KHI ADMIN BẤM "DUYỆT"
    private void handleApprovePayment(DAO.FeesDAO.MemberFeeStatusRow row, models.Fee fee, Stage dialog) {
        Users u = DAO.UsersDAO.getInstance().selectByEmail(row.getEmail());
        if (u == null) return;
        Database db = new Database(); db.connect();
        try {

            String sqlUpdate = "UPDATE user_fees SET status='PAID', paid_date=CURRENT_DATE WHERE u_id=? AND f_id=?";
            PreparedStatement pst = db.con.prepareStatement(sqlUpdate);
            pst.setInt(1, u.getUserId());
            pst.setInt(2, fee.getFId());
            pst.executeUpdate();
            models.Transaction t = new models.Transaction();
            t.setUserId(u.getUserId());
            t.setFcId(1);
            t.setTType(models.transactionType.INCOME);
            t.setTAmount(fee.getAmount());
            t.setTNote("Thu phí: " + fee.getTitle() + " từ " + u.getUserName());
            DAO.TransactionDAO.getInstance().insert(t); // Lưu vào bảng Transaction
            models.Notification n = new models.Notification(u.getUserId(), "✅ Thanh toán thành công",
                    "Khoản đóng '" + fee.getTitle() + "' của bạn đã được Admin duyệt.");
            DAO.NotificationDAO.getInstance().insert(n);
            showAlert("Thành công", "Đã duyệt và ghi nhận doanh thu!");
            dialog.close(); // Đóng popup để refresh
            showAdminFundManagement(); // Load lại trang Admin

        } catch(Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Có lỗi xảy ra khi duyệt.");
        }
    }

    private TableView<DAO.FeesDAO.MemberFeeStatusRow> createDetailTable() {
        TableView<DAO.FeesDAO.MemberFeeStatusRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DAO.FeesDAO.MemberFeeStatusRow, String> colName = new TableColumn<>("Họ tên");
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));

        TableColumn<DAO.FeesDAO.MemberFeeStatusRow, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));

        TableColumn<DAO.FeesDAO.MemberFeeStatusRow, String> colDate = new TableColumn<>("Ngày nộp");
        colDate.setCellValueFactory(cell -> {
            Date d = cell.getValue().getPaidDate();
            return new SimpleStringProperty(d == null ? "---" : new java.text.SimpleDateFormat("dd/MM/yyyy").format(d));
        });

        table.getColumns().addAll(colName, colEmail, colDate);
        return table;
    }

    private HBox createSummaryBox() {
        HBox summaryBox = new HBox(30);
        summaryBox.setAlignment(Pos.CENTER);
        summaryBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 10;");

        DAO.StatisticsDAO stats = DAO.StatisticsDAO.getInstance();
        java.text.NumberFormat vnMoney = java.text.NumberFormat.getCurrencyInstance(new Locale("vi","VN"));

        summaryBox.getChildren().addAll(
                createSummaryLabel("Tổng Mục Tiêu", vnMoney.format(stats.getTotalTarget()), "#2980b9"),
                createSummaryLabel("Quỹ Hiện Có", vnMoney.format(stats.getTotalBalance()), "#27ae60"),
                createSummaryLabel("Tổng Đã Chi", vnMoney.format(stats.getTotalExpense()), "#e74c3c")
        );
        return summaryBox;
    }

    // Hàm tạo phần Quản lý Chi tiêu
    private VBox createExpenseSection() {
        VBox box = new VBox(15);
        Label titleExpense = new Label("2. Quản lý Chi tiêu & Hóa đơn");
        titleExpense.getStyleClass().add("section-title");

        Button btnAddExpense = new Button("- Tạo khoản chi mới");
        btnAddExpense.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAddExpense.setOnAction(e -> showCreateExpenseDialog());

        TableView<models.Transaction> tableExpense = new TableView<>();
        tableExpense.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableExpense.setPrefHeight(200);

        java.text.NumberFormat vnMoney = java.text.NumberFormat.getCurrencyInstance(new Locale("vi","VN"));

        TableColumn<models.Transaction, String> colExContent = new TableColumn<>("Nội dung chi");
        colExContent.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTNote()));

        TableColumn<models.Transaction, String> colExAmount = new TableColumn<>("Số tiền");
        colExAmount.setCellValueFactory(cell -> new SimpleStringProperty(vnMoney.format(cell.getValue().getTAmount())));
        colExAmount.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");

        TableColumn<models.Transaction, String> colExDate = new TableColumn<>("Ngày chi");
        colExDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCreateAt().toString()));

        tableExpense.getColumns().addAll(colExContent, colExAmount, colExDate);
        try { tableExpense.setItems(FXCollections.observableArrayList(DAO.TransactionDAO.getInstance().getAllExpenses())); } catch(Exception e){}

        box.getChildren().addAll(titleExpense, btnAddExpense, tableExpense);
        return box;
    }
    private VBox createSummaryLabel(String title, String value, String color) {
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        VBox box = new VBox(5, lblTitle, lblValue);
        box.setAlignment(Pos.CENTER);
        return box;
    }
    private void showCreateExpenseDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Tạo khoản chi tiêu mới");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox form = new VBox(15);
        form.setPadding(new Insets(20));

        TextField txtContent = new TextField();
        txtContent.setPromptText("Nội dung chi (VD: Mua nước, Thuê sân...)");

        TextField txtAmount = new TextField();
        txtAmount.setPromptText("Số tiền chi (VNĐ)");
        Label lblDate = new Label("Ngày tạo: " + java.time.LocalDate.now());
        Button btnConfirm = new Button("Xác nhận Chi & Gửi thông báo");
        btnConfirm.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnConfirm.setOnAction(e -> {
            String content = txtContent.getText();
            String amountStr = txtAmount.getText();
            String error = utils.Validator.validateMoneyForm(content, amountStr);
            if (!error.equals("OK")) {
                utils.Validator.showError(error);
                return;
            }

            if (content.isEmpty() || amountStr.isEmpty()) {
                showAlert("Lỗi", "Vui lòng nhập đủ thông tin!");
                return;
            }

            try {
                java.math.BigDecimal amount = new java.math.BigDecimal(amountStr);
                if (DAO.TransactionDAO.getInstance().createExpense(content, amount)) {
                    String msg = "Admin vừa tạo khoản chi mới:\n" +
                            "- Nội dung: " + content + "\n" +
                            "- Số tiền: " + java.text.NumberFormat.getCurrencyInstance(new Locale("vi","VN")).format(amount);
                    models.Notification notif = new models.Notification(0, "💸 Thông báo chi tiêu quỹ", msg);
                    DAO.NotificationDAO.getInstance().insert(notif);
                    showAlert("Thành công", "Đã tạo khoản chi và thông báo cho toàn bộ CLB!");
                    dialog.close();
                    showAdminFundManagement(); // Load lại trang
                } else {
                    showAlert("Lỗi", "Không thể lưu giao dịch.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Số tiền không hợp lệ.");
            }
        });

        form.getChildren().addAll(
                new Label("Nội dung chi tiêu:"), txtContent,
                new Label("Số tiền:"), txtAmount,
                lblDate,
                new Separator(),
                btnConfirm
        );
        dialog.setScene(new Scene(form, 350, 300));
        dialog.show();
    }
    private void deleteFee(models.Fee fee) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Xóa khoản thu: " + fee.getTitle() + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            // Gọi Service xóa
            if (new services.FeeService_Impl().deleteFee(fee.getFId())) {
                showAlert("Thành công", "Đã xóa khoản thu!");
                showAdminFundManagement(); // Load lại bảng
            } else {
                showAlert("Lỗi", "Không thể xóa (Có thể đã có người đóng tiền khoản này).");
            }
        }
    }
    private void showHomeDashboard() {
        resetActiveButtons(btnDashboard);
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(15));
        Label lblWelcome = new Label("Tổng quan CLB");
        lblWelcome.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label lblDate = new Label("Cập nhật: " + java.time.LocalDate.now());
        lblDate.setStyle("-fx-text-fill: #7f8c8d;");
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        DAO.StatisticsDAO statsDAO = DAO.StatisticsDAO.getInstance();
        java.math.BigDecimal totalBalance = statsDAO.getTotalBalance(); // Đã sửa logic tính đúng
        java.math.BigDecimal totalTarget = statsDAO.getTotalTarget();
        java.math.BigDecimal totalExpense = statsDAO.getTotalExpense();
        java.text.NumberFormat vnMoney = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
        VBox card1 = createStatCard("💰 Quỹ hiện có", vnMoney.format(totalBalance), "-fx-background-color: #27ae60;");
        VBox card2 = createStatCard("🎯 Tổng mục tiêu", vnMoney.format(totalTarget), "-fx-background-color: #2980b9;");
        VBox card3 = createStatCard("💸 Tổng đã chi", vnMoney.format(totalExpense), "-fx-background-color: #e74c3c;");

        statsBox.getChildren().addAll(card1, card2, card3);
        VBox chartSection = new VBox(10);
        chartSection.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        HBox navBox = new HBox(15);
        navBox.setAlignment(Pos.CENTER);
        Button btnPrev = new Button("◀ Tháng trước");
        Button btnNext = new Button("Tháng sau ▶");
        btnPrev.setStyle("-fx-background-color: #ecf0f1; -fx-cursor: hand;");
        btnNext.setStyle("-fx-background-color: #ecf0f1; -fx-cursor: hand;");
        Label lblChartMonth = new Label();
        lblChartMonth.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Chỉ số tài chính");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số tiền (VNĐ)");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(true);
        barChart.setAnimated(false); // Tắt hiệu ứng để reload cho mượt
        VBox.setVgrow(barChart, Priority.ALWAYS);
        Runnable updateChartData = () -> {
            String monthKey = "Tháng " + currentChartMonth.getMonthValue() + "/" + currentChartMonth.getYear();
            lblChartMonth.setText("Thống kê " + monthKey);
            java.util.Map<String, double[]> monthlyStats = statsDAO.getMonthlyStats();
            String key = currentChartMonth.toString();
            double[] values = monthlyStats.getOrDefault(key, new double[]{0, 0, 0});
            barChart.getData().clear();
            barChart.layout();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(monthKey);
            XYChart.Data<String, Number> dataTarget = new XYChart.Data<>("Mục tiêu", values[0]);
            XYChart.Data<String, Number> dataIncome = new XYChart.Data<>("Thực thu", values[1]);
            XYChart.Data<String, Number> dataExpense = new XYChart.Data<>("Đã chi", values[2]);

            series.getData().addAll(dataTarget, dataIncome, dataExpense);
            barChart.getData().add(series);
            for (XYChart.Data<String, Number> data : series.getData()) {
                javafx.scene.Node node = data.getNode();
                if (node != null) {
                    String color = "";
                    switch (data.getXValue()) {
                        case "Mục tiêu": color = "#2980b9"; break; // Xanh dương
                        case "Thực thu": color = "#27ae60"; break; // Xanh lá
                        case "Đã chi":   color = "#e74c3c"; break; // Đỏ
                    }
                    node.setStyle("-fx-bar-fill: " + color + ";");
                }
            }
        };
        btnPrev.setOnAction(e -> {
            currentChartMonth = currentChartMonth.minusMonths(1);
            updateChartData.run();
        });

        btnNext.setOnAction(e -> {
            currentChartMonth = currentChartMonth.plusMonths(1);
            updateChartData.run();
        });
        updateChartData.run();

        navBox.getChildren().addAll(btnPrev, lblChartMonth, btnNext);
        chartSection.getChildren().addAll(navBox, barChart);
        VBox.setVgrow(chartSection, Priority.ALWAYS);

        layout.getChildren().addAll(lblWelcome, lblDate, statsBox, chartSection);
        contentArea.getChildren().setAll(layout);
    }
    private VBox createStatCard(String title, String value, String colorStyle) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setPrefHeight(120);
        card.setStyle(colorStyle + " -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255,255,255,0.8);");

        card.getChildren().addAll(lblValue, lblTitle);
        return card;
    }

    // 2. HỒ SƠ CÁ NHÂN
    private void showProfileView() {
        resetActiveButtons(btnProfile);

        VBox layout = new VBox(20);
        layout.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 15;");
        layout.setMaxWidth(600);

        Label title = new Label("Chỉnh sửa thông tin cá nhân");
        title.getStyleClass().add("section-title");

        TextField txtName = new TextField(currentUser.getUserName());
        TextField txtEmail = new TextField(currentUser.getEmail()); txtEmail.setEditable(false); // Email không cho sửa
        TextField txtPhone = new TextField(currentUser.getPhone());
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("Nhập mật khẩu mới nếu muốn đổi");

        Button btnSave = new Button("Lưu thay đổi");
        btnSave.setStyle("-fx-background-color: #7c73e6; -fx-text-fill: white; -fx-padding: 10 20;");
        btnSave.setOnAction(e -> {
            currentUser.setUserName(txtName.getText());
            currentUser.setPhone(txtPhone.getText());
            showAlert("Thành công", "Đã cập nhật thông tin!");
        });

        layout.getChildren().addAll(
                title,
                new Label("Họ và tên:"), txtName,
                new Label("Email:"), txtEmail,
                new Label("Số điện thoại:"), txtPhone,
                new Label("Mật khẩu mới:"), txtPass,
                btnSave
        );
        contentArea.getChildren().setAll(layout);
    }
    private void showMyFeesView() {
        resetActiveButtons(btnMyFees);
        VBox layout = new VBox(20);
        Label title = new Label("Các khoản cần đóng");
        title.getStyleClass().add("section-title");

        TableView<MemberFeeRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Cột Tên
        TableColumn<MemberFeeRow, String> colTitle = new TableColumn<>("Khoản thu");
        colTitle.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTitle()));

        // Cột Tiền
        TableColumn<MemberFeeRow, String> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(cell -> {
            String s = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi","VN")).format(cell.getValue().getAmount());
            return new javafx.beans.property.SimpleStringProperty(s);
        });

        // Cột Deadline
        TableColumn<MemberFeeRow, String> colDead = new TableColumn<>("Hạn chót");
        colDead.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getDeadline())));

        // Cột Nút Thanh toán
        TableColumn<MemberFeeRow, Void> colAction = new TableColumn<>("Thanh toán");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Nộp ngay");
            {
                btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    MemberFeeRow row = getTableView().getItems().get(getIndex());
                    handleMemberPayment(row); // Hàm xử lý nộp tiền
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(colTitle, colAmount, colDead, colAction);

        try {

            List<models.FeeStatus> unpaid = new services.FeeStatusService_Impl().getUnpaidList(currentUser.getUserId());
            ObservableList<MemberFeeRow> rows = FXCollections.observableArrayList();

            services.FeeService feeService = new services.FeeService_Impl();
            for (models.FeeStatus fs : unpaid) {

                models.Fee f = feeService.getFeeById(fs.getFId());
                if (f != null) {
                    rows.add(new MemberFeeRow(f, fs));
                }
            }

            if (rows.isEmpty()) table.setPlaceholder(new Label("Tuyệt vời! Bạn không còn khoản nợ nào."));
            table.setItems(rows);

        } catch (Exception e) { e.printStackTrace(); }

        layout.getChildren().addAll(title, table);
        contentArea.getChildren().setAll(layout);
    }

    private void handleMemberPayment(MemberFeeRow row) {
        showPaymentDialog(row);
    }

    // --- FORM THANH TOÁN QR CODE ---
    private void showPaymentDialog(MemberFeeRow row) {
        Stage dialog = new Stage();
        dialog.setTitle("Cổng thanh toán");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white;");

        Label lblTitle = new Label("Thanh toán: " + row.getTitle());
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        String qrData = "Nộp quỹ: " + row.getTitle() + " - " + currentUser.getUserName();
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + qrData.replace(" ", "%20");

        ImageView qrView = new ImageView(new javafx.scene.image.Image(qrUrl, true));
        qrView.setFitWidth(200);
        qrView.setFitHeight(200);

        Label lblInfo = new Label("Vui lòng quét mã trên hoặc chuyển khoản tới:\nSTK: 123456789 (MB Bank)\nChủ TK: VKU IT CLUB\nSố tiền: " +
                java.text.NumberFormat.getCurrencyInstance(new Locale("vi","VN")).format(row.getAmount()));
        lblInfo.setStyle("-fx-text-alignment: CENTER; -fx-line-spacing: 5;");

        Button btnConfirm = new Button("✅ Đã chuyển");
        btnConfirm.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        btnConfirm.setOnAction(e -> {
            models.FeeStatus fs = row.getStatusObj();
            fs.setStatus(models.status.valueOf("PENDING"));
            if (new services.FeeStatusService_Impl().updateFeeStatus(fs)) {
                models.Notification n = new models.Notification(1, "Yêu cầu duyệt: " + currentUser.getUserName(),
                        currentUser.getUserName() + " vừa báo đã nộp tiền cho quỹ: " + row.getTitle());
                DAO.NotificationDAO.getInstance().insert(n);

                showAlert("Thành công", "Đã gửi yêu cầu! Vui lòng chờ Admin duyệt.");
                dialog.close();
                showMyFeesView(); // Load lại bảng
            } else {
                showAlert("Lỗi", "Không thể gửi yêu cầu.");
            }
        });

        layout.getChildren().addAll(lblTitle, qrView, lblInfo, new Separator(), btnConfirm);
        dialog.setScene(new Scene(layout, 400, 550));
        dialog.show();
    }
    private void showPaymentForm(FeeModel fee) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Xác nhận nộp tiền");

        VBox form = new VBox(15);
        form.setPadding(new Insets(30));
        form.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("Thanh toán: " + fee.getTitle());
        Label lblAmount = new Label("Số tiền: " + fee.getAmount() + " VND");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        ComboBox<String> cbMethod = new ComboBox<>();
        cbMethod.getItems().addAll("Chuyển khoản (QR Code)", "Tiền mặt");
        cbMethod.setValue("Chuyển khoản (QR Code)");

        TextArea txtNote = new TextArea();
        txtNote.setPromptText("Ghi chú giao dịch (Mã giao dịch...)");
        txtNote.setPrefHeight(80);

        Button btnConfirm = new Button("Xác nhận đã chuyển");
        btnConfirm.setStyle("-fx-background-color: #7c73e6; -fx-text-fill: white;");
        btnConfirm.setOnAction(e -> {
            showAlert("Thành công", "Yêu cầu đã gửi! Chờ Admin duyệt.");
            dialog.close();
        });

        form.getChildren().addAll(lblTitle, lblAmount, new Label("Hình thức:"), cbMethod, new Label("Ghi chú:"), txtNote, btnConfirm);
        Scene scene = new Scene(form, 400, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    private void showAdminMemberManagement() {
        resetActiveButtons(btnAdminMembers);
        VBox layout = new VBox(20);
        Label lblAdmins = new Label("Danh sách Quản trị viên");
        lblAdmins.getStyleClass().add("section-title");

        TableView<Users> tableAdmin = createCustomUserTable(true);
        tableAdmin.setPrefHeight(200);
        Label lblMembers = new Label("Danh sách Thành viên");
        lblMembers.getStyleClass().add("section-title");

        TableView<Users> tableMember = createCustomUserTable(false);
        VBox.setVgrow(tableMember, Priority.ALWAYS);
        try {
            List<Users> allUsers = UsersDAO.getInstance().selectAll();
            ObservableList<Users> adminList = FXCollections.observableArrayList();
            ObservableList<Users> memberList = FXCollections.observableArrayList();

            for (Users u : allUsers) {
                if (u.getRole() == UsersRole.ADMIN) {
                    adminList.add(u);
                } else {
                    memberList.add(u);
                }
            }

            tableAdmin.setItems(adminList);
            tableMember.setItems(memberList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không tải được danh sách!");
        }

        layout.getChildren().addAll(lblAdmins, tableAdmin, lblMembers, tableMember);
        contentArea.getChildren().setAll(layout);
    }
    private TableView<Users> createCustomUserTable(boolean isAdminTable) {
        TableView<Users> table = new TableView<>();
        TableColumn<Users, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getUserId()));
        colId.setPrefWidth(50);

        TableColumn<Users, String> colName = new TableColumn<>("Họ tên");
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUserName()));
        colName.setPrefWidth(150);

        TableColumn<Users, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        colEmail.setPrefWidth(200);

        TableColumn<Users, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));
        colStatus.setPrefWidth(100);

        TableColumn<Users, Void> colTotal = new TableColumn<>("Tổng đã nộp (Xem)");
        colTotal.setPrefWidth(150);
        colTotal.setCellFactory(param -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink();
            {
                link.setOnAction(e -> {
                    Users u = getTableView().getItems().get(getIndex());
                    showMemberHistoryPopup(u);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Users u = getTableView().getItems().get(getIndex());
                    java.math.BigDecimal amount = u.getTotalPaid();
                    String formatted = java.text.NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " đ";

                    link.setText(formatted);
                    link.setDisable(amount == null || amount.compareTo(java.math.BigDecimal.ZERO) == 0);
                    setGraphic(link);
                }
            }
        });

        TableColumn<Users, Void> colAction = new TableColumn<>("Hành động");
        colAction.setPrefWidth(160);
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button();
            {
                btn.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());
                    handleUserAction(user, getTableView());
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); }
                else {
                    Users user = getTableView().getItems().get(getIndex());
                    String status = (user.getStatus() == null) ? "" : user.getStatus().trim().toUpperCase();
                    if ("PENDING".equals(status)) {
                        btn.setText("Duyệt");
                        btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                        btn.setVisible(true);
                    } else {
                        if (user.getUserId() == currentUser.getUserId()) {
                            btn.setVisible(false);
                        } else {
                            btn.setText("Xóa");
                            btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                            btn.setVisible(true);
                        }
                    }
                    setGraphic(btn);
                }
            }
        });

        table.getColumns().addAll(colId, colName, colEmail, colStatus, colTotal, colAction);
        return table;
    }
    private void showMemberHistoryPopup(Users user) {
        Stage dialog = new Stage();
        dialog.setTitle("Lịch sử đóng quỹ: " + user.getUserName());
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setPrefWidth(450);

        Label lblTitle = new Label("Các khoản đã nộp bởi " + user.getUserName());
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        TableView<MemberFeeRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Cột Tên Quỹ
        TableColumn<MemberFeeRow, String> colTitle = new TableColumn<>("Khoản thu");
        colTitle.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));

        // Cột Số tiền
        TableColumn<MemberFeeRow, String> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(cell -> {
            String s = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi","VN")).format(cell.getValue().getAmount());
            return new SimpleStringProperty(s);
        });

        // Cột Ngày nộp
        TableColumn<MemberFeeRow, String> colDate = new TableColumn<>("Ngày nộp");
        colDate.setCellValueFactory(cell -> {
            Date d = cell.getValue().getPaidDate();
            return new SimpleStringProperty(d != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(d) : "");
        });

        table.getColumns().addAll(colTitle, colAmount, colDate);

        try {

            List<models.FeeStatus> paidList = new services.FeeStatusService_Impl().selectPaidByUserId(user.getUserId());
            ObservableList<MemberFeeRow> rows = FXCollections.observableArrayList();
            services.FeeService feeService = new services.FeeService_Impl();
            for (models.FeeStatus fs : paidList) {
                models.Fee f = feeService.getFeeById(fs.getFId());
                if (f != null) {
                    rows.add(new MemberFeeRow(f, fs));
                }
            }

            if (rows.isEmpty()) table.setPlaceholder(new Label("Thành viên này chưa đóng khoản nào."));
            table.setItems(rows);

        } catch (Exception e) { e.printStackTrace(); }

        Button btnClose = new Button("Đóng");
        btnClose.setOnAction(e -> dialog.close());
        btnClose.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        btnClose.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(lblTitle, table, btnClose);

        Scene scene = new Scene(layout);
        dialog.setScene(scene);
        dialog.show();
    }
    private void handleUserAction(Users user, TableView<Users> table) {
        String status = (user.getStatus() == null) ? "" : user.getStatus().trim().toUpperCase();
        if ("PENDING".equals(status)) {

            if (new services.UsersService_Impl().updateUserStatus(user.getUserId(), "ACTIVE")) {
                user.setStatus("ACTIVE");
                showAlert("Thành công", "Đã duyệt thành viên: " + user.getUserName());
                table.refresh();
            } else {
                showAlert("Lỗi", "Lỗi SQL: Không thể cập nhật trạng thái.");
                user.setStatus("PENDING");
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Xác nhận xóa " + user.getUserName() + "?\nToàn bộ lịch sử đóng tiền sẽ mất.",
                    ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                if (new UsersService_Impl().deleteUser(user.getUserId())) {
                    showAlert("Đã xóa", "Đã xóa thành công!");

                    // Xóa dòng đó khỏi bảng ngay lập tức
                    table.getItems().remove(user);
                } else {
                    showAlert("Lỗi", "Không thể xóa (Lỗi khóa ngoại hoặc SQL).");
                }
            }
        }
    }
    private void handleUserAction(Users user) {
        String status = (user.getStatus() != null) ? user.getStatus().trim().toUpperCase() : "";
        if ("PENDING".equals(status)) {
            if (new UsersService_Impl().updateUserStatus(user.getUserId(), "ACTIVE")) {
                user.setStatus("ACTIVE");
                showAlert("Thành công", "Đã duyệt thành viên ID: " + user.getUserId());
                ZoneRulesProvider table = null;
                table.refresh();
            } else {
                showAlert("Lỗi", "Không thể cập nhật. Hãy xem Console để biết tại sao.");
            }
        } else {
            if (user.getUserId() == UsersController.currentUser.getUserId()) {
                showAlert("Cảnh báo", "Bạn không thể tự xóa chính mình!");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Bạn có chắc muốn xóa " + user.getUserName() + " (" + user.getRole() + ")?\nLịch sử đóng tiền cũng sẽ bị xóa.",
                    ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                if (new UsersService_Impl().deleteUser(user.getUserId())) {
                    showAlert("Đã xóa", "Đã xóa thành công!");
                    showAdminMemberManagement();
                } else {
                    showAlert("Lỗi", "Xóa thất bại.");
                }
            }
        }
    }
    private void showCreateFeeDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Tạo khoản thu mới");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white;");

        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Tên khoản thu (VD: Quỹ lớp...)");

        // 1. Ô nhập TỔNG
        TextField txtTotalTarget = new TextField();
        txtTotalTarget.setPromptText("Nhập TỔNG số tiền cần thu (VD: 1000000)");

        // 2. Ô nhập CHI TIẾT
        TextField txtPerPerson = new TextField();
        txtPerPerson.setPromptText("Số tiền mỗi người (Tự động tính)");

        DatePicker dpDeadline = new DatePicker(java.time.LocalDate.now().plusDays(7));
        Label lblInfo = new Label();

        List<Users> allUsers = DAO.UsersDAO.getInstance().selectAll();
        int totalMembers = allUsers.size();
        lblInfo.setText("Đang có " + totalMembers + " thành viên. Hệ thống sẽ tự chia đều.");

        txtTotalTarget.textProperty().addListener((obs, oldVal, newVal) -> {

            if (!newVal.matches("\\d*")) {
                txtTotalTarget.setText(newVal.replaceAll("[^\\d]", ""));
                return;
            }
            try {
                if (!newVal.isEmpty() && totalMembers > 0) {
                    java.math.BigDecimal total = new java.math.BigDecimal(newVal);
                    java.math.BigDecimal mems = new java.math.BigDecimal(totalMembers);
                    // Chia đều, làm tròn lên
                    java.math.BigDecimal perPerson = total.divide(mems, 0, java.math.RoundingMode.CEILING);
                    txtPerPerson.setText(perPerson.toString());
                } else {
                    txtPerPerson.setText("");
                }
            } catch (Exception e) {}
        });

        Button btnCreate = new Button("Xác nhận tạo");
        btnCreate.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCreate.setMaxWidth(Double.MAX_VALUE);

        btnCreate.setOnAction(e -> {
            try {
                String tName = txtTitle.getText();
                String tTotalStr = txtTotalTarget.getText();
                String error = utils.Validator.validateMoneyForm(tName, tTotalStr);
                if (!error.equals("OK")) {
                    utils.Validator.showError(error);
                    return;
                }
                java.math.BigDecimal target = new java.math.BigDecimal(txtTotalTarget.getText().isEmpty() ? "0" : txtTotalTarget.getText());
                java.math.BigDecimal amount = new java.math.BigDecimal(txtPerPerson.getText().isEmpty() ? "0" : txtPerPerson.getText());
                java.sql.Date dead = java.sql.Date.valueOf(dpDeadline.getValue());

                models.Fee newFee = new models.Fee();
                newFee.setTitle(tName);
                newFee.setTargetAmount(target);
                newFee.setAmount(amount);
                newFee.setDeadline(dead);
                newFee.setDescription("Mục tiêu: " + java.text.NumberFormat.getInstance().format(target));

                if (feeController.createFee(newFee, true)) { // true = tự gán nợ
                    showAlert("Thành công", "Đã tạo quỹ và chia đều cho " + totalMembers + " người!");
                    dialog.close();
                    showAdminFundManagement();
                } else {
                    showAlert("Lỗi", "Tạo thất bại.");
                }
            } catch (Exception ex) {
                showAlert("Lỗi", "Vui lòng kiểm tra lại số liệu.");
            }
        });

        form.getChildren().addAll(
                new Label("Tên quỹ:"), txtTitle,
                new Label("Tổng số tiền cần thu:"), txtTotalTarget,
                lblInfo,
                new Label("Mỗi thành viên phải đóng:"), txtPerPerson,
                new Label("Hạn chót:"), dpDeadline,
                new Separator(),
                btnCreate
        );

        dialog.setScene(new Scene(form, 350, 450));
        dialog.show();
    }
    private void showHistoryView() {
        resetActiveButtons(btnHistory);

        VBox layout = new VBox(20);
        Label title = new Label("Lịch sử đóng góp");
        title.getStyleClass().add("section-title");

        TableView<MemberFeeRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<MemberFeeRow, String> colTitle = new TableColumn<>("Khoản thu");
        colTitle.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTitle()));

        TableColumn<MemberFeeRow, String> colAmount = new TableColumn<>("Số tiền đã nộp");
        colAmount.setCellValueFactory(cell -> {
            String s = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi","VN")).format(cell.getValue().getAmount());
            return new javafx.beans.property.SimpleStringProperty(s);
        });

        TableColumn<MemberFeeRow, String> colDate = new TableColumn<>("Ngày nộp");
        colDate.setCellValueFactory(cell -> {
            java.sql.Date d = cell.getValue().getPaidDate();
            return new javafx.beans.property.SimpleStringProperty(d != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(d) : "N/A");
        });

        // Thêm cột trạng thái cho đẹp
        TableColumn<MemberFeeRow, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty("✅ Đã hoàn thành"));
        colStatus.setStyle("-fx-text-fill: green; -fx-alignment: CENTER;");

        table.getColumns().addAll(colTitle, colAmount, colDate, colStatus);
        try {

            List<models.FeeStatus> paidList = new services.FeeStatusService_Impl().selectPaidByUserId(currentUser.getUserId());
            ObservableList<MemberFeeRow> rows = FXCollections.observableArrayList();
            services.FeeService feeService = new services.FeeService_Impl();
            for (models.FeeStatus fs : paidList) {
                models.Fee f = feeService.getFeeById(fs.getFId());
                if (f != null) rows.add(new MemberFeeRow(f, fs));
            }
            if (rows.isEmpty()) table.setPlaceholder(new Label("Bạn chưa có lịch sử đóng góp nào."));
            table.setItems(rows);

        } catch (Exception e) { e.printStackTrace(); }

        layout.getChildren().addAll(title, table);
        contentArea.getChildren().setAll(layout);
    }

    private void showNotificationView() {
        resetActiveButtons(btnNotifications);

        VBox layout = new VBox(20);
        Label title = new Label("Thông báo của tôi");
        title.getStyleClass().add("section-title");

        ListView<models.Notification> listView = new ListView<>();
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(models.Notification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label lblSub = new Label(item.getTitle());
                    lblSub.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    Label lblMsg = new Label(item.getMessage());
                    lblMsg.setWrapText(true);
                    Label lblTime = new Label(item.getCreatedAt().toString());
                    lblTime.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
                    VBox box = new VBox(5, lblSub, lblMsg, lblTime);
                    box.setPadding(new Insets(10));
                    box.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0;"); // Gạch chân
                    setGraphic(box);
                }
            }
        });
        try {
            List<models.Notification> list = DAO.NotificationDAO.getInstance().selectByUserId(currentUser.getUserId());
            listView.getItems().addAll(list);
            if (list.isEmpty()) listView.setPlaceholder(new Label("Không có thông báo mới."));
        } catch (Exception e) { e.printStackTrace(); }

        layout.getChildren().addAll(title, listView);
        contentArea.getChildren().setAll(layout);
    }
    private void showAdminSendNotification() {
        resetActiveButtons(btnAdminNotifs);

        VBox layout = new VBox(20);
        Label title = new Label("Trung tâm thông báo");
        title.getStyleClass().add("section-title");

        VBox inputBox = new VBox(10);
        inputBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        TextField txtSubject = new TextField();
        txtSubject.setPromptText("Tiêu đề thông báo...");
        txtSubject.setStyle("-fx-font-weight: bold;");

        TextArea txtContent = new TextArea();
        txtContent.setPromptText("Nhập nội dung tin nhắn gửi đi...");
        txtContent.setPrefHeight(100);

        HBox btnBox = new HBox(15);
        btnBox.setAlignment(Pos.CENTER_LEFT);

        Button btnSendAll = new Button("📢 Gửi tất cả");
        btnSendAll.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");

        Button btnSendSelected = new Button("📨 Gửi người đã chọn");
        btnSendSelected.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-cursor: hand;");

        Button btnAutoRemind = new Button("⚡ Tự động");
        btnAutoRemind.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnBox.getChildren().addAll(btnSendAll, btnSendSelected, spacer, btnAutoRemind);
        inputBox.getChildren().addAll(new Label("Soạn tin nhắn mới:"), txtSubject, txtContent, btnBox);

        Label lblList = new Label("Danh sách thành viên (Giữ Ctrl hoặc Shift để chọn nhiều người)");

        TableView<Users> table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableColumn<Users, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getUserId()));
        colId.setPrefWidth(50);

        // Cột Tên
        TableColumn<Users, String> colName = new TableColumn<>("Họ tên");
        colName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUserName()));
        colName.setPrefWidth(200);

        // Cột Email
        TableColumn<Users, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmail()));
        colEmail.setPrefWidth(200);

        TableColumn<Users, String> colDebtStatus = new TableColumn<>("Tình trạng nợ");
        colDebtStatus.setCellValueFactory(cell -> {
            List<models.FeeStatus> unpaid = new services.FeeStatusService_Impl().getUnpaidList(cell.getValue().getUserId());
            if (unpaid.isEmpty()) return new javafx.beans.property.SimpleStringProperty("✅ Đã nộp đủ");
            return new javafx.beans.property.SimpleStringProperty("❌ Nợ " + unpaid.size() + " khoản");
        });
        colDebtStatus.setPrefWidth(150);

        table.getColumns().addAll(colId, colName, colEmail, colDebtStatus);

        try {
            List<Users> users = DAO.UsersDAO.getInstance().selectAll();
            table.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) { e.printStackTrace(); }

        btnSendAll.setOnAction(e -> {
            String sub = txtSubject.getText();
            String msg = txtContent.getText();

            if (sub.isEmpty() || msg.isEmpty()) {
                showAlert("Thiếu thông tin", "Vui lòng nhập tiêu đề và nội dung.");
                return;
            }

            models.Notification n = new models.Notification(0, sub, msg);
            if (DAO.NotificationDAO.getInstance().insert(n)) {
                showAlert("Thành công", "Đã gửi thông báo đến toàn bộ hệ thống!");
                txtContent.clear(); txtSubject.clear();
            } else {
                showAlert("Lỗi", "Gửi thất bại. Kiểm tra kết nối.");
            }
        });

        btnSendSelected.setOnAction(e -> {
            String sub = txtSubject.getText();
            String msg = txtContent.getText();
            List<Users> selectedUsers = table.getSelectionModel().getSelectedItems();

            if (selectedUsers.isEmpty()) {
                showAlert("Chưa chọn người", "Vui lòng chọn ít nhất 1 người trong bảng dưới.");
                return;
            }
            if (sub.isEmpty() || msg.isEmpty()) {
                showAlert("Thiếu thông tin", "Vui lòng nhập tiêu đề và nội dung.");
                return;
            }

            int count = 0;
            for (Users u : selectedUsers) {
                models.Notification n = new models.Notification(u.getUserId(), sub, msg);
                if (DAO.NotificationDAO.getInstance().insert(n)) {
                    count++;
                }
            }
            showAlert("Hoàn tất", "Đã gửi tin nhắn riêng cho " + count + " thành viên.");
        });

        btnAutoRemind.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Hệ thống sẽ quét và gửi tin nhắn nhắc nợ cho những ai chưa đóng tiền.\nTiếp tục?",
                    ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                int sentCount = 0;
                List<Users> allUsers = table.getItems();
                services.FeeStatusService fsService = new services.FeeStatusService_Impl();
                services.FeeService feeService = new services.FeeService_Impl(); // Cần cái này để lấy tên khoản thu

                for (Users u : allUsers) {
                    List<models.FeeStatus> unpaidList = fsService.getUnpaidList(u.getUserId());

                    if (!unpaidList.isEmpty()) {
                        StringBuilder msgBuilder = new StringBuilder();
                        msgBuilder.append("Chào ").append(u.getUserName()).append(",\n");
                        msgBuilder.append("Bạn còn các khoản chưa đóng:\n");

                        for (models.FeeStatus fs : unpaidList) {
                            models.Fee f = feeService.getFeeById(fs.getFId());
                            if (f != null) {
                                msgBuilder.append("- ").append(f.getTitle()).append("\n");
                            }
                        }
                        msgBuilder.append("Vui lòng đóng sớm nhé!");

                        // Gửi
                        models.Notification n = new models.Notification(u.getUserId(), "⚠️ Nhắc nợ tự động", msgBuilder.toString());
                        if (DAO.NotificationDAO.getInstance().insert(n)) {
                            sentCount++;
                        }
                    }
                }
                showAlert("Hoàn tất", "Đã gửi nhắc nhở cho " + sentCount + " người.");
            }
        });

        layout.getChildren().addAll(title, inputBox, lblList, table);
        contentArea.getChildren().setAll(layout);
    }

    private Button createMenuButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("menu-button");
        if (active) btn.getStyleClass().add("menu-button-active");
        return btn;
    }

    private void resetActiveButtons(Button activeOne) {
        Button[] allBtns = {btnDashboard, btnProfile, btnMyFees, btnHistory, btnNotifications, btnAdminMembers, btnAdminFunds, btnAdminNotifs};
        for (Button b : allBtns) {
            if (b != null) b.getStyleClass().remove("menu-button-active");
        }
        if (activeOne != null) activeOne.getStyleClass().add("menu-button-active");
    }

    private VBox createCard(String title, String value) {
        VBox card = new VBox(5, new Label(title), new Label(value));
        card.getStyleClass().add("card");
        card.setPrefWidth(200);
        ((Label)card.getChildren().get(0)).getStyleClass().add("card-title");
        ((Label)card.getChildren().get(1)).getStyleClass().add("card-value");
        return card;
    }

    private BarChart<String, Number> createChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Biểu đồ Thu - Chi 6 tháng gần nhất");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Thu nhập");
        series.getData().add(new XYChart.Data<>("T10", 100));
        series.getData().add(new XYChart.Data<>("T11", 150));
        series.getData().add(new XYChart.Data<>("T12", 120));
        chart.getData().add(series);
        return chart;
    }

    private TableColumn createColumn(String header, String prop, int width) {
        TableColumn col = new TableColumn(header);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setPrefWidth(width);
        return col;
    }

    private void showAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(content);
        a.showAndWait();
    }

    public static class FeeModel {
        String title, amount, deadline, status;
        public FeeModel(String t, String a, String d, String s) { title=t; amount=a; deadline=d; status=s; }
        public String getTitle() { return title; }
        public String getAmount() { return amount; }
        public String getDeadline() { return deadline; }
        public String getStatus() { return status; }
    }

}

class MemberFeeRow {
    int feeId;
    String title;
    java.math.BigDecimal amount;
    java.sql.Date deadline;
    java.sql.Date paidDate;
    models.FeeStatus statusObj;

    public MemberFeeRow(models.Fee fee, models.FeeStatus status) {
        this.feeId = fee.getFId();
        this.title = fee.getTitle();
        this.amount = fee.getAmount();
        this.deadline = fee.getDeadline();
        this.paidDate = status.getPaidDate();
        this.statusObj = status;
    }


    public String getTitle() { return title; }
    public java.math.BigDecimal getAmount() { return amount; }
    public java.sql.Date getDeadline() { return deadline; }
    public java.sql.Date getPaidDate() { return paidDate; }
    public models.FeeStatus getStatusObj() { return statusObj; }
}