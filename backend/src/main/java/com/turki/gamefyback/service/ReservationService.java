// gamefyback/service/ReservationService.java
package com.turki.gamefyback.service;

import com.turki.gamefyback.dto.ReservationRequestDTO;
import com.turki.gamefyback.dto.ReservationDisplayDTO;
import com.turki.gamefyback.dto.ReservationStatisticsDTO;
import com.turki.gamefyback.exception.UserNotFoundException;
import com.turki.gamefyback.model.*;
import com.turki.gamefyback.model.Reservation.ReservationType;
import com.turki.gamefyback.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PCAvailabilitySlotRepository pcAvailabilitySlotRepository;

    private static BigDecimal PC_HOURLY_RATE = new BigDecimal("8.00");
    private static BigDecimal VIP_ROOM_HOURLY_RATE = new BigDecimal("20.00");

    public List<Integer> getAvailablePCs(LocalDateTime start, LocalDateTime end) {
        List<Integer> allPCs = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        Instant startInstant = start.atZone(ZoneId.of("Africa/Tunis")).toInstant();
        Instant endInstant = end.atZone(ZoneId.of("Africa/Tunis")).toInstant();
        List<Integer> bookedPCs = pcAvailabilitySlotRepository.findOverlappingSlotsReservation(startInstant, endInstant)
                .stream()
                .map(PCAvailabilitySlot::getPcNumber)
                .distinct()
                .collect(Collectors.toList());
        return allPCs.stream()
                .filter(pc -> !bookedPCs.contains(pc))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationDisplayDTO createReservation(ReservationRequestDTO requestDTO) {
        User student = userRepository.findByUid(requestDTO.getStudentUid())
                .orElseThrow(
                        () -> new UserNotFoundException("Student not found with UID: " + requestDTO.getStudentUid()));

        if (requestDTO.getType() == Reservation.ReservationType.PC_ONLY
                || requestDTO.getType() == Reservation.ReservationType.PC_WITH_COACHING) {
            if (requestDTO.getPcNumbers() == null) {
                throw new IllegalArgumentException(
                        "PC number is required for PC_ONLY or PC_WITH_COACHING reservations.");
            }
        } else if (requestDTO.getType() == Reservation.ReservationType.VIP_ROOM) {
            requestDTO.setPcNumbers(List.of(0));

        } else if (requestDTO.getType() == Reservation.ReservationType.COACHING) {
            // Reserved for future use
        } else {
            throw new IllegalArgumentException("Unsupported reservation type: " + requestDTO.getType());
        }

        BigDecimal totalPrice = calculatePrice(requestDTO.getType(), requestDTO.getPcNumbers(),
                requestDTO.getStartTime(),
                requestDTO.getEndTime());

        Payment payment = new Payment();
        payment.setUser(student);
        payment.setPaymentType(Payment.PaymentType.PC_RESERVATION);
        payment.setAmount(totalPrice);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setTransactionId("OFFLINE_PENDING_" + UUID.randomUUID().toString().substring(0, 8));

        System.out.println("Storing times as LocalDateTime (Tunis):");
        System.out.println("Start: " + requestDTO.getStartTime());
        System.out.println("End: " + requestDTO.getEndTime());

        Reservation reservation = new Reservation();
        reservation.setType(requestDTO.getType());
        reservation.setStudent(student);
        reservation.setStartTime(requestDTO.getStartTime());
        reservation.setEndTime(requestDTO.getEndTime());
        reservation.setPcNumbers(requestDTO.getPcNumbers());
        reservation.setPrice(totalPrice);
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservation.setNotes(requestDTO.getNotes());
        // reservation.setPayment(payment);

        reservation = reservationRepository.save(reservation);
        Reservation savedReservation = reservation; // now final
        System.out.println("Stored times:");
        System.out.println("Start: " + reservation.getStartTime());
        System.out.println("End: " + reservation.getEndTime());
        if (requestDTO.getPcNumbers() != null && !requestDTO.getPcNumbers().isEmpty()) {
            requestDTO.getPcNumbers().forEach(pcNumber -> {
                PCAvailabilitySlot pcSlot = new PCAvailabilitySlot();
                pcSlot.setPcNumber(pcNumber);
                pcSlot.setStartTime(requestDTO.getStartTime());
                pcSlot.setEndTime(requestDTO.getEndTime());
                pcSlot.setReservation(savedReservation);
                pcAvailabilitySlotRepository.save(pcSlot);
            });
        }
        return mapToDisplayDTO(reservation);
    }

    public Optional<ReservationDisplayDTO> getReservationById(String id) {
        return reservationRepository.findById(id)
                .map(this::mapToDisplayDTO);
    }

    @Cacheable(value = "reservations", key = "'all'")
    public List<ReservationDisplayDTO> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(this::mapToDisplayDTO)
                .collect(Collectors.toList());
    }

    // ReservationService.java
    public boolean arePcsAvailable(List<Integer> pcNumbers, LocalDateTime start, LocalDateTime end) {
        return pcNumbers.stream()
                .allMatch(pc -> isPcAvailable(pc, start, end));
    }

    public List<ReservationDisplayDTO> getUserReservations(String studentUid) {
        User student = userRepository.findByUid(studentUid)
                .orElseThrow(() -> new UserNotFoundException("Student not found with UID: " + studentUid));

        return reservationRepository.findByStudent(student).stream()
                .map(this::mapToDisplayDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "reservations", key = "'all'")
    public void cancelReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + reservationId));

        if (reservation.getStatus() == Reservation.ReservationStatus.CONFIRMED
                || reservation.getStatus() == Reservation.ReservationStatus.PENDING) {
            reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);

            pcAvailabilitySlotRepository.findByReservationId(reservationId)
                    .ifPresent(pcAvailabilitySlotRepository::delete);
        } else {
            throw new IllegalArgumentException(
                    "Reservation cannot be cancelled in its current status: " + reservation.getStatus());
        }
    }

    public boolean isPcAvailable(int pcNumber, LocalDateTime startTime, LocalDateTime endTime) {
        List<PCAvailabilitySlot> overlappingSlots = pcAvailabilitySlotRepository.findOverlappingSlots(pcNumber,
                startTime, endTime);
        return overlappingSlots.isEmpty();
    }

    public List<PCAvailabilitySlot> getBookedPcSlotsForDay(int pcNumber, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay;
        endOfDay = date.atTime(23, 59, 59, 999999999);

        List<PCAvailabilitySlot> slots = pcAvailabilitySlotRepository.findByPcNumberAndStartTimeBetween(pcNumber,
                startOfDay, endOfDay);

        // For Saturday and Sunday, also include early-morning slots stored on "next"
        // day
        if (date.getDayOfWeek() == DayOfWeek.FRIDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            System.out.println("date.getDayOfWeek()");
            System.out.println("day of the week : " + date.getDayOfWeek());
            LocalDateTime earlyMorningStart = date.plusDays(1).atStartOfDay();
            LocalDateTime earlyMorningEnd = date.plusDays(1).atTime(4, 0); // up to 4AM

            List<PCAvailabilitySlot> earlySlots = pcAvailabilitySlotRepository
                    .findByPcNumberAndStartTimeBetween(pcNumber, earlyMorningStart, earlyMorningEnd);

            // Map early slots back visually to current day
            // for (PCAvailabilitySlot slot : earlySlots) {
            //     if (slot.getStartTime().toLocalTime().isBefore(LocalTime.of(4, 0))) {
            //         slot.setStartTime(slot.getStartTime().minusDays(1));
            //         slot.setEndTime(slot.getEndTime().minusDays(1));
            //     }
            //     System.out.println("slot endtime : " + slot.getEndTime());
            //     System.out.println("slot starttime : " + slot.getStartTime());
            // }

            slots.addAll(earlySlots);
        }
        return slots;

        // return pcAvailabilitySlotRepository.findByPcNumberAndStartTimeBetween(
        // pcNumber, startOfDay, endOfDay);
    }

    private BigDecimal calculatePrice(ReservationType type, List<Integer> pcNumbers,
                                      LocalDateTime start, LocalDateTime end) {

        long durationHours = java.time.Duration.between(start, end).toHours();
        if (durationHours <= 0) {
            throw new IllegalArgumentException("Reservation duration must be positive.");
        }
        if (type == Reservation.ReservationType.VIP_ROOM) {
            return VIP_ROOM_HOURLY_RATE.multiply(new BigDecimal(durationHours));
        }
        else if (type == Reservation.ReservationType.PC_ONLY || type == Reservation.ReservationType.PC_WITH_COACHING) {
            int numPCs = pcNumbers != null ? pcNumbers.size() : 1;
            return PC_HOURLY_RATE.multiply(new BigDecimal(durationHours)).multiply(new BigDecimal(numPCs));
        }
        else if (type == Reservation.ReservationType.COACHING) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    private ReservationDisplayDTO mapToDisplayDTO(Reservation reservation) {
        ReservationDisplayDTO dto = new ReservationDisplayDTO();
        dto.setId(reservation.getId());
        dto.setType(reservation.getType());

        if (reservation.getStudent() != null) {
            dto.setStudentId(reservation.getStudent().getId());
            dto.setStudentFirstName(reservation.getStudent().getFirstName());
            dto.setStudentLastName(reservation.getStudent().getLastName());
            dto.setStudentEmail(reservation.getStudent().getEmail());
            dto.setStudentPhoneNumber(reservation.getStudent().getPhoneNumber());
        } //oussema add this
        else {
            dto.setUsername(reservation.getUsername()); // Use username if student is null
            dto.setStudentPhoneNumber(reservation.getPhoneNumber()); // Use phoneNumber if student is null
        }

        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setPcNumbers(reservation.getPcNumbers());
        dto.setCoachingGame(null);
        dto.setStatus(reservation.getStatus());
        dto.setPrice(reservation.getPrice());
        dto.setNotes(reservation.getNotes());

        // if (reservation.getPayment() != null) {
        // dto.setPaymentStatus(reservation.getPayment().getStatus().name());
        // dto.setPaymentType(reservation.getPayment().getPaymentType().name());
        // dto.setTransactionId(reservation.getPayment().getTransactionId());
        // } else {
        // dto.setPaymentStatus(Payment.PaymentStatus.PENDING.name());
        // dto.setPaymentType(Payment.PaymentType.PC_RESERVATION.name());
        // dto.setTransactionId("N/A_OFFLINE");
        // }

        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());

        return dto;
    }

    ///////////////////////////oussemaaaaa methods here ////////////////////////////////
    
    //oussema new create reservation method for admin
    @Transactional
    @CacheEvict(value = "reservations", key = "'all'")
    public ReservationDisplayDTO createAdminReservation(ReservationRequestDTO requestDTO) {
        // Validate input: either studentUid or (username + phoneNumber) must be provided
        if (requestDTO.getStudentUid() == null && (requestDTO.getUsername() == null || requestDTO.getPhoneNumber() == null)) {
            throw new IllegalArgumentException("Either studentUid or both username and phoneNumber are required");
        }

        // Validate time constraints
        if (requestDTO.getEndTime().isBefore(requestDTO.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Validate PC availability or set defaults
        if (requestDTO.getType() == Reservation.ReservationType.PC_ONLY
                || requestDTO.getType() == Reservation.ReservationType.PC_WITH_COACHING) {
            if (requestDTO.getPcNumbers() == null || requestDTO.getPcNumbers().isEmpty()) {
                throw new IllegalArgumentException("PC numbers are required for PC_ONLY or PC_WITH_COACHING reservations.");
            }
            for (Integer pcNumber : requestDTO.getPcNumbers()) {
                if (pcNumber < 1 || pcNumber > 10) {
                    throw new IllegalArgumentException("PC number must be between 1 and 10");
                }
                if (!isPcAvailable(pcNumber, requestDTO.getStartTime(), requestDTO.getEndTime())) {
                    throw new IllegalArgumentException("PC " + pcNumber + " is not available for the selected time slot");
                }
            }
        } else if (requestDTO.getType() == Reservation.ReservationType.VIP_ROOM) {
            requestDTO.setPcNumbers(List.of(0));
        } else if (requestDTO.getType() == Reservation.ReservationType.COACHING) {
            if (requestDTO.getCoachId() == null || requestDTO.getCoachingGame() == null) {
                throw new IllegalArgumentException("Coach ID and coaching game are required for COACHING reservations.");
            }
        } else {
            throw new IllegalArgumentException("Unsupported reservation type: " + requestDTO.getType());
        }

        // Handle student data
        User student = null;
        if (requestDTO.getStudentUid() != null) {
            student = userRepository.findByUid(requestDTO.getStudentUid())
                    .orElseThrow(() -> new UserNotFoundException("Student not found with UID: " + requestDTO.getStudentUid()));
        }

        // Validate coach for COACHING or PC_WITH_COACHING
        User coach = null;
        if (requestDTO.getType() == Reservation.ReservationType.COACHING
                || requestDTO.getType() == Reservation.ReservationType.PC_WITH_COACHING) {
            coach = userRepository.findByUid(requestDTO.getCoachId())
                    .orElseThrow(() -> new UserNotFoundException("Coach not found with UID: " + requestDTO.getCoachId()));
        }

        // Calculate price
        BigDecimal totalPrice = calculatePrice(requestDTO.getType(), requestDTO.getPcNumbers(),
                requestDTO.getStartTime(), requestDTO.getEndTime());

        // Create payment
        Payment payment = new Payment();
        if (student != null) {
            payment.setUser(student);
        }
        payment.setPaymentType(Payment.PaymentType.PC_RESERVATION);
        payment.setAmount(totalPrice);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setTransactionId("OFFLINE_PENDING_" + UUID.randomUUID().toString().substring(0, 8));
        BigDecimal price = requestDTO.getPrice();
        if (price != null && price.compareTo(BigDecimal.ZERO) <= 0) {
            price = null; // treat 0 as null
        }
        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setType(requestDTO.getType());
        reservation.setStudent(student);
        reservation.setUsername(requestDTO.getUsername());
        reservation.setPhoneNumber(requestDTO.getPhoneNumber());
        reservation.setStartTime(requestDTO.getStartTime());
        reservation.setEndTime(requestDTO.getEndTime());
        reservation.setPcNumbers(requestDTO.getPcNumbers());
        reservation.setCoach(coach);
        reservation.setCoachingGame(requestDTO.getCoachingGame());
        reservation.setPrice(price);
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservation.setNotes(requestDTO.getNotes());
        // reservation.setPayment(payment);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        // Save reservation
        Reservation savedReservation = reservationRepository.save(reservation);

        // Update PC availability slots
        if (requestDTO.getPcNumbers() != null && !requestDTO.getPcNumbers().isEmpty()) {
            requestDTO.getPcNumbers().forEach(pcNumber -> {
                PCAvailabilitySlot pcSlot = new PCAvailabilitySlot();
                pcSlot.setPcNumber(pcNumber);
                pcSlot.setStartTime(requestDTO.getStartTime());
                pcSlot.setEndTime(requestDTO.getEndTime());
                pcSlot.setReservation(savedReservation);
                pcAvailabilitySlotRepository.save(pcSlot);
            });
        }
        System.out.println("PRICE SENT TO DB: " + reservation.getPrice());

        return mapToDisplayDTO(savedReservation);
    }
    // oussema New method to fetch reservations by studentId or username
    public List<ReservationDisplayDTO> getReservationsByStudentIdOrUsername(String studentId, String username) {
        List<Reservation> reservations;
        if (studentId != null && !studentId.isEmpty()) {
            User student = userRepository.findByUid(studentId)
                    .orElseThrow(() -> new UserNotFoundException("Student not found with UID: " + studentId));
            reservations = reservationRepository.findByStudent(student);
        } else if (username != null && !username.isEmpty()) {
            reservations = reservationRepository.findByUsername(username);
        } else {
            throw new IllegalArgumentException("Either studentId or username must be provided");
        }
        return reservations.stream()
                .map(this::mapToDisplayDTO)
                .collect(Collectors.toList());
    }


    @Transactional
   @CacheEvict(value = "reservations", key = "'all'")
    public ReservationDisplayDTO updateReservationStatus(String reservationId, Reservation.ReservationStatus newStatus) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + reservationId));

        // Validate transition rules (e.g., prevent changing from CANCELLED)
        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED && newStatus != Reservation.ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot change status of a cancelled reservation.");
        }

        // Update the status
        reservation.setStatus(newStatus);
        reservation.setUpdatedAt(LocalDateTime.now());
        Reservation updatedReservation = reservationRepository.save(reservation);

        return mapToDisplayDTO(updatedReservation);
    }

    @Transactional
    @CacheEvict(value = "reservations", key = "'all'")
    public void deleteReservationWithPcAvailability(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + reservationId));

        // Delete associated PC availability slots
        pcAvailabilitySlotRepository.deleteByReservationId(reservationId);

        // Delete the reservation
        reservationRepository.delete(reservation);
    }


    // New method to calculate reservation statistics
    public ReservationStatisticsDTO getReservationStatistics() {
        List<Reservation> reservations = reservationRepository.findAll();

        ReservationStatisticsDTO stats = new ReservationStatisticsDTO();

        // Total number of reservations
        stats.setTotalReservations(reservations.size());

        // Breakdown by reservation type
        Map<ReservationType, Long> typeCounts = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getType, Collectors.counting()));
        stats.setTypeBreakdown(typeCounts);

        return stats;
    }
    // Enhanced method to calculate reservation statistics
    public ReservationStatisticsDTO getReservationStatisticsPDF() {
        List<Reservation> reservations = reservationRepository.findAll();
        LocalDate currentYear = LocalDate.now().withDayOfYear(1); // Start of current year (2025-01-01)
        LocalDateTime startOfYear = currentYear.atStartOfDay();
        LocalDateTime endOfYear = currentYear.plusYears(1).minusDays(1).atTime(23, 59, 59);

        ReservationStatisticsDTO stats = new ReservationStatisticsDTO();

        // Total number of reservations
        stats.setTotalReservations(reservations.size());

        // Number of reservations by type
        Map<ReservationType, Long> typeCounts = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getType, Collectors.counting()));
        stats.setTypeBreakdown(typeCounts);

        // Number of reservations by month in the current year
        List<Reservation> currentYearReservations = reservationRepository.findByCreatedAtBetween(startOfYear, endOfYear);
        Map<Integer, Long> monthlyCounts = currentYearReservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().getMonthValue(),
                        Collectors.counting()
                ));
        stats.setMonthlyBreakdown(monthlyCounts);

        // Number of users with the most reservations
        Map<String, Long> userReservationCounts = reservations.stream()
                .filter(r -> r.getStudent() != null && r.getStudent().getId() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getStudent().getId(),
                        Collectors.counting()
                ));
        Map.Entry<String, Long> maxUserReservations = userReservationCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
        if (maxUserReservations != null) {
            stats.setTopUserId(maxUserReservations.getKey());
            stats.setTopUserReservationCount(maxUserReservations.getValue());
        }

        return stats;
    }
}