package App;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public final class CorreoSimple {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String USUARIO = "notificaciones.sistemas811@gmail.com";
    private static final String PASSWORD = "avvlcynnzafcafrm";
    private static final String DESTINATARIO_INTERNO = "config.soporte811@gmail.com";

    private CorreoSimple() {
    }

    public static void enviarCorreoInterno(String asunto, String cuerpo) throws MessagingException {
        Message mensaje = new MimeMessage(crearSesion());
        mensaje.setFrom(new InternetAddress(USUARIO));
        mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(DESTINATARIO_INTERNO));
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        Transport.send(mensaje);
    }

    private static Session crearSesion() {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USUARIO, PASSWORD);
            }
        });
    }
}
