package com.example.Gestion_des_incidents.email;

public interface interfaceSendEmail {

    public String sendSimpleMessage(EmailBody email, String email_);

    public String sendEmailWithAttachment(EmailBody email);
}
