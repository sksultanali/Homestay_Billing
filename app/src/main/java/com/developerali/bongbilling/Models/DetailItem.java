package com.developerali.bongbilling.Models;

public class DetailItem {
    private int PAX;
    private String description;
    private int nights;
    private double price;
    private int room;
    private double total;

    public int getPAX() { return PAX; }
    public void setPAX(int PAX) { this.PAX = PAX; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getNights() { return nights; }
    public void setNights(int nights) { this.nights = nights; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getRoom() { return room; }
    public void setRoom(int room) { this.room = room; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}

