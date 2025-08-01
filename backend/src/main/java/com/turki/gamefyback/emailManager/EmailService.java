package com.turki.gamefyback.emailManager;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    // @Autowired
    // private RedisTemplate <String,Object> redisTemplate;


    private final String emailSender = "no-reply@gamefy.tn";
    private final String AccountVerificationUrl = "http://217.182.93.224:8089/verifyAccount";
    private final String PasswordResetUrl = "http://217.182.93.224:8089/verify";
    private static final String EMAIL_QUEUE = "emailQueue";

    public void sendEmail(Email email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(emailSender);
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody(), true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // public void addToQueue(Email email){
    //     redisTemplate.opsForList().rightPush(EMAIL_QUEUE, email);
    // }
    public void sendAccountVerificationEmail(String email,String userName,String token){
        String verificationUrl = AccountVerificationUrl +"/"+ token;
        String htmlBody = "<h2>Hello dear " + userName + ",</h2>" +
                "<p>We have received a request of an account creation that contains your email. " +
                "If that were you, please click the button below to verify your account:</p>" +
                "<a href='" + verificationUrl + "' style='background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px;'>Verify Account</a>" +
                "<p style='margin-top: 30px;'>If you did not make this request, please ignore this email.</p>" +
                "<p>Thank you,<br>Workflow Manager</p>";
        Email emailToSend = new Email(email,emailSender,"Account verification",htmlBody);
        // addToQueue(emailToSend);
        sendEmail(emailToSend);
        System.out.println("done");
    }
    public void  sendPasswordResetEmail(String email,String userName,String token){
        String htmlBody = "<h2>Hello dear " + userName + ",</h2>" +
                "<p>We have received a request to reset the password for your account. " +
                "If you made this request, copy this code down below:</p>" +
                "<h1 style='color: #4CAF50;'>" + token + "</h1>" +
                "<p style='margin-top: 30px;'>If you did not request a password reset, please ignore this email. Your account will remain secure.</p>" +
                "<p>Thank you,<br>Workflow Manager</p>";

        Email emailToSend = new Email(email,emailSender,"Password Reset",htmlBody);
        // addToQueue(emailToSend);
        sendEmail(emailToSend);
    }
    public void sendEmailChangeConfirmation(String email, String userName, String confirmationToken) {
        String confirmationUrl = "http://localhost:5173/emailchange/" + confirmationToken;
        String htmlBody = "<h2>Hello " + userName + ",</h2>" +
                "<p>We received a request to change the email associated with your account.</p>" +
                "<p>If you made this request, please click the button below to confirm the change:</p>" +
                "<a href='" + confirmationUrl + "' style='background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px;'>Confirm Email Change</a>" +
                "<p>If you did not request this, you can ignore this email and no changes will be made.</p>" +
                "<p>Thank you,<br>Workflow Manager</p>";

        Email emailToSend = new Email(email, emailSender, "Confirm Email Change", htmlBody);
        // addToQueue(emailToSend);
        sendEmail(emailToSend);
    }

    public void sendPasswordChangeSuccessNotification(String email, String userName) {
        String htmlBody = "<h2>Hello " + userName + ",</h2>" +
                "<p>This is to inform you that your account password has been successfully changed.</p>" +
                "<p>If you did not perform this action, please contact our support immediately.</p>" +
                "<p>Thank you,<br>Workflow Manager</p>";

        Email emailToSend = new Email(email, emailSender, "Password Changed Successfully", htmlBody);
        // addToQueue(emailToSend);
        sendEmail(emailToSend);
    }

    public void send2faCode(String email, String userName, String code) {
        String htmlBody = "<h2>Hello " + userName + ",</h2>" +
                "<p>Your 2FA code is:</p>" +
                "<h1 style='color: #4CAF50;'>" + code + "</h1>" +
                "<p>This code is valid for 5 minutes.</p>" +
                "<p>Thank you,<br>Workflow Manager</p>";

        Email emailToSend = new Email(email, emailSender, "Your 2FA Code", htmlBody);
        // addToQueue(emailToSend);
        sendEmail(emailToSend);
    }


    public void sendadminAdded(String email,String password, String userName) {
        String connectUrl = "http://localhost:5173/WMAdmin/login";
        String htmlBody = "<h2>Hello " + userName + ",</h2>" +
                "<p>You have been added as an admin in workflow manager platform</p>" +
                "<p>Email:"+email+"</p>" +
                "<p>Password:"+password+"</p>"+
                "<a href='" + connectUrl + "' style='background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px;'>Login</a>" +
                "<p>Thank you,<br>Workflow Manager</p>";
        Email emailToSend = new Email(email, emailSender, "Password Changed Successfully", htmlBody);
        // addToQueue(emailToSend);
        sendEmail(emailToSend);
    }

    // public void processEmails() {
    //     for (int i = 0; i < 25; i++) {
    //         Email email = (Email) redisTemplate.opsForList().leftPop(EMAIL_QUEUE);
    //         if (email == null) {
    //             break;
    //         }
    //         sendEmail(email);
    //         System.out.println("sent");
    //     }
    // }
}
