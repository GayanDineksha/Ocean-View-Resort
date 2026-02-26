/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.model;
import java.sql.Date;
/**
 *
 * @author User
 */
public class Reservation {
    private int reservationId;
    private String reservationNumber;
    private int guestId;
    private int roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private int adults;
    private int children;
    private String reservationStatus;

    public Reservation() {}

    // Getters and Setters (අනිවාර්යයෙන්ම Generate කරගන්න NetBeans වලින් - Right Click -> Insert Code -> Getter and Setter)
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }
    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public Date getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Date checkInDate) { this.checkInDate = checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(Date checkOutDate) { this.checkOutDate = checkOutDate; }
    public int getAdults() { return adults; }
    public void setAdults(int adults) { this.adults = adults; }
    public int getChildren() { return children; }
    public void setChildren(int children) { this.children = children; }
    public String getReservationStatus() { return reservationStatus; }
    public void setReservationStatus(String reservationStatus) { this.reservationStatus = reservationStatus; }
}