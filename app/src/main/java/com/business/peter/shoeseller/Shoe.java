package com.business.peter.shoeseller;

/**
 * Created by Victor on 13/09/2016.
 */
public class Shoe {
    private String name;
    private String description;
    private String sid;
    private String color;
    private String type;
    private String date_bought;
    private String buying_price;
    private String selling_price;
    private String profit;
    private String date_sold;
    private String image;

    public Shoe() {
    }

    public Shoe(String name, String description, String sid, String color, String type, String date_bought, String buying_price, String selling_price, String profit, String date_sold, String image) {
        this.name = name;
        this.description = description;
        this.sid = sid;
        this.color = color;
        this.type = type;
        this.date_bought = date_bought;
        this.buying_price = buying_price;
        this.selling_price = selling_price;
        this.profit = profit;
        this.date_sold = date_sold;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(String selling_price) {
        this.selling_price = selling_price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String sid) {
        this.profit = profit;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate_bought() {
        return date_bought;
    }

    public void setDate_bought(String date_bought) {
        this.date_bought = date_bought;
    }

    public String getBuying_price() {
        return buying_price;
    }

    public void setBuying_price(String buying_price) {
        this.buying_price = buying_price;
    }

    public String getDate_sold() {
        return date_sold;
    }

    public void setDate_sold(String date_sold) {
        this.date_sold = date_sold;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
