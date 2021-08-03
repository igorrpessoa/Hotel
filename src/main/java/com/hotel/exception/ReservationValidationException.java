package com.hotel.exception;

public class ReservationValidationException extends Exception{

    public ReservationValidationException(String errorMessage) {
        super(errorMessage);
    }
}
