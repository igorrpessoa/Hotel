package com.hotel.service;

import com.hotel.util.HotelUtils;
import com.hotel.exception.ReservationNotFoundException;
import com.hotel.exception.ReservationValidationException;
import com.hotel.dto.ReservationDTO;
import com.hotel.mapper.ReservationMapper;
import com.hotel.model.Reservation;
import com.hotel.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class HotelService {

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private ReservationRepository reservationRepository;

    public void getRoomAvailability(ReservationDTO reservation) throws ParseException, ReservationValidationException {

        Timestamp startDate = HotelUtils.parseStringToTimestamp(reservation.getStartDate());
        Timestamp endDate = HotelUtils.parseStringToTimestamp(reservation.getEndDate());

        validateReservationDate(startDate, endDate);
    }

    public ReservationDTO createReservation(ReservationDTO reservation) throws ReservationValidationException, ParseException {


        Timestamp startDate = HotelUtils.parseStringToTimestamp(reservation.getStartDate());
        Timestamp endDate = HotelUtils.parseStringToTimestamp(reservation.getEndDate());

        validateReservationDate(startDate, endDate);

        Reservation reservationToBook = new Reservation();
        reservationToBook.setStartDate(startDate);
        reservationToBook.setEndDate(endDate);
        reservationToBook.setReservationCode(reservationToBook.generateReservationCode());
        reservationRepository.save(reservationToBook);
        return reservationMapper.reservationToReservationDTO(reservationToBook);
    }

    public ReservationDTO updateReservation(ReservationDTO reservation) throws ReservationValidationException, ParseException {

        Optional<Reservation> reservationBooked = reservationRepository.findByReservationCode(reservation.getReservationCode());

        if(reservationBooked.isPresent()) {
            Reservation reservationToBook = reservationBooked.get();
            Timestamp startDate = HotelUtils.parseStringToTimestamp(reservation.getStartDate());
            Timestamp endDate = HotelUtils.parseStringToTimestamp(reservation.getEndDate());

            validateReservationDate(startDate, endDate);

            reservationToBook.setStartDate(startDate);
            reservationToBook.setEndDate(endDate);
            reservationRepository.save(reservationToBook);
            return reservationMapper.reservationToReservationDTO(reservationToBook);
        }
        return null;
    }

    public void deleteReservation(String reservationCode) throws ReservationNotFoundException {
        Optional<Reservation> reservation = reservationRepository.findByReservationCode(reservationCode);
        if(reservation.isPresent()) {
            reservationRepository.delete(reservation.get());
        } else {
            throw new ReservationNotFoundException("Not possible to find reservation.");
        }
    }

    /*    Validate the reservation regarding the rules of the HOTEL
    *
    * The Hotel contains only 1 room;
    * Your stay can’t be longer than 3 days;
    * The room can’t be reserved more than 30 days in advance;
    * All reservations start at least the next day of booking;
    * A “DAY’ in the hotel room starts from 00:00 to 23:59:59.
    * */
    private void validateReservationDate(Timestamp startDate, Timestamp endDate) throws ReservationValidationException {

        if(endDate.before(startDate)) {
            throw new ReservationValidationException("Start Date should be before then End Date");
        }

        if(!validateDaysToBook(startDate, endDate)) {
            throw new ReservationValidationException("Not possible to book a Room for more than 3 days");
        }

        if(!validateDayOfReservation(startDate)) {
            throw new ReservationValidationException("You can only book a Room at max 30 days ahead");
        }

        List<Reservation> availableReservations =
                reservationRepository.listReservedRooms(startDate, endDate);
        if(availableReservations.size() > 0) {
            throw new ReservationValidationException("The Room is not available for the requested period");
        }
    }

    private Boolean validateDayOfReservation(Timestamp sd) {
        LocalDate startDate = sd.toLocalDateTime().toLocalDate();
        LocalDate today = LocalDate.now();
        return !startDate.isAfter(today.plusDays(30));

    }

    private Boolean validateDaysToBook(Timestamp d1, Timestamp d2) {
        long difference_In_Days
                = TimeUnit
                .MILLISECONDS
                .toDays(d2.getTime() - d1.getTime());
        return difference_In_Days <= 3;
    }


    public ReservationDTO findReservation(String reservationCode) throws ReservationNotFoundException {
        Optional<Reservation> reservation = reservationRepository.findByReservationCode(reservationCode);
        if(reservation.isPresent()) {
            return reservationMapper.reservationToReservationDTO(reservation.get());
        }
        throw new ReservationNotFoundException("Could not find the reservation with the reservation code provided");
    }
}
