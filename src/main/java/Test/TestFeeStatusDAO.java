package Test;

import DAO.FeeStatusDAO;
import models.FeeStatus;
import models.status;

import java.sql.Date; // Dùng sql.Date thay vì Timestamp
import java.util.List;

public class TestFeeStatusDAO {
    public static void main(String[] args) {
        FeeStatusDAO dao = FeeStatusDAO.getInstance();

        System.out.println("--- BẮT ĐẦU TEST ---");

        // =======================
        // 1. INSERT (Thêm mới)
        // =======================
        FeeStatus fs = new FeeStatus();

        // Lưu ý: ufId (ID tự tăng) thường không cần set khi insert,
        // nhưng nếu code DAO của bạn yêu cầu thì cứ set.
        // fs.setUfId(1);

        fs.setFId(1);         // (!) Đảm bảo ID khoản thu = 1 có trong bảng fees
        fs.setUserId(1);      // (!) Đảm bảo ID user = 1 có trong bảng users
        fs.setStatus(status.UNPAID); // Dùng Enum
        fs.setPaidDate(null); // Chưa đóng thì ngày là null

        // Hiện tại Model chưa có setFsNote, ta bỏ qua hoặc bạn cần thêm cột note vào Model sau
        // fs.setFsNote("Chưa đóng");

        System.out.print("Test Insert: ");
        int resultInsert = dao.insert(fs);
        System.out.println(resultInsert > 0 ? "Thành công" : "Thất bại (Kiểm tra xem ID User/Fee có tồn tại không)");

        // =======================
        // 2. SELECT BY ID (Lấy theo ID)
        // =======================
        // Giả sử ta muốn lấy bản ghi có uf_id = 1
        int testId = 1;

        FeeStatus result = dao.selectById(testId); // Truyền số int, không truyền object
        if (result != null) {
            System.out.println("Test GetById: Tìm thấy -> Trạng thái: " + result.getStatus());
        } else {
            System.out.println("Test GetById: Không tìm thấy ID " + testId);
        }

        // =======================
        // 3. UPDATE (Đóng phí)
        // =======================
        if (result != null) {
            result.setStatus(status.PAID);
            // Lấy ngày hiện tại
            result.setPaidDate(new Date(System.currentTimeMillis()));

            System.out.print("Test Update: ");
            int resultUpdate = dao.update(result);
            System.out.println(resultUpdate > 0 ? "Thành công" : "Thất bại");
        }

        // =======================
        // 4. SELECT UNPAID (Lấy danh sách nợ của User)
        // =======================
        // DAO hiện tại của chúng ta có hàm lấy danh sách nợ của 1 User
        int userIdTest = 1;
        List<FeeStatus> unpaidList = dao.selectUnpaidByUserId(userIdTest);

        System.out.println("Test Unpaid List for User " + userIdTest + ":");
        if (unpaidList.isEmpty()) {
            System.out.println("- Không có khoản nợ nào.");
        } else {
            for (FeeStatus f : unpaidList) {
                System.out.println("- Khoản thu ID: " + f.getFId() + " | Trạng thái: " + f.getStatus());
            }
        }
    }
}