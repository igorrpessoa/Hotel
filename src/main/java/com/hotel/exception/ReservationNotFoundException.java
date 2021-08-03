package com.hotel.exception;

public class ReservationNotFoundException extends Exception{

    public ReservationNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
