package com.turki.gamefyback.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "coach_availabilities")
public class CoachAvailability {

    @Id
    private String id;

    @DBRef
    @NotNull
    private User coach;

    // Regular weekly schedule
    private List<TimeSlot> regularSchedule = new ArrayList<>();

    // Special dates (vacations, one-time availability changes)
    private List<SpecialDate> specialDates = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlot {
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private boolean availableOnline;
        private boolean availableInPerson;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialDate {
        private LocalDateTime date;
        private boolean isAvailable; // If false, this is time off; if true, this is special available time
        private LocalTime startTime; // Only relevant if isAvailable is true
        private LocalTime endTime; // Only relevant if isAvailable is true
        private boolean availableOnline; // Only relevant if isAvailable is true
        private boolean availableInPerson; // Only relevant if isAvailable is true
    }
}