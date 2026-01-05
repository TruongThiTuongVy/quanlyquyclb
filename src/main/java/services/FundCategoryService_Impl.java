package services;
import DAO.FundCategoryDAO;
import models.FundCategory;

import java.util.List;


public class FundCategoryService_Impl implements FundCategoryService {

    private final FundCategoryDAO dao = FundCategoryDAO.getInstance();

    @Override
    public boolean createCategory(FundCategory c) {
        if (c == null || c.getFcName() == null) return false;
        return dao.insert(c) > 0;
    }

    @Override
    public boolean updateCategory(FundCategory c) {
        return dao.update(c) > 0;
    }

    @Override
    public boolean deleteCategory(int id) {
        FundCategory c = new FundCategory();
        c.setFcId(id);
        return dao.delete(c);
    }

    @Override
    public List<FundCategory> getAll() {
        return dao.selectAll();
    }
}