package com.turki.gamefyback.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class Course {

    @Id
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    private CourseType courseType;

    private int capacityLimit;

    private int currentEnrollment = 0;

    private boolean isActive = true;

    @DBRef
    private User instructor; // Reference to a coach user

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private List<CourseSession> sessions = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum CourseType {
        GAME_DEVELOPMENT,
        ESPORTS_COACHING,
        GENERAL
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseSession {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String location; // Can be "ONLINE" or a physical location
    }
}
