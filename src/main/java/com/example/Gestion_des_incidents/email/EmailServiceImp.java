package com.example.Gestion_des_incidents.email;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.FileSystemResource;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImp implements interfaceSendEmail{

    @Autowired
    JavaMailSender javaMailSender;

    // @Value("${spring.mail.username}")
    private String sender = "kh.soufiene09@gmail.com";

    @Override
    public String sendSimpleMessage(EmailBody details, String email) {

        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom("kh.soufiene09@gmail.com");
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMessage());
            mailMessage.setSubject(details.getBody());

            // Sending the mail
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            e.printStackTrace();
            return "Error while Sending Mail";
        }
    }

    // Method 2
    // To send an email with attachment
    @Override
    public String sendEmailWithAttachment(EmailBody details) {
        // Creating a mime message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            // Setting multipart as true for attachments to
            // be send
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMessage());
            mimeMessageHelper.setSubject(
                    details.getBody());

            // Adding the attachment
            FileSystemResource file = new FileSystemResource(
                    new File(details.getAttachement()));

            mimeMessageHelper.addAttachment(
                    file.getFilename(), file);

            // Sending the mail
            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        }

        // Catch block to handle MessagingException
        catch (MessagingException e) {

            // Display message when exception occurred
            return "Error while sending mail!!!";
        }
    }

}
