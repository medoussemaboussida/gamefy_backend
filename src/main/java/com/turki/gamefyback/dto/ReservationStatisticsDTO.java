package com.turki.gamefyback.dto;

import com.turki.gamefyback.model.Reservation.ReservationType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ReservationStatisticsDTO {
    private int totalReservations;
    private BigDecimal totalRevenue;
    private Map<ReservationType, Long> typeBreakdown;
    private BigDecimal averagePrice;
    // New fields for monthly breakdown
    private Map<Integer, Long> monthlyBreakdown;

    // New fields for top user with most reservations
    private String topUserId;
    private Long topUserReservationCount;
}