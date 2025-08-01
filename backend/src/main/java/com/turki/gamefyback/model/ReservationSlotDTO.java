package com.turki.gamefyback.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Size;

public class ReservationSlotDTO {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;
    
    @Size(max = 10, message = "Maximum 10 PCs per reservation")
    private List<Integer> pcNumbers;

    // Similar helper methods
    public LocalDateTime getStartTimeLocal() {
        return startTime.atZone(ZoneId.of("Africa/Tunis")).toLocalDateTime();
    }

    public LocalDateTime getEndTimeLocal() {
        return endTime.atZone(ZoneId.of("Africa/Tunis")).toLocalDateTime();
    }

    // Constructors, getters, and setters
    public ReservationSlotDTO() {
    }

    public ReservationSlotDTO(LocalDateTime startTime, LocalDateTime endTime, List<Integer> pcNumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.pcNumbers = pcNumber;
    }

    // Getters and Setters
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
}
