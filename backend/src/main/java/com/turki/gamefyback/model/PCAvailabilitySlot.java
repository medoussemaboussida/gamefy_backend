package com.turki.gamefyback.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;

// @Data // Removed @Data as explicit getters and setters are being added
@NoArgsConstructor // Lombok @NoArgsConstructor generates a no-argument constructor
@AllArgsConstructor // Lombok @AllArgsConstructor generates a constructor with all arguments
@Document(collection = "pc_availability_slots")
// Compound index to quickly check for overlapping reservations for a specific
// PC
@CompoundIndex(def = "{'pcNumber': 1, 'startTime': 1, 'endTime': 1}")
public class PCAvailabilitySlot {

    @Id
    private String id;

    @Min(0)
    @Max(10) // Assuming 10 PCs as per requirement
    private int pcNumber;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    // Similar helper methods
    public LocalDateTime getStartTimeLocal() {
        return startTime.atZone(ZoneId.of("Africa/Tunis")).toLocalDateTime();
    }

    public LocalDateTime getEndTimeLocal() {
        return endTime.atZone(ZoneId.of("Africa/Tunis")).toLocalDateTime();
    }

    @DBRef
    @NotNull
    private Reservation reservation; // Link to the actual reservation

    // --- Getters ---
    public String getId() {
        return id;
    }

    public int getPcNumber() {
        return pcNumber;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Reservation getReservation() {
        return reservation;
    }

    // --- Setters ---
    public void setId(String id) {
        this.id = id;
    }

    public void setPcNumber(int pcNumber) {
        this.pcNumber = pcNumber;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
