package controllers;

import models.Transaction;
import models.transactionType; // Enum của em
import services.TransactionService;
import services.TransactionService_Impl;

import java.math.BigDecimal;
import java.sql.Date; // Hoặc java.time.LocalDate tùy model của em
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class TransactionController {

    private final TransactionService transactionService = new TransactionService_Impl();
    private final Scanner scanner = new Scanner(System.in);
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    /**
     * 1. Tạo giao dịch mới
     */
    public void createTransaction() {
        if (UsersController.currentUser == null) {
            System.out.println("đăng nhập để thực hiện giao dịch.");
            return;
        }
        BigDecimal amount;
        while (true) {
            String input = scanner.nextLine();
            try {
                amount = new BigDecimal(input);
                if (amount.signum() <= 0) {
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println(" nhập số hợp lệ.");
            }
        }
        transactionType type = null;
        while (type == null) {
            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                type = transactionType.INCOME;
            } else if (choice.equals("2")) {
                type = transactionType.EXPENSE;
            } else {
                System.out.println(" không hợp lệ.");
            }
        }
        String note = scanner.nextLine();
        int categoryId = 0;
        try {
            categoryId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            categoryId = 1;
        }
        String imagePath = scanner.nextLine();
        if (imagePath.isEmpty()) imagePath = null;
        Transaction t = new Transaction();
        t.setTAmount(amount);
        t.setTType(type);
        t.setTNote(note);
        t.setImage(imagePath);
        t.setCreateAt(Date.valueOf(LocalDate.now()));
        t.setUserId(UsersController.currentUser.getUserId());
        t.setTId(categoryId);
        if (transactionService.create(t)) {
            System.out.println("Giao dịch thành công!");
        } else {
            System.out.println("Giao dịch thất bại");
        }
    }
    public void showAllTransactions() {
        List<Transaction> list = transactionService.getAll();
        if (list.isEmpty()) {
            return;
        }

        for (Transaction t : list) {
            String formattedMoney = currencyFormatter.format(t.getTAmount());
            System.out.printf("%-5d | %-15s | %-10s | %-20s | %-15d\n",
                    t.getTId(),
                    formattedMoney,
                    t.getTType(), // Enum sẽ tự toString()
                    t.getTNote(),
                    t.getUserId()); // Nếu muốn hiện Tên, em cần join bảng hoặc gọi UserService.getById
        }
    }

    public void showFundReport() {
        BigDecimal totalIncome = transactionService.getTotalIncome();
        BigDecimal totalExpense = transactionService.getTotalExpense();
        BigDecimal balance = transactionService.getCurrentBalance();
        if (balance.signum() < 0) {
            System.out.println("Quỹ âm");
        }
    }
    public void deleteTransaction() {
        // Kiểm tra quyền Admin
        if (UsersController.currentUser == null ||
                !UsersController.currentUser.getRole().toString().equalsIgnoreCase("ADMIN")) {
            return;
        }


        try {
            int id = Integer.parseInt(scanner.nextLine());
            if (transactionService.delete(id)) {
                System.out.println(">> Đã xóa thành công.");
            } else {
                System.out.println(">> Xóa thất bại");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID không hợp lệ.");
        }
    }
}