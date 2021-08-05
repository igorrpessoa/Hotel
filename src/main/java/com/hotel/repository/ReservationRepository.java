package com.hotel.repository;

import com.hotel.model.Reservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Integer> {

    @Query(value = "SELECT * from Reservation " +
            "WHERE (startDate <= :startDate AND endDate >= :startDate) " +
            "OR (startDate <= :endDate AND endDate >= :endDate)" +
            "OR (startDate >= :startDate AND endDate <= :endDate)", nativeQuery = true)
    List<Reservation> listReservedRooms(@Param("startDate") Timestamp startDate,
                                         @Param("endDate") Timestamp endDate);

    Optional<Reservation> findByReservationCode(@Param("reservationCode") String reservationCode);

    @Query(value = "SELECT * from Reservation " +
            "WHERE startDate >= :today OR (startDate <= :today AND endDate >= :today)", nativeQuery = true)
    List<Reservation> listReservationsByToday(@Param("today") Timestamp today);

}
