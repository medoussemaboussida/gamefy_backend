package com.turki.gamefyback.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "forms")
public class Form {

    @Id
    private String id;

    @NotNull
    private FormType formType;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private String phoneNumber;

    @NotBlank
    private String message;

    private FormStatus status = FormStatus.NEW;

    private String responseMessage; // Response from manager or webmaster

    private LocalDateTime respondedAt;

    private String respondedBy; // User ID of manager/webmaster who responded

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum FormType {
        CONTACT,
        COACH,
        PARTNER
    }

    public enum FormStatus {
        NEW,
        IN_PROGRESS,
        RESPONDED,
        CLOSED
    }
}