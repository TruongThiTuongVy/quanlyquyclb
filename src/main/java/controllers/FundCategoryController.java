package controllers;

import models.FundCategory;
import services.FundCategoryService;
import services.FundCategoryService_Impl;

import java.util.List;
import java.util.Scanner;

public class FundCategoryController {

    // Khởi tạo Service
    private final FundCategoryService service = new FundCategoryService_Impl();
    private final Scanner scanner = new Scanner(System.in);
    public List<FundCategory> showAllCategories() {
        List<FundCategory> list = service.getAll();

        if (list.isEmpty()) {
            return list;
        }
        for (FundCategory c : list) {
            System.out.printf("%-5d | %-30s | %-15s\n",
                    c.getFcId(),
                    c.getFcName(),
                    c.getType());
        }
        return list;
    }

    public void createCategory() {
        String name = scanner.nextLine();
        String type = scanner.nextLine().toUpperCase();
        FundCategory fc = new FundCategory();
        fc.setFcName(name);
        fc.setType(type);

        if (service.createCategory(fc)) {
            System.out.println(">> Thêm danh mục thành công!");
        } else {
            System.out.println(">> Thêm thất bại");
        }
    }
    public void updateCategory() {
        showAllCategories(); // Hiện danh sách để biết ID mà chọn
        int id = 0;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID không hợp lệ.");
            return;
        }
        String newName = scanner.nextLine();
        String newType = scanner.nextLine();
        FundCategory fc = new FundCategory();
        fc.setFcId(id);
        fc.setFcName(newName);
        fc.setType(newType);

        if (service.updateCategory(fc)) {
            System.out.println("Cập nhật thành công!");
        } else {
            System.out.println("Cập nhật thất bại ");
        }
    }

    public void deleteCategory() {
        showAllCategories();
        try {
            int id = Integer.parseInt(scanner.nextLine());
            if (service.deleteCategory(id)) {
                System.out.println(">> Đã xóa thành công.");
            } else {
                System.out.println(">> Xóa thất bại");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID không hợp lệ.");
        }
    }
    private boolean isAdmin() {
        return UsersController.currentUser != null &&
                UsersController.currentUser.getRole().toString().equalsIgnoreCase("ADMIN");
    }
}