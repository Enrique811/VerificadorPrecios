package Metodos;

import App.CorreoSimple;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.mail.MessagingException;

public final class ActivationEmailService {

    private static final String ASUNTO_ACTIVACION = "Activacion de sistema";
    private static final String ASUNTO_REACTIVACION = "Reactivacion de sistema";
    private static final String VALOR_NO_DISPONIBLE = "-";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private ActivationEmailService() {
    }

    public static void enviarCorreoActivacion(LicenseValidationResult validationResult,
            String archivoLicencia, ActivationEvent activationEvent) throws ActivationEmailException {
        if (activationEvent == null || activationEvent == ActivationEvent.SIN_ENVIO) {
            return;
        }

        if (validationResult == null || !validationResult.isLicenciaValida()) {
            throw new ActivationEmailException("La licencia es invalida y no se puede completar el proceso de activacion.");
        }

        String activadoPor = valorVisible(validationResult.getActivadoPor());
        if (VALOR_NO_DISPONIBLE.equals(activadoPor)) {
            throw new ActivationEmailException("No fue posible detectar la llave publica utilizada para la activacion.");
        }

        try {
            CorreoSimple.enviarCorreoInterno(activationEvent.getAsunto(),
                    construirCuerpo(validationResult, archivoLicencia, activadoPor, activationEvent));
        } catch (MessagingException ex) {
            throw new ActivationEmailException("No fue posible enviar el correo interno de activacion.", ex);
        } catch (Exception ex) {
            throw new ActivationEmailException("Ocurrio un error general al generar el correo interno de activacion.", ex);
        }
    }

    private static String construirCuerpo(LicenseValidationResult validationResult,
            String archivoLicencia, String activadoPor, ActivationEvent activationEvent) {
        return "Hola..\r\n\r\n"
                + activationEvent.getMensajePrincipal() + "\r\n\r\n"
                + "UUID del equipo:\r\n"
                + valorVisible(validationResult.getUuidLocal()) + "\r\n\r\n"
                + "Activado por:\r\n"
                + activadoPor + "\r\n\r\n"
                + "Fecha y hora:\r\n"
                + formatearFecha(new Date()) + "\r\n\r\n"
                + "Datos de licencia:\r\n"
                + "- Archivo importado: " + valorVisible(archivoLicencia) + "\r\n"
                + "- UUID licencia: " + valorVisible(validationResult.getUuidLicencia()) + "\r\n"
                + "- Vigencia desde: " + formatearFecha(validationResult.getFechaInicio()) + "\r\n"
                + "- Vigencia hasta: " + formatearFecha(validationResult.getFechaFin()) + "\r\n\r\n"
                + "Gracias.";
    }

    private static String formatearFecha(Date fecha) {
        if (fecha == null) {
            return VALOR_NO_DISPONIBLE;
        }
        return new SimpleDateFormat(DATE_FORMAT).format(fecha);
    }

    private static String valorVisible(String valor) {
        return valor == null || valor.trim().isEmpty() ? VALOR_NO_DISPONIBLE : valor.trim();
    }

    public enum ActivationEvent {
        ACTIVACION_INICIAL(ASUNTO_ACTIVACION, "Se ha activado el sistema correctamente."),
        REACTIVACION(ASUNTO_REACTIVACION, "Se ha reactivado el sistema correctamente."),
        SIN_ENVIO("", "");

        private final String asunto;
        private final String mensajePrincipal;

        private ActivationEvent(String asunto, String mensajePrincipal) {
            this.asunto = asunto;
            this.mensajePrincipal = mensajePrincipal;
        }

        public String getAsunto() {
            return asunto;
        }

        public String getMensajePrincipal() {
            return mensajePrincipal;
        }
    }

    public static final class ActivationEmailException extends Exception {

        public ActivationEmailException(String message) {
            super(message);
        }

        public ActivationEmailException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
