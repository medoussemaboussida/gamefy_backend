package com.turki.gamefyback.controller;

import com.google.firebase.database.annotations.NotNull;
import com.turki.gamefyback.dto.ReservationRequestDTO;
import com.turki.gamefyback.dto.ReservationDisplayDTO;
import com.turki.gamefyback.dto.ReservationStatisticsDTO;
import com.turki.gamefyback.model.Reservation;
import com.turki.gamefyback.service.ReservationService;
import com.turki.gamefyback.repository.PCAvailabilitySlotRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.turki.gamefyback.model.PCAvailabilitySlot;
import com.turki.gamefyback.model.ReservationSlotDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    // Endpoint for users to create a reservation
    @PostMapping
    public ResponseEntity<ReservationDisplayDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO requestDTO) {
        // Get the authenticated user's UID to link the reservation for security and
        // consistency
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ReservationDisplayDTO createdReservation = reservationService.createReservation(requestDTO);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @GetMapping("/available-pcs")
    public List<Integer> getAvailablePCs(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull LocalDateTime end) {
        return reservationService.getAvailablePCs(start, end);
    }

    // Endpoint to get a single reservation by ID (e.g., for user to view details)
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDisplayDTO> getReservationById(@PathVariable String id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint for Admin Dashboard to view all reservations
    // Add @PreAuthorize("hasRole('ROLE_MANAGER')") for admin access
    @GetMapping
    public ResponseEntity<List<ReservationDisplayDTO>> getAllReservations() {
        List<ReservationDisplayDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    // Endpoint for a specific user to view their reservation history
    // This typically requires authorization to ensure a user can only view their
    // own history.
    // @PreAuthorize("#studentUid == authentication.principal.uid") // Example using
    // SpEL for authorization
    @GetMapping("/history/{studentUid}")
    public ResponseEntity<List<ReservationDisplayDTO>> getUserReservations(@PathVariable String studentUid) {
        List<ReservationDisplayDTO> userReservations = reservationService.getUserReservations(studentUid);
        return ResponseEntity.ok(userReservations);
    }

    // Endpoint to cancel a reservation
    // Add authorization check: only the student who booked or an admin can cancel
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable String id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    // NEW ENDPOINT: To check PC availability for the frontend
    @GetMapping("/pc-availability")
    public ResponseEntity<List<Integer>> getAvailablePcs(
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull LocalDateTime endTime) {

        List<Integer> availablePcs = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            if (reservationService.isPcAvailable(i, startTime, endTime)) {
                availablePcs.add(i);
            }
        }
        return ResponseEntity.ok(availablePcs);
    }

    @GetMapping("/pc/{pcNumber}/booked-slots")
    public ResponseEntity<List<PCAvailabilitySlot>> getBookedPcSlotsForDay(
            @PathVariable("pcNumber") int pcNumber,
            @RequestParam("date") @jakarta.validation.constraints.NotNull LocalDate date) {

        List<PCAvailabilitySlot> bookedSlots = reservationService.getBookedPcSlotsForDay(pcNumber, date);
        return ResponseEntity.ok(bookedSlots);
    }

    //////////////////oussema methods /////////////////////////////

    @PostMapping("/admin")
    public ResponseEntity<ReservationDisplayDTO> createAdminReservation(
            @Valid @RequestBody ReservationRequestDTO requestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ReservationDisplayDTO createdReservation = reservationService.createAdminReservation(requestDTO);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationDisplayDTO> updateReservationStatus(
            @PathVariable String id,
            @RequestBody Reservation.ReservationStatus status) { // Expecting ReservationStatus enum directly
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ReservationDisplayDTO updatedReservation = reservationService.updateReservationStatus(id, status);
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable String id) {
        reservationService.deleteReservationWithPcAvailability(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/statistics")
    public ResponseEntity<ReservationStatisticsDTO> getReservationStatistics() {
        ReservationStatisticsDTO stats = reservationService.getReservationStatistics();
        return ResponseEntity.ok(stats);
    }
    // New endpoint for PDF statistics report
    @GetMapping("/statistics/pdf")
    public ResponseEntity<ReservationStatisticsDTO> getReservationStatisticsPDF() {
        ReservationStatisticsDTO stats = reservationService.getReservationStatisticsPDF();
        return ResponseEntity.ok(stats);
    }
}