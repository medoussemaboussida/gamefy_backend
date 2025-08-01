package com.turki.gamefyback.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pc_pricings")
public class PcPricing {

    @Id
    private String id;

    @NotNull
    private PcType pcType;

    // For 1 hour base price
    @NotNull
    @Positive
    private Double oneHourPrice;

    // For 2 hour base price
    @NotNull
    @Positive
    private Double twoHourPrice;

    // For 3 hours base price
    @NotNull
    @Positive
    private Double threeHourPrice;

    public enum PcType {
        NORMAL,
        VIP
    }

    public enum ReservationType {
        PC_ONLY,
        COACHING,
        PC_WITH_COACHING,
        VIP_ROOM
    }
}