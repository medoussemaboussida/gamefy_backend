package com.turki.gamefyback.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "work_day_schedules")
public class WorkDaySchedule {

    @Id
    private String id;

    // E.g., "July 2025"
    @NotNull
    private Month month;

    // E.g., MONDAY
    @NotNull
    private Weekday weekday;

    // E.g., "10AM"
    @NotNull
    private String startTime;

    // E.g., "2AM"
    @NotNull
    private String endTime;

    public enum Weekday {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    //change months to enum type
        public enum Month {
        JANUARY,
        FEBRUARY,
        MARCH,
        APRIL,
        MAY,
        JUNE,
        JULY,
        AUGUST,
        SEPTEMBER,
        OCTOBER,
        NOVEMBER,
        DECEMBER
    }
}