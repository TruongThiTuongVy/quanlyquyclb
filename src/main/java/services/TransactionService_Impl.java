package services;

import DAO.TransactionDAO;
import models.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class TransactionService_Impl implements TransactionService {

    private TransactionDAO dao = TransactionDAO.getInstance();

    @Override
    public boolean create(Transaction transaction) {
        return dao.insert(transaction);
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public List<Transaction> getAll() {
        return List.of();
    }

    @Override
    public BigDecimal getTotalIncome() {
        return null;
    }

    @Override
    public BigDecimal getTotalExpense() {
        return null;
    }

    @Override
    public BigDecimal getCurrentBalance() {
        return null;
    }

    public List<Transaction> getAllExpenses() {
        return dao.getAllExpenses();
    }
}