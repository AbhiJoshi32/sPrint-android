package com.binktec.sprint.modal.pojo;

public class PrintJobDetail {
    private PrintTransaction printTransaction;
    private String status;
    private String date;
    private String time;
    private User user;
    private String tId;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
