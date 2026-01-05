package models;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {

    private int tId;
    private transactionType tType;
    private BigDecimal tAmount;
    private String tNote;
    private String image;
    private Date createAt;
    private int userId;
    private int fcId;

    public Transaction() {
    }

    public Transaction(int tId, transactionType tType, BigDecimal tAmount,
                       String tNote, String image,Date createAt, int userId, int fcId) {
        this.tId = tId;
        this.tType = tType;
        this.tAmount = tAmount;
        this.tNote = tNote;
        this.image = image;
        this.createAt = createAt;
        this.userId = userId;
        this.fcId = fcId;
    }

    public Transaction(int userId, int fcId, transactionType tType,
                       BigDecimal tAmount, String tNote, String image, Date createAt) {
        this.userId = userId;
        this.fcId = fcId;
        this.tType = tType;
        this.tAmount = tAmount;
        this.tNote = tNote;
        this.image = image;
        this.createAt = createAt;
    }

    public int getTId() {
        return tId;
    }

    public void setTId(int tId) {
        this.tId = tId;
    }

    public transactionType getTType() {
        return tType;
    }

    public void setTType(transactionType tType) {
        this.tType = tType;
    }

    public BigDecimal getTAmount() {
        return tAmount;
    }

    public void setTAmount(BigDecimal tAmount) {
        this.tAmount = tAmount;
    }

    public String getTNote() {
        return tNote;
    }

    public void setTNote(String tNote) {
        this.tNote = tNote;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFcId() {
        return fcId;
    }

    public void setFcId(int fcId) {
        this.fcId = fcId;
    }
    public Date getCreateAt() {
        return createAt;
    }
    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "tId=" + tId +
                ", tType=" + tType +
                ", tAmount=" + tAmount +
                ", tNote='" + tNote + '\'' +
                ", userId=" + userId +
                ", fcId=" + fcId +
                '}';
    }
}
