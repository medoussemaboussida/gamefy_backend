package com.turki.gamefyback.repository;

import com.turki.gamefyback.model.Offer;
import com.turki.gamefyback.model.Offer.ReservationType;
import com.turki.gamefyback.model.Offer.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OfferRepository extends MongoRepository<Offer, String> {

    Optional<Offer> findFirstByTypeAndStatus(ReservationType type, Status status);

    Optional<Offer> findFirstByTypeAndStatusAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            Offer.ReservationType type,
            Offer.Status status,
            LocalDateTime checkOut,
            LocalDateTime checkIn);

}