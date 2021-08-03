package com.hotel.mapper;

import com.hotel.HotelUtils;
import com.hotel.dto.ReservationDTO;
import com.hotel.model.Reservation;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class ReservationMapper {

    public ReservationDTO reservationToReservationDTO(Reservation reservation) throws ParseException {
        return new ReservationDTO(
                HotelUtils.parseTimestampToString(reservation.getStartDate()),
                HotelUtils.parseTimestampToString(reservation.getEndDate()),
                reservation.getReservationCode());
    }
}
