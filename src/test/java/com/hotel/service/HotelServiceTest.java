package com.hotel.service;

import com.hotel.HotelApplication;
import com.hotel.exception.ReservationValidationException;
import com.hotel.mapper.ReservationMapper;
import com.hotel.model.Reservation;
import com.hotel.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = HotelApplication.class)
class HotelServiceTest {

    @InjectMocks
    private HotelService hotelService;

    @Mock
    ReservationMapper reservationMapper;

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    public void givenStartDateBeforeEndDateWhenValidateThenThrowReservationValidationException() {
        LocalDate startDate = LocalDate.now().plusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(1);

        Exception reservationValidationException = assertThrows(ReservationValidationException.class, () ->
                hotelService.validateReservationDate(startDate, endDate));

        assertEquals("Start Date should be before then End Date", reservationValidationException.getMessage());
    }

    @Test
    public void givenStartDateBeingTodayWhenValidateThenThrowReservationValidationException() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(2);

        Exception reservationValidationException = assertThrows(ReservationValidationException.class, () ->
                hotelService.validateReservationDate(startDate, endDate));

        assertEquals("Start Date should be after today", reservationValidationException.getMessage());
    }


    @Test
    public void givenStartDate30DaysAheadTodayWhenValidateThenThrowReservationValidationException() {
        LocalDate startDate = LocalDate.now().plusDays(31);
        LocalDate endDate = LocalDate.now().plusDays(32);

        Exception reservationValidationException = assertThrows(ReservationValidationException.class, () ->
                hotelService.validateReservationDate(startDate, endDate));

        assertEquals("You can only book a Room at max 30 days ahead", reservationValidationException.getMessage());
    }

    @Test
    public void given4DayReservationWhenValidateThenThrowReservationValidationException() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate =  LocalDate.now().plusDays(5);

        Exception reservationValidationException = assertThrows(ReservationValidationException.class, () ->
                hotelService.validateReservationDate(startDate, endDate));

        assertEquals("Not possible to book a Room for more than 3 days", reservationValidationException.getMessage());
    }

//    @Test
//    public void givenDatesAlreadyBookedWhenValidateThenThrowReservationValidationException() {
//        LocalDate startDate = LocalDate.now().plusDays(1);
//        LocalDate endDate = LocalDate.now().plusDays(2);
//        List<Reservation> availableReservations = new ArrayList<>();
//        availableReservations.add(new Reservation());
//
//        Mockito.when(reservationRepository.listReservedRooms(Timestamp.valueOf(startDate.atStartOfDay()),
//                Timestamp.valueOf(endDate.atStartOfDay()))).thenReturn(availableReservations);
//        Exception reservationValidationException = assertThrows(ReservationValidationException.class, () ->
//                hotelService.validateReservationDate(startDate, endDate));
//
//        assertEquals("The Room is not available for the requested period", reservationValidationException.getMessage());
//    }
}