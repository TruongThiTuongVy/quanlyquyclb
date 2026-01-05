package services;

import DAO.FeesDAO;
import models.Fee;
import java.util.List;

public class FeeService_Impl implements FeeService {
    private FeesDAO feeDAO = FeesDAO.getInstance();

    @Override
    public boolean createFee(Fee fee) {
        if (fee == null) return false;
        if (fee.getAmount().signum() <= 0) return false;
        return feeDAO.insert(fee) > 0;
    }

    @Override
    public boolean updateFee(Fee fee) {
        if (fee == null || fee.getFId() <= 0) return false;
        return feeDAO.update(fee) > 0;
    }

    @Override
    public boolean deleteFee(int feeId) {
        return feeDAO.delete(feeId) > 0;
    }

    @Override
    public Fee getFeeById(int feeId) {
        return feeDAO.selectById(feeId);
    }

    @Override
    public List<Fee> getAllFees() {
        return feeDAO.selectAll();
    }
}