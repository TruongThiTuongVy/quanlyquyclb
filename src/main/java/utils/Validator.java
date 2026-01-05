package utils;

import java.util.regex.Pattern;
import javafx.scene.control.Alert;

public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");

    public static String validateRegister(String name, String email, String phone, String pass) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty())
            return "Vui lòng nhập đầy đủ thông tin!";
        if (!EMAIL_PATTERN.matcher(email).matches())
            return "Email không hợp lệ!";
        if (!PHONE_PATTERN.matcher(phone).matches())
            return "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0!";
        if (pass.length() < 6)
            return "Mật khẩu phải từ 6 ký tự trở lên!";
        return "OK";
    }

    public static String validateMoneyForm(String title, String amountStr) {
        if (title.isEmpty() || amountStr.isEmpty()) return "Vui lòng nhập đủ thông tin!";
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) return "Số tiền phải lớn hơn 0!";
        } catch (NumberFormatException e) {
            return "Số tiền không đúng định dạng!";
        }
        return "OK";
    }
    public static void showError(String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Lỗi nhập liệu");
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}