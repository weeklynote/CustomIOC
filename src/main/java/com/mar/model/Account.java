package com.mar.model;

/**
 * @Author: 刘劲
 * @Date: 2020/4/7 23:08
 */
public class Account {

    private String cardNo;
    private String name;
    private int money;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        return money;
    }

    public void addMoney(int money){
        this.money = this.money + money;
    }

    public void minusMoney(int money){
        this.money = this.money - money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getCardNo() { return cardNo; }

    public void setCardNo(String cardNo) { this.cardNo = cardNo;}

    @Override
    public String toString() {
        return "Account{" +
                "cardNo='" + cardNo + '\'' +
                ", name='" + name + '\'' +
                ", money=" + money +
                '}';
    }
}
