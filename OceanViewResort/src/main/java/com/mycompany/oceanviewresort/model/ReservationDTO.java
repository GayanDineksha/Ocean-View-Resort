/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.model;

/**
 *
 * @author User
 */
public class ReservationDTO {

    private long reservationId;
    private String reservationNumber;
    
    // --- Reservation Details ---
    private long roomId;
    private String roomDetails; 
    private String checkInDate;
    private String checkOutDate;
    private int adults;
    private int children;
    private String specialRequests;
    private String reservationStatus;
    private double roomRateAtBooking;
    
    // --- Guest Details (For the form) ---
    private long guestId;
    private String guestName;
    private String nicPassport;
    private String contactNumber;
    private String email;
    
    // --- User who created it ---
    private long createdBy;

    public ReservationDTO() {}

    // Getters and Setters
    public long getReservationId() { return reservationId; }
    public void setReservationId(long reservationId) { this.reservationId = reservationId; }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    public long getRoomId() { return roomId; }
    public void setRoomId(long roomId) { this.roomId = roomId; }

    public String getRoomDetails() { return roomDetails; }
    public void setRoomDetails(String roomDetails) { this.roomDetails = roomDetails; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public int getAdults() { return adults; }
    public void setAdults(int adults) { this.adults = adults; }

    public int getChildren() { return children; }
    public void setChildren(int children) { this.children = children; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public String getReservationStatus() { return reservationStatus; }
    public void setReservationStatus(String reservationStatus) { this.reservationStatus = reservationStatus; }

    public double getRoomRateAtBooking() { return roomRateAtBooking; }
    public void setRoomRateAtBooking(double roomRateAtBooking) { this.roomRateAtBooking = roomRateAtBooking; }

    public long getGuestId() { return guestId; }
    public void setGuestId(long guestId) { this.guestId = guestId; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getNicPassport() { return nicPassport; }
    public void setNicPassport(String nicPassport) { this.nicPassport = nicPassport; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public long getCreatedBy() { return createdBy; }
    public void setCreatedBy(long createdBy) { this.createdBy = createdBy; }
}