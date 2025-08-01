package com.turki.gamefyback.service;

import com.turki.gamefyback.model.Offer;
import com.turki.gamefyback.model.PcPricing;
import com.turki.gamefyback.model.WorkDaySchedule;
import com.turki.gamefyback.repository.OfferRepository;
import com.turki.gamefyback.repository.PcPricingRepository;
import com.turki.gamefyback.repository.WorkDayScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OfferPricingWorkScheduleService {

    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private PcPricingRepository pcPricingRepository;
    @Autowired
    private WorkDayScheduleRepository workDayScheduleRepository;

    // -------------------- Offre CRUD --------------------
    @CacheEvict(value = "offers", allEntries = true)
    public Offer createOffer(Offer offer) {
        return offerRepository.save(offer);
    }
    @Cacheable(value = "offers", key = "'all'")
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }
    public Offer getOfferById(String id) {
        return offerRepository.findById(id).orElseThrow(IllegalStateException::new);
    }

    public Optional<Offer> getActiveOfferByTypeAndOverlap(Offer.ReservationType type, LocalDateTime checkIn,
                                                          LocalDateTime checkOut) {
        return offerRepository.findFirstByTypeAndStatusAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                type, Offer.Status.ACTIVE, checkOut, checkIn);
    }

    public Optional<Offer> getActiveOfferByType(Offer.ReservationType type) {
        return offerRepository.findFirstByTypeAndStatus(type, Offer.Status.ACTIVE);
    }
    @CacheEvict(value = "offers", allEntries = true)
    public Offer updateOffer(String id, Offer updatedOffer) {
        Offer existing = getOfferById(id);
        updatedOffer.setId(existing.getId());
        return offerRepository.save(updatedOffer);
    }
    @CacheEvict(value = "offers", allEntries = true)
    public void deleteOffer(String id) {
        offerRepository.deleteById(id);
    }

    // -------------------- PcPricing CRUD --------------------
    @CacheEvict(value = "pc-pricings", allEntries = true)
    public PcPricing createPcPricing(PcPricing pcPricing) {
        return pcPricingRepository.save(pcPricing);
    }
    @Cacheable(value = "pc-pricings", key = "'all'")
    public List<PcPricing> getAllPcPricings() {
        return pcPricingRepository.findAll();
    }
    public PcPricing getPcPricingById(String id) {
        return pcPricingRepository.findById(id).orElseThrow(IllegalStateException::new);
    }
    public PcPricing getPcPricingByType(PcPricing.PcType pcType) {
        return pcPricingRepository.findByPcType(pcType).orElseThrow(IllegalStateException::new);
    }
    @CacheEvict(value = "pc-pricings", allEntries = true)
    public PcPricing updatePcPricing(String id, PcPricing updatedPcPricing) {
        PcPricing existing = getPcPricingById(id);
        updatedPcPricing.setId(existing.getId());
        return pcPricingRepository.save(updatedPcPricing);
    }
    @CacheEvict(value = "pc-pricings", allEntries = true)
    public void deletePcPricing(String id) {
        pcPricingRepository.deleteById(id);
    }

    // -------------------- WorkDaySchedule CRUD --------------------

    public WorkDaySchedule createWorkDaySchedule(WorkDaySchedule schedule) {
        return workDayScheduleRepository.save(schedule);
    }
    @Cacheable(value = "workday-schedules", key = "'all'")
    public List<WorkDaySchedule> getAllWorkDaySchedules() {
        return workDayScheduleRepository.findAll();
    }

    @CacheEvict(value = "pc-pricings", allEntries = true)
    public WorkDaySchedule getWorkDayScheduleById(String id) {
        return workDayScheduleRepository.findById(id).orElseThrow(IllegalStateException::new);
    }
    @CacheEvict(value = "pc-pricings", allEntries = true)
    public WorkDaySchedule updateWorkDaySchedule(String id, WorkDaySchedule updatedSchedule) {
        WorkDaySchedule existing = getWorkDayScheduleById(id);
        updatedSchedule.setId(existing.getId());
        return workDayScheduleRepository.save(updatedSchedule);
    }
    @CacheEvict(value = "pc-pricings", allEntries = true)
    public void deleteWorkDaySchedule(String id) {
        workDayScheduleRepository.deleteById(id);
    }
}