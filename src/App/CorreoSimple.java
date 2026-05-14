/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

/**
 *
 * @author gaming
 */
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

public class CorreoSimple {

    public static void main(String[] args) {

        final String usuario = "notificaciones.sistemas811@gmail.com";
        final String password = "avvlcynnzafcafrm";

        Properties props = new Properties();

       props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(
                props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(usuario, password);
                    }
                });

        try {

            Message mensaje = new MimeMessage(session);

            mensaje.setFrom(new InternetAddress(usuario));

            mensaje.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("config.soporte811@gmail.com")
            );

            mensaje.setSubject("Prueba");

            mensaje.setText("Hola desde Java");

            Transport.send(mensaje);

            System.out.println("Correo enviado");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}