package com.hotel.mapper;

import com.hotel.util.HotelUtils;
import com.hotel.dto.ReservationDTO;
import com.hotel.model.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationDTO reservationToReservationDTO(Reservation reservation) {
        return new ReservationDTO(
                HotelUtils.parseTimestampToString(reservation.getStartDate()),
                HotelUtils.parseTimestampToString(reservation.getEndDate()),
                reservation.getReservationCode());
    }
}
