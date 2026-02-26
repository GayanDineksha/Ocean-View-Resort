/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.model;

/**
 *
 * @author User
 */
public class Guest {
    private int guestId;
    private String guestName;
    private String nicPassport;
    private String contactNumber;
    private String email;

    public Guest() {}

    public Guest(int guestId, String guestName, String nicPassport, String contactNumber, String email) {
        this.guestId = guestId;
        this.guestName = guestName;
        this.nicPassport = nicPassport;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    // Getters and Setters
    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getNicPassport() { return nicPassport; }
    public void setNicPassport(String nicPassport) { this.nicPassport = nicPassport; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

