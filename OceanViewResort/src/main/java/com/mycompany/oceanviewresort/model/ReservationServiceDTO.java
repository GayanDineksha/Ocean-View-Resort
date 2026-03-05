/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.model;

/**
 *
 * @author User
 */
public class ReservationServiceDTO {
    private long reservationId;
    private int serviceId;
    private int quantity;
    private long addedBy;

    public ReservationServiceDTO() {}

    public long getReservationId() { return reservationId; }
    public void setReservationId(long reservationId) { this.reservationId = reservationId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public long getAddedBy() { return addedBy; }
    public void setAddedBy(long addedBy) { this.addedBy = addedBy; }
}
