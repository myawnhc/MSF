package com.hazelcast.msfdemo.ordersvc.domain;

import com.hazelcast.msf.persistence.DTO;

import java.io.Serializable;

public class Order extends DTO<String> implements Serializable {

    private String orderNumber;
    private String acctNumber;
    private String itemNumber;
    private String location;
    private int extendedPrice;
    private int quantity;

    public Order() {}

    // Commands
    public void setOrderNumber(String orderNum) { this.orderNumber = orderNum; }
    public String getOrderNumber() { return this.orderNumber; }

    public void setAcctNumber(String acctNum) { this.acctNumber = acctNum; }
    public String getAcctNumber() { return this.acctNumber; }

    public void setItemNumber(String itemNum) { this.itemNumber = itemNum; }
    public String getItemNumber() { return this.itemNumber; }

    public void setLocation(String location) { this.location = location; }
    public String getLocation() { return location; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getQuantity() { return quantity; }

    public void setExtendedPrice(int price) { this.extendedPrice = price; }
    public int getExtendedPrice() { return extendedPrice; }
}
