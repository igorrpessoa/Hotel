package com.hotel.service;

import com.hotel.ReservationValidator;
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
import java.util.Optional;

@Service
public class HotelService {

    @Autowired
    private ReservationValidator reservationValidator;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private ReservationRepository reservationRepository;

    public void getRoomAvailability(ReservationDTO reservation) throws ReservationValidationException {

        LocalDate startDate = HotelUtils.parseStringToLocalDate(reservation.getStartDate());
        LocalDate endDate = HotelUtils.parseStringToLocalDate(reservation.getEndDate());

        reservationValidator.validateReservationDate(startDate, endDate);
    }

    public ReservationDTO createReservation(ReservationDTO reservation) throws ReservationValidationException {


        LocalDate startDate = HotelUtils.parseStringToLocalDate(reservation.getStartDate());
        LocalDate endDate = HotelUtils.parseStringToLocalDate(reservation.getEndDate());

        reservationValidator.validateReservationDate(startDate, endDate);

        Reservation reservationToBook = new Reservation();
        reservationToBook.setStartDate(Timestamp.valueOf(startDate.atStartOfDay()));
        reservationToBook.setEndDate(Timestamp.valueOf(endDate.atStartOfDay()));
        reservationToBook.setReservationCode(reservationToBook.generateReservationCode());
        reservationRepository.save(reservationToBook);
        return reservationMapper.reservationToReservationDTO(reservationToBook);
    }

    public ReservationDTO updateReservation(ReservationDTO reservation) throws ReservationValidationException {

        Optional<Reservation> reservationBooked = reservationRepository.findByReservationCode(reservation.getReservationCode());

        if(reservationBooked.isPresent()) {
            Reservation reservationToBook = reservationBooked.get();
            LocalDate startDate = HotelUtils.parseStringToLocalDate(reservation.getStartDate());
            LocalDate endDate = HotelUtils.parseStringToLocalDate(reservation.getEndDate());

            reservationValidator.validateReservationDate(startDate, endDate);

            reservationToBook.setStartDate(Timestamp.valueOf(startDate.atStartOfDay()));
            reservationToBook.setEndDate(Timestamp.valueOf(endDate.atStartOfDay()));
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

    public ReservationDTO findReservation(String reservationCode) throws ReservationNotFoundException {
        Optional<Reservation> reservation = reservationRepository.findByReservationCode(reservationCode);
        if(reservation.isPresent()) {
            return reservationMapper.reservationToReservationDTO(reservation.get());
        }
        throw new ReservationNotFoundException("Could not find the reservation with the reservation code provided");
    }
}
