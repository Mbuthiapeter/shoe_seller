package com.business.peter.shoeseller.model;

/**
 * Created by Victor on 03/02/2017.
 */
public class ListSales extends ListData {
    String name, size, buying_price, selling_price,profit,sid;

    public String getName() {
        return name;
    }

    public void setName(String name) {this.name = name; }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBuying_price() {
        return buying_price;
    }

    public void setBuying_price(String buying_price) {
        this.buying_price = buying_price;
    }

    public String getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(String selling_price) {
        this.selling_price = selling_price;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
