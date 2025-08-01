package com.turki.gamefyback.controller;

import com.turki.gamefyback.model.Offer;
import com.turki.gamefyback.model.PcPricing;
import com.turki.gamefyback.model.WorkDaySchedule;
import com.turki.gamefyback.service.OfferPricingWorkScheduleService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class OfferPricingWorkScheduleController {

    private final OfferPricingWorkScheduleService service;

    // -------------------- Offer --------------------

    @PostMapping("/offers")
    public ResponseEntity<Offer> createOffer(@RequestBody Offer offer) {
        return ResponseEntity.ok(service.createOffer(offer));
    }

    @GetMapping("/offers")
    public ResponseEntity<List<Offer>> getAllOffers() {
        return ResponseEntity.ok(service.getAllOffers());
    }

    @GetMapping("/offers/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable String id) {
        return ResponseEntity.ok(service.getOfferById(id));
    }

    @GetMapping("/offers/by-type/{type}/active/by-daterange")
    public ResponseEntity<Offer> getActiveOfferByTypeAndOverlap(
            @PathVariable Offer.ReservationType type,
            @RequestParam("checkIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam("checkOut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut) {
        System.out.println("checkin time : " + checkIn);
        System.out.println("checkout time : " + checkOut);
        return service.getActiveOfferByTypeAndOverlap(type, checkIn, checkOut)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // @GetMapping("/offers/by-type/{type}/active")
    // public ResponseEntity<Offer> getActiveOfferByType(@PathVariable
    // Offer.ReservationType type) {
    // return service.getActiveOfferByType(type)
    // .map(ResponseEntity::ok)
    // .orElse(ResponseEntity.notFound().build());
    // }

    @PutMapping("/offers/{id}")
    public ResponseEntity<Offer> updateOffer(@PathVariable String id, @RequestBody Offer offer) {
        return ResponseEntity.ok(service.updateOffer(id, offer));
    }

    @DeleteMapping("/offers/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable String id) {
        service.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- PcPricing --------------------

    @PostMapping("/pc-pricings")
    public ResponseEntity<PcPricing> createPcPricing(@RequestBody PcPricing pcPricing) {
        return ResponseEntity.ok(service.createPcPricing(pcPricing));
    }

    @GetMapping("/pc-pricings")
    public ResponseEntity<List<PcPricing>> getAllPcPricings() {
        return ResponseEntity.ok(service.getAllPcPricings());
    }

    @GetMapping("/pc-pricings/by-type/{type}")
    public ResponseEntity<PcPricing> getPcPricingByType(@PathVariable PcPricing.PcType type) {
        return ResponseEntity.ok(service.getPcPricingByType(type));
    }

    @GetMapping("/pc-pricings/{id}")
    public ResponseEntity<PcPricing> getPcPricingById(@PathVariable String id) {
        return ResponseEntity.ok(service.getPcPricingById(id));
    }

    @PutMapping("/pc-pricings/{id}")
    public ResponseEntity<PcPricing> updatePcPricing(@PathVariable String id, @RequestBody PcPricing pcPricing) {
        return ResponseEntity.ok(service.updatePcPricing(id, pcPricing));
    }

    @DeleteMapping("/pc-pricings/{id}")
    public ResponseEntity<Void> deletePcPricing(@PathVariable String id) {
        service.deletePcPricing(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- WorkDaySchedule --------------------

    @PostMapping("/workday-schedules")
    public ResponseEntity<WorkDaySchedule> createWorkDaySchedule(@RequestBody WorkDaySchedule schedule) {
        return ResponseEntity.ok(service.createWorkDaySchedule(schedule));
    }

    @GetMapping("/workday-schedules")
    public ResponseEntity<List<WorkDaySchedule>> getAllWorkDaySchedules() {
        return ResponseEntity.ok(service.getAllWorkDaySchedules());
    }

    @GetMapping("/workday-schedules/{id}")
    public ResponseEntity<WorkDaySchedule> getWorkDayScheduleById(@PathVariable String id) {
        return ResponseEntity.ok(service.getWorkDayScheduleById(id));
    }

    @PutMapping("/workday-schedules/{id}")
    public ResponseEntity<WorkDaySchedule> updateWorkDaySchedule(@PathVariable String id,
                                                                 @RequestBody WorkDaySchedule schedule) {
        return ResponseEntity.ok(service.updateWorkDaySchedule(id, schedule));
    }

    @DeleteMapping("/workday-schedules/{id}")
    public ResponseEntity<Void> deleteWorkDaySchedule(@PathVariable String id) {
        service.deleteWorkDaySchedule(id);
        return ResponseEntity.noContent().build();
    }
}