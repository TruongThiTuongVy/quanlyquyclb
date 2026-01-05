package services;

import models.FundCategory;

import java.util.List;

public interface FundCategoryService {
    boolean createCategory(FundCategory c);

    boolean updateCategory(FundCategory c);

    boolean deleteCategory(int id);

    List<FundCategory> getAll();

}
