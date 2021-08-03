package com.hotel.service;

import com.hotel.HotelUtils;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
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

    private void validateReservationDate(Timestamp startDate, Timestamp endDate) throws ParseException, ReservationValidationException {

        if(endDate.before(startDate)) {
            throw new ReservationValidationException("Start Date should be before then End Date");
        }
        //Not possible to book a Room for more than 3 days
        if(!validateDaysToBook(startDate, endDate)) {
            throw new ReservationValidationException("Not possible to book a Room for more than 3 days");
        }

        //Not possible to book a Room at max 30 days earlier
        if(!validateDayOfReservation(startDate)) {
            throw new ReservationValidationException("Not possible to book a Room at max 30 days earlier");
        }

        List<Reservation> availableReservations =
                reservationRepository.listReservedRooms(startDate, endDate);
        //Not possible to book a Room on the days requested
        if(availableReservations.size() > 0) {
            throw new ReservationValidationException("The Room is not available for the requested period");
        }
    }

    private Boolean validateDayOfReservation(Timestamp sd) {
        LocalDate startDate = sd.toLocalDateTime().toLocalDate();
        LocalDate today = LocalDate.now();
        if(startDate.isAfter(today.plusDays(30))
                && startDate.isAfter(today)){
            return false;
        } else {
            return true;
        }
    }

    private Boolean validateDaysToBook(Timestamp d1, Timestamp d2) {
        long difference_In_Days
                = TimeUnit
                .MILLISECONDS
                .toDays(d2.getTime() - d1.getTime());
        if(difference_In_Days > 3) {
            return false;
        }
        return true;
    }


    public ReservationDTO findReservation(String reservationCode) throws ReservationNotFoundException, ParseException {
        Optional<Reservation> reservation = reservationRepository.findByReservationCode(reservationCode);
        if(reservation.isPresent()) {
            return reservationMapper.reservationToReservationDTO(reservation.get());
        }
        throw new ReservationNotFoundException("Could not find the reservation with the reservation code provided");
    }
}
