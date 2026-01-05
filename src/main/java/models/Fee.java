package models;
import java.sql.Date;
import java.math.BigDecimal; // Dùng BigDecimal cho tiền tệ chính xác hơn

public class Fee {
    private int fId;
    private String title;
    private BigDecimal amount;
    private Date deadline;
    private String description;
    private int paidCount;
    private java.math.BigDecimal targetAmount;


    public Fee() {}

    public Fee(int fId, String title, BigDecimal amount, Date deadline, String description) {
        this.fId = fId;
        this.title = title;
        this.amount = amount;
        this.deadline = deadline;
        this.description = description;
    }

    public int getFId() { return fId; }
    public void setFId(int fId) { this.fId = fId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPaidCount() { return paidCount; }
    public void setPaidCount(int paidCount) { this.paidCount = paidCount; }

    public java.math.BigDecimal getTargetAmount() {
        return targetAmount != null ? targetAmount : java.math.BigDecimal.ZERO;
    }
    public void setTargetAmount(java.math.BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }



}