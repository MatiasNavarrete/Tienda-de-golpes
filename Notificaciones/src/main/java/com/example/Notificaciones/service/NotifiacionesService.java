package com.example.Notificaciones.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotifiacionesService {

    private final JavaMailSender mailSender;

    public NotifiacionesService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void enviarCorreo(String to, String subject, String body){
        SimpleMailMessage message= new
                SimpleMailMessage();
                            message.setTo(to);
                            message.setSubject(subject);
                            message.setText(body);
                            mailSender.send(message);
                            System.out.println("Correo enviado a:" + to);
    }
}
