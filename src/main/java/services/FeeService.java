package services;

import models.Fee;
import java.util.List;

public interface FeeService {
    boolean createFee(Fee fee);
    boolean updateFee(Fee fee);
    boolean deleteFee(int feeId);
    Fee getFeeById(int feeId);
    List<Fee> getAllFees();
}