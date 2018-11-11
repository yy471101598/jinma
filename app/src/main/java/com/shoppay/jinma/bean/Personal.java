package com.shoppay.jinma.bean;

/**
 * Created by songxiaotao on 2018/1/23.
 */

public class Personal {

    /**
     * UserAccount : admin
     * UserName : 超管
     * UserShopName : 沙井店
     * UserPhone : 13333333333
     * UserCreateTime : 2014/11/28
     * lastlogin : 2018/1/23 14:47:08
     */

    private String UserAccount;
    private String UserName;
    private String UserShopName;
    private String UserPhone;
    private String UserCreateTime;
    private String lastlogin;

    public String getUserGroupName() {
        return UserGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        UserGroupName = userGroupName;
    }

    private String UserGroupName;

    public String getUserAccount() {
        return UserAccount;
    }

    public void setUserAccount(String UserAccount) {
        this.UserAccount = UserAccount;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getUserShopName() {
        return UserShopName;
    }

    public void setUserShopName(String UserShopName) {
        this.UserShopName = UserShopName;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String UserPhone) {
        this.UserPhone = UserPhone;
    }

    public String getUserCreateTime() {
        return UserCreateTime;
    }

    public void setUserCreateTime(String UserCreateTime) {
        this.UserCreateTime = UserCreateTime;
    }

    public String getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(String lastlogin) {
        this.lastlogin = lastlogin;
    }
}
