package models;
import java.sql.Date;

public class Notification {
    private int nId;
    private int uId;
    private String title;
    private String message;
    private Date createdAt;

    public Notification() {}
    public Notification(int uId, String title, String message) {
        this.uId = uId;
        this.title = title;
        this.message = message;
        this.createdAt = new Date(System.currentTimeMillis());
    }


    public int getNId() { return nId; }
    public void setNId(int nId) { this.nId = nId; }
    public int getUId() { return uId; }
    public void setUId(int uId) { this.uId = uId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}