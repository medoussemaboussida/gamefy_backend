package com.turki.gamefyback.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reservation")
public class Reservation {

    @Id
    private String id;

    @NotNull
    private ReservationType type;

    @DBRef
 //   @NotNull // Student must be associated with a reservation
    private User student; // The student who made the reservation
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username; // For non-registered users
    @Pattern(regexp = "\\+?[0-9]{8,15}", message = "Invalid phone number format")
    private String phoneNumber; // For non-registered users

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    // Add these helper methods
    public LocalDateTime getStartTimeLocal() {
        return startTime.atZone(ZoneId.of("Africa/Tunis")).toLocalDateTime();
    }

    public LocalDateTime getEndTimeLocal() {
        return endTime.atZone(ZoneId.of("Africa/Tunis")).toLocalDateTime();
    }

    // @ElementCollection
    // @Column(name = "pc_number")
    private List<Integer> pcNumbers;
    // For coaching reservations
    @DBRef
    private User coach; // If this is a coaching reservation

    // NEW FIELD: To store the game selected for coaching
    private String coachingGame; // e.g., "League of Legends", "Valorant", "CS:GO"

    @NotNull
    private ReservationStatus status = ReservationStatus.PENDING;

    //@Positive
    private BigDecimal price; // Total price of the reservation

    private String notes; // Any special requests or notes (not in UI, can be ignored or used by default)

    @DBRef
    private Payment payment; // Reference to payment details

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum ReservationType {
        PC_ONLY,
        COACHING,
        PC_WITH_COACHING,
        VIP_ROOM
    }

    public enum ReservationStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        COMPLETED
    }

    // public String getId() {
    //     return id;
    // }

    // public void setId(String id) {
    //     this.id = id;
    // }

    // public ReservationType getType() {
    //     return type;
    // }

    // public void setType(ReservationType type) {
    //     this.type = type;
    // }

    // public User getStudent() {
    //     return student;
    // }

    // public void setStudent(User student) {
    //     this.student = student;
    // }

    // public LocalDateTime getStartTime() {
    //     return startTime;
    // }

    // public void setStartTime(LocalDateTime startTime) {
    //     this.startTime = startTime;
    // }

    // public LocalDateTime getEndTime() {
    //     return endTime;
    // }

    // public void setEndTime(LocalDateTime endTime) {
    //     this.endTime = endTime;
    // }

    // public Integer getPcNumber() {
    //     return pcNumber;
    // }

    // public void setPcNumber(Integer pcNumber) {
    //     this.pcNumber = pcNumber;
    // }

    // public User getCoach() {
    //     return coach;
    // }

    // public void setCoach(User coach) {
    //     this.coach = coach;
    // }

    // public String getCoachingGame() {
    //     return coachingGame;
    // }

    // public void setCoachingGame(String coachingGame) {
    //     this.coachingGame = coachingGame;
    // }

    // public ReservationStatus getStatus() {
    //     return status;
    // }

    // public void setStatus(ReservationStatus status) {
    //     this.status = status;
    // }

    // public BigDecimal getPrice() {
    //     return price;
    // }

    // public void setPrice(BigDecimal price) {
    //     this.price = price;
    // }

    // public String getNotes() {
    //     return notes;
    // }

    // public void setNotes(String notes) {
    //     this.notes = notes;
    // }

    // public Payment getPayment() {
    //     return payment;
    // }

    // public void setPayment(Payment payment) {
    //     this.payment = payment;
    // }

    // public LocalDateTime getCreatedAt() {
    //     return createdAt;
    // }

    // public void setCreatedAt(LocalDateTime createdAt) {
    //     this.createdAt = createdAt;
    // }

    // public LocalDateTime getUpdatedAt() {
    //     return updatedAt;
    // }

    // public void setUpdatedAt(LocalDateTime updatedAt) {
    //     this.updatedAt = updatedAt;
    // }
}