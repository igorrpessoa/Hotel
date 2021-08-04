package com.hotel.validator;

import com.hotel.exception.ReservationValidationException;
import com.hotel.model.Reservation;
import com.hotel.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.*;
import java.util.List;

@Component
public class ReservationValidator {

    @Autowired
    private ReservationRepository reservationRepository;


    /*    Validate the reservation regarding the rules of the HOTEL
     *
     * The Hotel contains only 1 room;
     * Your stay can’t be longer than 3 days;
     * The room can’t be reserved more than 30 days in advance;
     * All reservations start at least the next day of booking;
     * A “DAY’ in the hotel room starts from 00:00 to 23:59:59.
     * */
    public void validateReservationDate(LocalDate startDate, LocalDate endDate) throws ReservationValidationException {

        if(endDate.isBefore(startDate)) {
            throw new ReservationValidationException("Start Date should be before then End Date");
        }

        if(startDate.equals(LocalDate.now()) || startDate.isBefore(LocalDate.now())){
            throw new ReservationValidationException("Start Date should be after today");
        }

        if(!validateDaysToBook(startDate, endDate)) {
            throw new ReservationValidationException("Not possible to book a Room for more than 3 days");
        }

        if(!validateDayOfReservation(startDate)) {
            throw new ReservationValidationException("You can only book a Room at max 30 days ahead");
        }

        List<Reservation> availableReservations =
                reservationRepository.listReservedRooms(Timestamp.valueOf(startDate.atStartOfDay()), Timestamp.valueOf(endDate.atStartOfDay()));
        if(availableReservations.size() > 0) {
            throw new ReservationValidationException("The Room is not available for the requested period");
        }
    }

    //Currently using Montreal timezone considering the Hotel places there
    private Boolean validateDayOfReservation(LocalDate startDate) {
        Instant now = Instant.now();
        ZonedDateTime montrealTime = now.atZone(ZoneId.of("America/Montreal"));

        return !startDate.isAfter(montrealTime.toLocalDate().plusDays(30));

    }

    private Boolean validateDaysToBook(LocalDate d1, LocalDate d2) {
        long differenceDays = Duration.between(d1.atStartOfDay(), d2.atStartOfDay()).toDays();
        return differenceDays < 3;
    }

}
