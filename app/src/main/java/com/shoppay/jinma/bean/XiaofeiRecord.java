package com.shoppay.jinma.bean;

/**
 * Created by songxiaotao on 2018/1/24.
 */

public class XiaofeiRecord {

    /**
     * OrderAccount : 20180124143624
     * OrderID : 206
     * OrderType : 7
     * OrderTypeTxt : 商品消费
     * MemCard : 0
     * MemName : 散客
     * OrderTotalMoney : 100.0000
     * OrderDiscountMoney : 100.0000
     * OrderCreateTime : 2018/1/24 14:36:27
     */

    private String OrderAccount;
    private String OrderID;
    private String OrderType;
    private String OrderTypeTxt;
    private String MemCard;
    private String MemName;
    private String OrderTotalMoney;
    private String OrderDiscountMoney;
    private String OrderCreateTime;

    public String getOrderAccount() {
        return OrderAccount;
    }

    public void setOrderAccount(String OrderAccount) {
        this.OrderAccount = OrderAccount;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String OrderID) {
        this.OrderID = OrderID;
    }

    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String OrderType) {
        this.OrderType = OrderType;
    }

    public String getOrderTypeTxt() {
        return OrderTypeTxt;
    }

    public void setOrderTypeTxt(String OrderTypeTxt) {
        this.OrderTypeTxt = OrderTypeTxt;
    }

    public String getMemCard() {
        return MemCard;
    }

    public void setMemCard(String MemCard) {
        this.MemCard = MemCard;
    }

    public String getMemName() {
        return MemName;
    }

    public void setMemName(String MemName) {
        this.MemName = MemName;
    }

    public String getOrderTotalMoney() {
        return OrderTotalMoney;
    }

    public void setOrderTotalMoney(String OrderTotalMoney) {
        this.OrderTotalMoney = OrderTotalMoney;
    }

    public String getOrderDiscountMoney() {
        return OrderDiscountMoney;
    }

    public void setOrderDiscountMoney(String OrderDiscountMoney) {
        this.OrderDiscountMoney = OrderDiscountMoney;
    }

    public String getOrderCreateTime() {
        return OrderCreateTime;
    }

    public void setOrderCreateTime(String OrderCreateTime) {
        this.OrderCreateTime = OrderCreateTime;
    }
}
