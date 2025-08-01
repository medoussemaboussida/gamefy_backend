package com.turki.gamefyback.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "offers")
public class Offer {

    @Id
    private String id;

    @NotNull
    private String name;

    @NotNull
    private Status status;

    @NotNull
    private ReservationType type;

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

    // Percentage value, e.g., 20 for 20%
    @NotNull
    @Min(0)
    @Max(80)
    private Integer percentage;

    public enum ReservationType {
        PC_ONLY,
        COACHING,
        PC_WITH_COACHING,
        VIP_ROOM
    }

    public enum Status {
        ACTIVE,
        NOT_ACTIVE
    }

}