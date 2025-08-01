package com.turki.gamefyback.emailManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailScheduler {

    @Autowired
    private EmailService emailService;

    // @Scheduled(fixedRate = 1000)
    // public void run() {
    //     emailService.processEmails();
    // }
}