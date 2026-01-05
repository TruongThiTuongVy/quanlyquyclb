package services;

import models.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    boolean create(Transaction t);

    boolean delete(int id);

    List<Transaction> getAll();

    BigDecimal getTotalIncome();

    BigDecimal getTotalExpense();

    BigDecimal getCurrentBalance();

}
