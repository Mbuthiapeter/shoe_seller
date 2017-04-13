package com.business.peter.shoeseller;

/**
 * Created by Victor on 29/11/2016.
 */
public class SoldShoe {
    private String name;
    private String comment;
    private String sid;
    private String date_bought;
    private String buying_price;
    private String selling_price;
    private String profit;
    private String date_sold;
    private String size;
    private String image;
    private String total_pro;
    private String total_sale;
    private String count;
    private String type;
    private String color;
    private int layout;

    public SoldShoe() {
    }

    public SoldShoe(String name, String comment, String sid, String date_bought, String buying_price, String selling_price, String profit, String date_sold,String size,String total_pro, String total_sale,String count, String image,String color, String type,int layout) {
        this.name = name;
        this.comment = comment;
        this.sid = sid;
        this.date_bought = date_bought;
        this.buying_price = buying_price;
        this.selling_price = selling_price;
        this.profit = profit;
        this.date_sold = date_sold;
        this.size = size;
        this.image = image;
        this.total_pro = total_pro;
        this.total_sale = total_sale;
        this.count = count;
        this.color = color;
        this.type = type;
        this.layout = layout;
    }

    public String getTotal_pro() {
        return total_pro;
    }

    public void setTotal_pro(String total_pro) {
        this.total_pro = total_pro;
    }

    public String getTotal_sale() {
        return total_sale;
    }

    public void setTotal_sale(String total_sale) {
        this.total_sale = total_sale;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
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

    public String getDate_sold() {
        return date_sold;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }
}
