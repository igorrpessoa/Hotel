package com.hotel.controller;

import com.hotel.dto.ReservationDTO;
import com.hotel.exception.ReservationNotFoundException;
import com.hotel.exception.ReservationValidationException;
import com.hotel.service.HotelService;
import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/hotel")
public class HotelController {

//    final static Logger logger = Logger.getLogger(HotelController.class);


    @Autowired
    private HotelService hotelService;

    @GetMapping(value = "/availability",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity getRoomAvailability(@RequestBody ReservationDTO reservation) throws ReservationValidationException {
        if(reservation == null
                || StringUtils.isNullOrEmpty(reservation.getStartDate())
                || StringUtils.isNullOrEmpty(reservation.getEndDate())) {
            return ResponseEntity.badRequest().build();
        }

        hotelService.getRoomAvailability(reservation);
        return ResponseEntity.ok().body("The Room is available for the selected period");
    }

    @GetMapping(value = "/reservations",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getReservations() {
        List<ReservationDTO> reservationDTOS = hotelService.getRooms();
        return ResponseEntity.ok().body(reservationDTOS.toString());
    }

    @GetMapping(value = "/reservation/{reservationCode}")
    public ResponseEntity getReservation(@PathVariable("reservationCode") String reservationCode) throws ReservationNotFoundException {
       if(StringUtils.isNullOrEmpty(reservationCode)){
           return ResponseEntity.badRequest().build();
       }
        ReservationDTO reservation = hotelService.findReservation(reservationCode);
        return ResponseEntity.ok().body(reservation.toString());
    }

    @DeleteMapping(value = "/reservation/{reservationCode}")
    public ResponseEntity cancelReservation(@PathVariable("reservationCode") String reservationCode) throws ReservationNotFoundException{
        if(StringUtils.isNullOrEmpty(reservationCode)){
            return ResponseEntity.badRequest().build();
        }
        hotelService.deleteReservation(reservationCode);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/reservation",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity createReservation(@RequestBody ReservationDTO reservation) throws ReservationValidationException {
        if(reservation == null
                || StringUtils.isNullOrEmpty(reservation.getStartDate())
                || StringUtils.isNullOrEmpty(reservation.getEndDate())) {
            return ResponseEntity.badRequest().build();
        }

        ReservationDTO reservationCreated = hotelService.createReservation(reservation);
        URI uri = URI.create("http://localhost:8080/hotel/reservation/" + reservationCreated.getReservationCode());
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/reservation",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity updateReservation(@RequestBody ReservationDTO reservation) throws ReservationValidationException {
        if(reservation == null
                || StringUtils.isNullOrEmpty(reservation.getStartDate())
                || StringUtils.isNullOrEmpty(reservation.getEndDate())) {
            return ResponseEntity.badRequest().build();
        }

        ReservationDTO reservationCreated = hotelService.updateReservation(reservation);
        return ResponseEntity.ok(reservationCreated.toString());

    }


}
