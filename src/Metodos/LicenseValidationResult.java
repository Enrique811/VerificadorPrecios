package Metodos;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class LicenseValidationResult {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final String licensePath;
    private final String uuidLicencia;
    private final String uuidLocal;
    private final Date fechaInicio;
    private final Date fechaFin;
    private final Date fechaServidor;
    private final boolean firmaValida;
    private final boolean uuidValido;
    private final boolean vigente;
    private final boolean noIniciada;
    private final boolean archivoLeido;
    private final boolean jsonValido;
    private final String activadoPor;
    private final String mensajeError;

    public LicenseValidationResult(String licensePath, String uuidLicencia, String uuidLocal,
            Date fechaInicio, Date fechaFin, Date fechaServidor, boolean firmaValida,
            boolean uuidValido, boolean vigente, boolean noIniciada, boolean archivoLeido,
            boolean jsonValido, String activadoPor, String mensajeError) {
        this.licensePath = licensePath;
        this.uuidLicencia = uuidLicencia;
        this.uuidLocal = uuidLocal;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaServidor = fechaServidor;
        this.firmaValida = firmaValida;
        this.uuidValido = uuidValido;
        this.vigente = vigente;
        this.noIniciada = noIniciada;
        this.archivoLeido = archivoLeido;
        this.jsonValido = jsonValido;
        this.activadoPor = activadoPor;
        this.mensajeError = mensajeError;
    }

    public String getLicensePath() {
        return licensePath;
    }

    public String getUuidLicencia() {
        return uuidLicencia;
    }

    public String getUuidLocal() {
        return uuidLocal;
    }

    public Date getFechaInicio() {
        return fechaInicio == null ? null : new Date(fechaInicio.getTime());
    }

    public Date getFechaFin() {
        return fechaFin == null ? null : new Date(fechaFin.getTime());
    }

    public Date getFechaServidor() {
        return fechaServidor == null ? null : new Date(fechaServidor.getTime());
    }

    public boolean isFirmaValida() {
        return firmaValida;
    }

    public boolean isUuidValido() {
        return uuidValido;
    }

    public boolean isVigente() {
        return vigente;
    }

    public boolean isNoIniciada() {
        return noIniciada;
    }

    public boolean isArchivoLeido() {
        return archivoLeido;
    }

    public boolean isJsonValido() {
        return jsonValido;
    }

    public String getActivadoPor() {
        return activadoPor;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public boolean isLicenciaValida() {
        return archivoLeido && jsonValido && firmaValida && uuidValido && vigente;
    }

    public String getEstadoVigencia() {
        if (fechaInicio == null || fechaFin == null || fechaServidor == null) {
            return "desconocida";
        }
        if (noIniciada) {
            return "no iniciada";
        }
        if (vigente) {
            return "vigente";
        }
        return "vencida";
    }

    public String buildSummary() {
        StringBuilder builder = new StringBuilder();
        builder.append("Archivo licencia: ").append(valorVisible(licensePath)).append('\n');
        builder.append("Firma: ").append(firmaValida ? "valida" : "invalida").append('\n');
        builder.append("UUID licencia: ").append(valorVisible(uuidLicencia)).append('\n');
        builder.append("UUID local: ").append(valorVisible(uuidLocal)).append('\n');
        builder.append("UUID: ").append(uuidValido ? "valido" : "invalido").append('\n');
        builder.append("Inicio: ").append(formatearFecha(fechaInicio)).append('\n');
        builder.append("Fin: ").append(formatearFecha(fechaFin)).append('\n');
        builder.append("Fecha servidor: ").append(formatearFecha(fechaServidor)).append('\n');
        builder.append("Vigencia: ").append(getEstadoVigencia()).append('\n');
        builder.append("Resultado final: ").append(isLicenciaValida() ? "licencia valida" : "licencia no valida");
        if (mensajeError != null && !mensajeError.trim().isEmpty()) {
            builder.append('\n').append("Detalle: ").append(mensajeError.trim());
        }
        return builder.toString();
    }

    private String formatearFecha(Date fecha) {
        if (fecha == null) {
            return "-";
        }
        return new SimpleDateFormat(DATE_FORMAT).format(fecha);
    }

    private String valorVisible(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor.trim();
    }
}
