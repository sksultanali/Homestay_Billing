package com.developerali.bongbilling.Models;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

public class CompanyModel {

    String name, address, contact, code;
    Drawable img, back_img, side_img;
    int color;

    public CompanyModel(String name, String address, String contact, String code,
                        Drawable img, Drawable back_img, Drawable side_img, int color) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.code = code;
        this.img = img;
        this.back_img = back_img;
        this.side_img = side_img;
        this.color = color;
    }

    public CompanyModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Drawable getImg() {
        return img;
    }

    public void setImg(Drawable img) {
        this.img = img;
    }

    public Drawable getBack_img() {
        return back_img;
    }

    public void setBack_img(Drawable back_img) {
        this.back_img = back_img;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Drawable getSide_img() {
        return side_img;
    }

    public void setSide_img(Drawable side_img) {
        this.side_img = side_img;
    }

    @Override
    public String toString() {
        return name;
    }
}
