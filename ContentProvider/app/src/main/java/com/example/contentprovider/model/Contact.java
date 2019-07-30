package com.example.contentprovider.model;

import android.database.Cursor;
import android.graphics.Bitmap;

public class Contact implements Comparable<Contact> {
    private String name;
    private String phoneNumber;
    private Bitmap avatar;

    public Contact(String name, String phoneNumber, Bitmap avatar) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
    }

    @Override
    public int compareTo(Contact contact) {
        return this.name.compareTo(contact.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }
}
