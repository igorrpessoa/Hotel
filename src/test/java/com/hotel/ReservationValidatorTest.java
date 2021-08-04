package com.hotel;

import com.hotel.exception.ReservationValidationException;
import com.hotel.model.Reservation;
import com.hotel.repository.ReservationRepository;
import com.hotel.util.HotelUtils;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class ReservationValidatorTest {

    @InjectMocks
    private ReservationValidator reservationValidator;

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    public void givenStartDateBeforeEndDateWhenValidateThenThrowReservationValidationException() {
        LocalDate startDate = HotelUtils.parseStringToLocalDate("2021-08-05");
        LocalDate endDate = HotelUtils.parseStringToLocalDate("2021-08-04");

        Exception reservationValidationException = assertThrows(ReservationValidationException.class, () ->
                reservationValidator.validateReservationDate(startDate, endDate));

        assertEquals("Start Date should be before then End Date", reservationValidationException.getMessage());
    }

    @Test
    public void givenStartDateBeingTodayWhenValidateThenThrowReservationValidationException() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(2);

        Exception reservationValidationException = assertThrows(ReservationValidationException.class, () ->
                reservationValidator.validateReservationDate(startDate, endDate));

        assertEquals("Start Date should be after today", reservationValidationException.getMessage());
    }


    @Test
    public void givenStartDate30DaysAheadTodayWhenValidateThenThrowReservationValidationException() {
        LocalDate startDate = LocalDate.now().plusDays(31);
        LocalDate endDate = LocalDate.now().plusDays(32);

        Exception reservationValidationException = assertThrows(ReservationValidationException.class, () ->
                reservationValidator.validateReservationDate(startDate, endDate));

        assertEquals("You can only book a Room at max 30 days ahead", reservationValidationException.getMessage());
    }

    @Test
    public void given4DayReservationWhenValidateThenThrowReservationValidationException() {
        LocalDate startDate = HotelUtils.parseStringToLocalDate("2021-08-01");
        LocalDate endDate = HotelUtils.parseStringToLocalDate("2021-08-04");

        Exception reservationValidationException = assertThrows(ReservationValidationException.class, () ->
                reservationValidator.validateReservationDate(startDate, endDate));

        assertEquals("Not possible to book a Room for more than 3 days", reservationValidationException.getMessage());
    }

    @Test
    public void givenDatesAlreadyBookedWhenValidateThenThrowReservationValidationException() {
        LocalDate startDate = HotelUtils.parseStringToLocalDate("2021-08-02");
        LocalDate endDate = HotelUtils.parseStringToLocalDate("2021-08-04");
        List<Reservation> availableReservations = new ArrayList<>();
        availableReservations.add(new Reservation());

        Mockito.when(reservationRepository.listReservedRooms(Timestamp.valueOf(startDate.atStartOfDay()),
                Timestamp.valueOf(endDate.atStartOfDay()))).thenReturn(availableReservations);
        Exception reservationValidationException = assertThrows(ReservationValidationException.class, () ->
                reservationValidator.validateReservationDate(startDate, endDate));

        assertEquals("The Room is not available for the requested period", reservationValidationException.getMessage());
    }
}