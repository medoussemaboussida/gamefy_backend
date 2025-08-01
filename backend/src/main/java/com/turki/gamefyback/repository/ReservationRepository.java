package com.turki.gamefyback.repository;

import com.turki.gamefyback.model.Reservation;
import com.turki.gamefyback.model.User; // Import the User model
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, String> {

    // Method to find all reservations made by a specific student
    List<Reservation> findByStudent(User student);

    List<Reservation> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Reservation> findByUsername(String username);
    // List<Reservation> findByPcNumberAndStartTimeLessThanAndEndTimeGreaterThan(
    //     Integer pcNumber, 
    //     LocalDateTime endTime, 
    //     LocalDateTime startTime
    // );
    // You might also add methods for filtering by date, status, etc., as needed for
    // admin dashboard
    // List<Reservation> findByStatus(Reservation.ReservationStatus status);
    // List<Reservation> findByStartTimeBetween(LocalDateTime start, LocalDateTime
    // end);


   // New method to find reservations by type, excluding a specific status, and overlapping time range
   List<Reservation> findByTypeInAndStatusNotAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
           List<Reservation.ReservationType> types,
           Reservation.ReservationStatus status,
           LocalDateTime endTime,
           LocalDateTime startTime
   );

    // New method to find reservations by creation date range
    List<Reservation> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}