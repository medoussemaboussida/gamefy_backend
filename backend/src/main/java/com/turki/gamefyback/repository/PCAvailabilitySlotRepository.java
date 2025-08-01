package com.turki.gamefyback.repository;

import com.turki.gamefyback.model.PCAvailabilitySlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PCAvailabilitySlotRepository extends MongoRepository<PCAvailabilitySlot, String> {

    // Query to find overlapping slots for a specific PC number
    // Checks if any existing slot overlaps with the requested startTime to endTime
    @Query("{ 'pcNumber': ?0, " +
            "  $or: [ " +
            "    { 'startTime': { $lt: ?2 }, 'endTime': { $gt: ?1 } }, " + // Existing slot starts before requested ends
                                                                           // AND ends after requested starts
            "    { 'startTime': { $gte: ?1, $lt: ?2 } }, " + // Existing slot starts within requested range
            "    { 'endTime': { $gt: ?1, $lte: ?2 } } " + // Existing slot ends within requested range
            "  ] " +
            "}")
    List<PCAvailabilitySlot> findOverlappingSlots(int pcNumber, LocalDateTime requestedStartTime,
            LocalDateTime requestedEndTime);

    @Query("{ $and: [ " +
           "{ 'startTime': { $lt: ?1 } }, " +  // Existing slot starts before our end time
           "{ 'endTime': { $gt: ?0 } } " +     // Existing slot ends after our start time
           "] }")
    List<PCAvailabilitySlot> findOverlappingSlotsReservation(Instant  start, Instant  end);

    // Find all slots for a given PC within a time range
    List<PCAvailabilitySlot> findByPcNumberAndStartTimeBetween(int pcNumber, LocalDateTime start, LocalDateTime end);

    List<PCAvailabilitySlot> findByPcNumberAndStartTimeBeforeAndEndTimeAfter(int pcNumber, LocalDateTime start,
            LocalDateTime end);

    // Find slots by reservation
    Optional<PCAvailabilitySlot> findByReservationId(String reservationId);

    // @Query("SELECT p.pcNumber FROM PCAvailabilitySlot p WHERE " +
    //         "(:start < p.endTime AND :end > p.startTime)")
    @Query("SELECT p.pcNumber FROM PCAvailabilitySlot p WHERE " +
            "(:start < p.endTime AND :end > p.startTime)")
    List<PCAvailabilitySlot> findBookedPCsInRange(LocalDateTime start, LocalDateTime end);

    List<PCAvailabilitySlot> findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    void deleteByReservationId(String reservationId);

}