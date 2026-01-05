package controllers;

import models.Fee;
import models.FeeStatus;
import models.Users;
import DAO.NotificationDAO;
import models.Notification;
import services.*;

import java.util.List;

public class FeeController {

    private final FeeService feeService = new FeeService_Impl();
    private final UsersService userService = new UsersService_Impl();
    private final FeeStatusService feeStatusService = new FeeStatusService_Impl();

    /**
     * 1. Lấy danh sách khoản thu
     */
    public List<Fee> getAllFees() {
        return feeService.getAllFees();
    }
    public boolean createFee(Fee fee, boolean autoAssign) {
        if (!isAdmin()) return false;

        boolean isCreated = feeService.createFee(fee);

        if (isCreated && autoAssign) {
            // 1.nợ
            List<Fee> allFees = feeService.getAllFees();
            if (!allFees.isEmpty()) {
                Fee newFee = allFees.get(allFees.size() - 1);
                assignFeeToAllUsers(newFee.getFId());

                // 2. (NOTIFICATION) GỬI TOÀN BỘ
                Notification notif = new Notification(
                        0, // gửi tất cả
                        "Thông báo đóng tiền quỹ",
                        "Đã có khoản thu mới: " + fee.getTitle() + ". Vui lòng đóng trước " + fee.getDeadline()
                );
                NotificationDAO.getInstance().insert(notif);
            }
        }
        return isCreated;
    }
    public boolean updateFee(Fee fee) {
        if (!isAdmin()) return false;
        return feeService.updateFee(fee);
    }
     //4. Logic áp nợ (Tạo FeeStatus - UNPAID) cho toàn bộ User
    public int assignFeeToAllUsers(int feeId) {
        if (!isAdmin()) return 0;

        List<Users> allUsers = userService.getAllUsers();
        int count = 0;

        for (Users user : allUsers) {
            // nợ cho từng user
            FeeStatus fs = new FeeStatus();
            fs.setFId(feeId);
            fs.setUserId(user.getUserId());
            fs.setStatus(models.status.UNPAID);
            if (feeStatusService.createFeeStatus(fs)) {
                count++;
            }
        }
        return count;
    }
    private boolean isAdmin() {
        // 1. Kiểm tra xem có ai đăng nhập chưa
        if (UsersController.currentUser == null) {
            System.out.println(" Chưa có ai đăng nhập");
            return false;
        }
        System.out.println("Người đang dùng: " + UsersController.currentUser.getEmail());
        System.out.println("Vai trò (Role): " + UsersController.currentUser.getRole());
        boolean isAd = (UsersController.currentUser.getRole() == models.UsersRole.ADMIN);

        if (!isAd) {
            System.out.println(" KHÔNG PHẢI ADMIN! Từ chối truy cập.");
        } else {
            System.out.println("ADMIN. Cho phép.");
        }

        return isAd;
    }

}