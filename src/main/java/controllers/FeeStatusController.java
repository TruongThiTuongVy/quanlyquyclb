package controllers;

import models.FeeStatus;
import models.Transaction;
import models.transactionType;
import services.FeeStatusService;
import services.FeeStatusService_Impl;
import services.TransactionService;
import services.TransactionService_Impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class FeeStatusController {

    private final FeeStatusService feeStatusService = new FeeStatusService_Impl();
    private final TransactionService transactionService = new TransactionService_Impl();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * 1.danh sách thành viên CHƯA đóng tiền
     */
    public void showUnpaidMembers() {
        int feeId = 0;
        try {
            feeId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("(!) ID không hợp lệ.");
            return;
        }

        List<FeeStatus> unpaidList = feeStatusService.getUnpaidList(feeId);

        if (unpaidList.isEmpty()) {
            System.out.println(" Tất cả thành viên đã đóng");
            return;
        }
        for (FeeStatus fs : unpaidList) {
            System.out.printf("%-5d | %-10d | %-10d | %-15s\n",
                    fs.getUfId(),
                    fs.getUserId(),
                    fs.getFId(),
                    "CHƯA ĐÓNG");
        }
    }

    /**
     * 2. Xác nhận thành viên đóng tiền
     */
    public void markFeeAsPaid() {
        // admin duyệt quyền đóng tiền
        if (UsersController.currentUser == null ||
                !UsersController.currentUser.getRole().toString().equalsIgnoreCase("ADMIN")) {
            return;
        }
        int fsId = 0;
        try {
            fsId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID không hợp lệ");
            return;
        }

        // Kiểm tra xem ID này có tồn tại và chưa đóng không
        FeeStatus currentFs = feeStatusService.getById(fsId);
        if (currentFs == null) {
            System.out.println("(!) Không tìm thấy ID");
            return;
        }
        if (currentFs.getPaidDate() != null) {
            System.out.println("đã đóng trước");
            return;
        }
        String note = scanner.nextLine();
        currentFs.setStatus(models.status.PAID);
        if (feeStatusService.updateFeeStatus(currentFs)) {
            System.out.println(">> Đã cập nhật");
            createTransactionForFee(currentFs, note);
        } else {
            System.out.println("Không thể cập nhật.");
        }

    }
    private void createTransactionForFee(FeeStatus fs, String note) {
        Transaction t = new Transaction();
        t.setTAmount(new BigDecimal("50000"));
        t.setTType(transactionType.INCOME);
        t.setTNote("Thu phí từ thành viên ID " + fs.getUserId() + ": " + note);
        t.setUserId(UsersController.currentUser.getUserId());
        t.setCreateAt(Date.valueOf(LocalDate.now()));
        t.setFcId(1);

        if (transactionService.create(t)) {
            System.out.println(">> Đã nhận tiền");
        } else {
            System.out.println("chưa tạo được Transaction.");
        }
    }
}