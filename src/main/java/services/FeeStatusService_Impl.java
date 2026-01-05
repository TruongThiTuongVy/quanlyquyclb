package services;

import DAO.FeeStatusDAO;
import models.FeeStatus;
import java.util.List;

public class FeeStatusService_Impl implements FeeStatusService {

    private FeeStatusDAO feeStatusDAO = FeeStatusDAO.getInstance();

    @Override
    public boolean createFeeStatus(FeeStatus feeStatus) {
        if (feeStatus == null) return false;
        return feeStatusDAO.insert(feeStatus) > 0;
    }

    @Override
    public boolean updateFeeStatus(FeeStatus feeStatus) {
        if (feeStatus == null) return false;
        return feeStatusDAO.update(feeStatus) > 0;
    }

    @Override
    public List<FeeStatus> getFeeStatusByUser(int userId) {
        return feeStatusDAO.selectByUserId(userId);
    }
    @Override
    public List<FeeStatus> getUnpaidList(int userId) {
        return feeStatusDAO.selectUnpaidByUserId(userId);
    }

    @Override
    public FeeStatus getById(int ufId) {
        return feeStatusDAO.selectById(ufId);
    }
    @Override
    public List<FeeStatus> selectPaidByUserId(int userId) {
        return feeStatusDAO.selectPaidByUserId(userId);
    }

}