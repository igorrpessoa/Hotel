package com.hotel.dto;

import org.json.JSONObject;

public class ReservationDTO {

    public ReservationDTO() {
    }

    public ReservationDTO(String startDate, String endDate, String reservationCode) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.reservationCode = reservationCode;
    }

    private String startDate;

    private String endDate;

    private String reservationCode;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }
}
