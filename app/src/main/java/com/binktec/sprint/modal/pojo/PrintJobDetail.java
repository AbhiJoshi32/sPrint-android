package com.binktec.sprint.modal.pojo;

public class PrintJobDetail {
    private PrintTransaction printTransaction;
    private String status;
    private String issuedDate;
    private String issuedTime;
    private String confirmedDate;
    private String completedTime;
    private User user;
    private String tId;

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getIssuedTime() {
        return issuedTime;
    }

    public void setIssuedTime(String issuedTime) {
        this.issuedTime = issuedTime;
    }

    public String getConfirmedDate() {
        return confirmedDate;
    }

    public void setConfirmedDate(String confirmedDate) {
        this.confirmedDate = confirmedDate;
    }

    public String getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(String completedTime) {
        this.completedTime = completedTime;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PrintTransaction getPrintTransaction() {
        return printTransaction;
    }

    public void setPrintTransaction(PrintTransaction printTransaction) {
        this.printTransaction = printTransaction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
