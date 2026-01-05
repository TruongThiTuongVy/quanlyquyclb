package models;

import java.sql.Date;

public class FeeStatus {
    private int ufId;
    private int userId;
    private int fId;
    private status status;
    private Date paidDate;

    public FeeStatus() {
    }

    public FeeStatus(int ufId, int userId, int fId, status status, Date paidDate) {
        this.ufId = ufId;
        this.userId = userId;
        this.fId = fId;
        this.status = status;
        this.paidDate = paidDate;
    }


    public int getUfId() {
        return ufId;
    }

    public void setUfId(int ufId) {
        this.ufId = ufId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFId() {
        return fId;
    }

    public void setFId(int fId) {
        this.fId = fId;
    }

    public models.status getStatus() {
        return status;
    }

    public void setStatus(models.status status) {
        this.status = status;
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }
}