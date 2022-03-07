package com.hotel.service;

import com.hotel.util.HotelUtils;
import com.hotel.exception.ReservationNotFoundException;
import com.hotel.exception.ReservationValidationException;
import com.hotel.dto.ReservationDTO;
import com.hotel.mapper.ReservationMapper;
import com.hotel.model.Reservation;
import com.hotel.repository.ReservationRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelService {

    final static Logger logger = Logger.getLogger(HotelService.class);

    @Autowired
    public HotelService(ReservationMapper reservationMapper, ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
    }

    private ReservationMapper reservationMapper;

    private ReservationRepository reservationRepository;

    public void getRoomAvailability(ReservationDTO reservation) throws ReservationValidationException {
        logger.debug(String.format("Getting room availability for %s to %s",
                reservation.getStartDate(), reservation.getEndDate()));
        LocalDate startDate = HotelUtils.parseStringToLocalDate(reservation.getStartDate());
        LocalDate endDate = HotelUtils.parseStringToLocalDate(reservation.getEndDate());

        validateReservationDate(startDate, endDate);
    }

    public List<ReservationDTO> getRooms() {
        Instant now = Instant.now();
        ZonedDateTime cancunTime = now.atZone(ZoneId.of("America/Cancun"));
        List<Reservation> reservationList = reservationRepository.listReservationsByToday(Timestamp.from(cancunTime.toInstant()));
        return reservationList.stream().map(r -> reservationMapper.reservationToReservationDTO(r)).collect(Collectors.toList());
    }

    public ReservationDTO createReservation(ReservationDTO reservation) throws ReservationValidationException {

        logger.debug(String.format("Creating reservation for %s to %s",
                reservation.getStartDate(), reservation.getEndDate()));
        LocalDate startDate = HotelUtils.parseStringToLocalDate(reservation.getStartDate());
        LocalDate endDate = HotelUtils.parseStringToLocalDate(reservation.getEndDate());

        validateReservationDate(startDate, endDate);

        Reservation reservationToBook = new Reservation();
        reservationToBook.setStartDate(Timestamp.valueOf(startDate.atStartOfDay()));
        reservationToBook.setEndDate(Timestamp.valueOf(endDate.atStartOfDay()));
        reservationToBook.setReservationCode(reservationToBook.generateReservationCode());
        reservationRepository.save(reservationToBook);
        return reservationMapper.reservationToReservationDTO(reservationToBook);
    }

    public ReservationDTO updateReservation(ReservationDTO reservation) throws ReservationValidationException {
        logger.debug(String.format("Updating reservation for %s to %s",
                reservation.getStartDate(), reservation.getEndDate()));
        Optional<Reservation> reservationBooked = reservationRepository.findByReservationCode(reservation.getReservationCode());

        if(reservationBooked.isPresent()) {
            Reservation reservationToBook = reservationBooked.get();
            LocalDate startDate = HotelUtils.parseStringToLocalDate(reservation.getStartDate());
            LocalDate endDate = HotelUtils.parseStringToLocalDate(reservation.getEndDate());

            validateReservationDate(startDate, endDate);

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
            logger.debug(String.format("Deleting reservation for %s to %s",
                    reservation.get().getStartDate(), reservation.get().getEndDate()));
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

    //Currently using Cancun timezone considering the Hotel places there
    private Boolean validateDayOfReservation(LocalDate startDate) {
        Instant now = Instant.now();
        ZonedDateTime cancunTime = now.atZone(ZoneId.of("America/Cancun"));

        return !startDate.isAfter(cancunTime.toLocalDate().plusDays(30));

    }

    private Boolean validateDaysToBook(LocalDate d1, LocalDate d2) {
        long differenceDays = Duration.between(d1.atStartOfDay(), d2.atStartOfDay()).toDays();
        return differenceDays < 3;
    }
}
