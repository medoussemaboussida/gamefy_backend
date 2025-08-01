package com.turki.gamefyback.dto;

import java.math.BigDecimal; // Import ReservationType if needed for validation
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import com.turki.gamefyback.model.Reservation;

import lombok.Data;

@Data
public class ReservationRequestDTO {

    // For linking to the authenticated student
    //@NotBlank(message = "Student UID is required")
    private String studentUid; // Frontend sends Firebase UID of the logged-in student
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username; // For non-registered users

    @Pattern(regexp = "\\+?[0-9]{8,15}", message = "Invalid phone number format")
    private String phoneNumber; // For non-registered users
    @NotNull(message = "Reservation type is required")
    private Reservation.ReservationType type;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future") // Ensures reservation is not in the past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future") // Ensures reservation is not in the past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    // For PC reservations
    @Size(max = 10, message = "Maximum 10 PCs per reservation")
    private List<Integer> pcNumbers;

    // For coaching reservations
    private String coachId; // Optional: ID of the selected coach
    private String coachingGame; // Optional: e.g., "League of Legends", "Valorant"

    // Price should ideally be calculated on backend, but can be sent for confirmation/validation
    //@NotNull(message = "Price is required")
    //@Positive(message = "Price must be positive")
    private BigDecimal price;

    private String notes; // Optional notes from the form

    // For payment method indication from the form (e.g., "Payment Offline")
    private boolean isOfflinePayment; // If true, payment is pending offline

    public String getStudentUid() {
        return studentUid;
    }

    public void setStudentUid(String studentUid) {
        this.studentUid = studentUid;
    }

    public @NotNull(message = "Reservation type is required") Reservation.ReservationType getType() {
        return type;
    }

    public void setType(@NotNull(message = "Reservation type is required") Reservation.ReservationType type) {
        this.type = type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    // public Integer getPcNumber() {
    //     return pcNumber;
    // }

    // public void setPcNumber(Integer pcNumber) {
    //     this.pcNumber = pcNumber;
    // }

    public String getCoachId() {
        return coachId;
    }

    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    public String getCoachingGame() {
        return coachingGame;
    }

    public void setCoachingGame(String coachingGame) {
        this.coachingGame = coachingGame;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isOfflinePayment() {
        return isOfflinePayment;
    }

    public void setOfflinePayment(boolean offlinePayment) {
        isOfflinePayment = offlinePayment;
    }
}