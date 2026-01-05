package services;

import models.FeeStatus;
import java.util.List;

public interface FeeStatusService {
    boolean createFeeStatus(FeeStatus feeStatus);
    boolean updateFeeStatus(FeeStatus feeStatus);
    List<FeeStatus> getFeeStatusByUser(int userId);
    List<FeeStatus> getUnpaidList(int userId);

    FeeStatus getById(int ufId);

    List<FeeStatus> selectPaidByUserId(int userId);
}