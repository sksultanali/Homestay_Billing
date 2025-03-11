package com.developerali.bongbilling.Models;

public class DetailsModel {
    String description;
    Integer price, PAX, Child, room, nights, total;

    public DetailsModel(String description, Integer price, Integer PAX, Integer Child,Integer room, Integer nights, Integer total) {
        this.description = description;
        this.price = price;
        this.PAX = PAX;
        this.Child = Child;
        this.room = room;
        this.nights = nights;
        this.total = total;
    }

    public DetailsModel() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPAX() {
        return PAX;
    }

    public void setPAX(Integer PAX) {
        this.PAX = PAX;
    }

    public Integer getChild() {
        return Child;
    }

    public void setChild(Integer child) {
        Child = child;
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public Integer getNights() {
        return nights;
    }

    public void setNights(Integer nights) {
        this.nights = nights;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
