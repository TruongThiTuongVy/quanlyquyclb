package models;

public class FundCategory {
    private int fcId;
    private String fcName;
    private String type;

    public FundCategory() {

    }

    public  FundCategory(int fcId, String fcName, String type) {
        this.fcId = fcId;
        this.fcName = fcName;
        this.type = type;
    }
    public int getFcId() {
        return fcId;
    }
    public void setFcId(int fcId) {
        this.fcId = fcId;
    }
    public String getFcName() {
        return fcName;
    }
    public void setFcName(String fcName) {
        this.fcName = fcName;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    @Override
    public String toString() {
        return fcName;
    }
}
