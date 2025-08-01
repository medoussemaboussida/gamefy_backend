package com.turki.gamefyback.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.turki.gamefyback.model.Payment;
import com.turki.gamefyback.model.Reservation;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
@Setter
@Getter
public class ReservationDisplayDTO {
    private String id;
    private Reservation.ReservationType type;

    // Student details
    private String studentId;
    private String studentFirstName;
    private String studentLastName;
    private String studentEmail;
    private String studentPhoneNumber; // From the form's "phone detail"
    private String username; // For non-registered users

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Africa/Tunis")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Africa/Tunis")
    private LocalDateTime endTime;
    @Size(max = 10, message = "Maximum 10 PCs per reservation")
    private List<Integer> pcNumbers;

    // Coach details
    private String coachId;
    private String coachFirstName;
    private String coachLastName;
    private String coachingGame;

    private Reservation.ReservationStatus status;
    private BigDecimal price;
    private String notes;

    // Payment details (simplified)
    private String paymentStatus; // e.g., "COMPLETED", "PENDING", "OFFLINE"
    private String paymentType; // e.g., "PC_RESERVATION", "COACHING_RESERVATION"
    private String transactionId; // If payment is processed

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Reservation.ReservationType getType() {
        return type;
    }

    public void setType(Reservation.ReservationType type) {
        this.type = type;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentPhoneNumber() {
        return studentPhoneNumber;
    }

    public void setStudentPhoneNumber(String studentPhoneNumber) {
        this.studentPhoneNumber = studentPhoneNumber;
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

    public String getCoachFirstName() {
        return coachFirstName;
    }

    public void setCoachFirstName(String coachFirstName) {
        this.coachFirstName = coachFirstName;
    }

    public String getCoachLastName() {
        return coachLastName;
    }

    public void setCoachLastName(String coachLastName) {
        this.coachLastName = coachLastName;
    }

    public String getCoachingGame() {
        return coachingGame;
    }

    public void setCoachingGame(String coachingGame) {
        this.coachingGame = coachingGame;
    }

    public Reservation.ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(Reservation.ReservationStatus status) {
        this.status = status;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}