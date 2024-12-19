package com.dcom.dataModel;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userId;
    private String pwd;
    private String status;
    private String userType;
    private String email;

    public User(String pwd, String status, String userType, String email) {
        this.pwd = pwd;
        this.status = status;
        this.userType = userType;
        this.email = email;
    }

    public User(int userId, String pwd, String status, String userType, String email) {
        this.userId = userId;
        this.pwd = pwd;
        this.status = status;
        this.userType = userType;

        this.email = email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public int getUserId() {
        return userId;
    }
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }



}
