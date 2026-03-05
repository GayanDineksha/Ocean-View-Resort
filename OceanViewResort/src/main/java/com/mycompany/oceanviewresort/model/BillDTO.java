/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.model;

/**
 *
 * @author User
 */
public class BillDTO {
    private long billId;
    private String billNumber;
    private long reservationId;
    private String guestName;
    private String guestPhone; 
    private String guestEmail; 
    private String reservationNumber;
    
    private double roomCharges;
    private double serviceCharges;
    private double taxAmount;
    private double discountAmount;
    private double totalAmount;
    private double amountPaid;
    private double balanceDue;
    
    private String paymentStatus;
    private String billDate;

    public BillDTO() {}

    // Getters and Setters
    public long getBillId() { return billId; }
    public void setBillId(long billId) { this.billId = billId; }

    public long getReservationId() { return reservationId; }
    public void setReservationId(long reservationId) { this.reservationId = reservationId; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }

    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    public double getRoomCharges() { return roomCharges; }
    public void setRoomCharges(double roomCharges) { this.roomCharges = roomCharges; }

    public double getServiceCharges() { return serviceCharges; }
    public void setServiceCharges(double serviceCharges) { this.serviceCharges = serviceCharges; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public double getBalanceDue() { return balanceDue; }
    public void setBalanceDue(double balanceDue) { this.balanceDue = balanceDue; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getBillDate() { return billDate; }
    public void setBillDate(String billDate) { this.billDate = billDate; }
}