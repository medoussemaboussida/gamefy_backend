package com.turki.gamefyback.repository;

import com.turki.gamefyback.model.PcPricing;
import com.turki.gamefyback.model.Role;
import com.turki.gamefyback.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page; // Add this import
import org.springframework.data.domain.Pageable; // Add this import

import java.util.Optional;

@Repository
public interface PcPricingRepository extends MongoRepository<PcPricing, String> {

    Optional<PcPricing> findByPcType(PcPricing.PcType pcType);

}