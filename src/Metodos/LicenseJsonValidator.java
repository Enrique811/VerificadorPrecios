package Metodos;

import static SQL.SQLFechaHora.obtenerFechayHoraActualDelServidorDate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LicenseJsonValidator {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_ONLY_FORMAT = "yyyy-MM-dd";
    public static final String ACTIVADO_POR_DISTRIBUIDOR = "DISTRIBUIDOR COLOMBIA";
    public static final String ACTIVADO_POR_DESARROLLADOR = "DESARROLLADOR";
    private static final String EMBEDDED_PUBLIC_KEY_BASE64 //este es del cliente
            = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzl0ie2IjH6sOfJXZquujuncM"
            + "aXfcLbzUDyWNN4dwvZO4PwTsEUl+PYEuT9Iv2BWNHXCZviyfDQjWIZcy4rqfBecFUOSF"
            + "iDfUhaCLk61y/Gx3uqfCGUHnxrCGYNBu5uWwGXLEt1nyOZAfBv4Z1JCCZ03YebZeZkwL"
            + "kYbL517dpKgbxuMHPtdl8p9GmXT8ljUZhao5WojmafxHQBz3jz+59LAUKUhdZb3hwiC+"
            + "526lU0JiBm48yB28dtL6dh3ixm9TOetN0Cwm+nDS0jMtqX+qvhYv/E+Ztz1n5Q++v9Zf"
            + "OTQMmlKI2TVZ1R+pL8kQOvuw/GsdZONcXscfMF7ENVRkAQIDAQAB";
    private static final String EMBEDDED_PUBLIC_KEY_BASE64_DEV //este el mio
            = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlN7pBkLh3rZiY8yPZ/xtcHAaD5eS93SZOMzdTnFAsmxDZ6Y8VIOXopY44Wg6h/CUjVRCFx38ZpqJ8DYqYPi+AdOzEyRwHSjWsPcDJ+xr+1Zt9y3t3eFeSatHpx9C3eIy3AwXJuC56oX1EqBNpNltG+lFFZHjCr+h3lp5iazGbfgii8gtdVC2z5pWGI6/agsmDEngY5vGl2N29lRkusG7sJkBdPEd2ReNnIy7oRZtq9w6RinpQxrLeToN8TGe5B5A/TfRyfInRT0ZY7RNlVvXCVT6J7R/9UdMgHK+gIzsReohV3OMZG+fsjfw7Rx3RxZT3mps+lbcFwvBs6xQteRHWQIDAQAB";
    private static final Pattern JSON_FIELD_PATTERN = Pattern.compile("\"(uuid|inicio|fin|firma)\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");

    private LicenseJsonValidator() {
    }

    public static LicenseValidationResult validate(String licenseInput) {
        if (licenseInput == null || licenseInput.trim().isEmpty()) {
            return invalidResult(null, null, null, null, null, null, false, false, false, false,
                    false, false, null, "No se configuro la licencia.");
        }

        try {
            LicenseSource source = resolveLicenseSource(licenseInput);
            LicensePayload payload = parseLicense(source.content);
            ActivationType activationType = detectActivationType(payload);
            boolean firmaValida = activationType != null;
            String uuidLocal = obtenerUuidLocalWindows();
            boolean uuidValido = uuidLocal != null
                    && !uuidLocal.isEmpty()
                    && payload.uuid.equals(normalizeUuid(uuidLocal));
            Date fechaServidor = obtenerFechayHoraActualDelServidorDate();
            VigenciaEstado vigencia = evaluarVigencia(payload.fechaInicio, payload.fechaFin, fechaServidor);

            String mensajeError = null;
            if (!firmaValida) {
                mensajeError = "La firma RSA no coincide con el contenido de la licencia.";
            } else if (!uuidValido) {
                mensajeError = "El UUID de la licencia no coincide con el UUID local del equipo.";
            } else if (!vigencia.vigente) {
                mensajeError = vigencia.noIniciada
                        ? "La licencia aun no inicia segun la fecha del servidor Firebird."
                        : "La licencia esta vencida o no se pudo obtener la fecha del servidor Firebird.";
            }

            return new LicenseValidationResult(
                    source.description,
                    payload.uuid,
                    uuidLocal,
                    payload.fechaInicio,
                    payload.fechaFin,
                    fechaServidor,
                    firmaValida,
                    uuidValido,
                    vigencia.vigente,
                    vigencia.noIniciada,
                    true,
                    true,
                    activationType == null ? null : activationType.activadoPor,
                    mensajeError);
        } catch (LicenseValidationException ex) {
            return invalidResult(describeLicenseInput(licenseInput), null, null, null, null, null, false,
                    false, false, false, true, false, null, ex.getMessage());
        } catch (Exception ex) {
            return invalidResult(describeLicenseInput(licenseInput), null, null, null, null, null, false,
                    false, false, false, true, false, null,
                    "No fue posible validar la licencia: " + ex.getMessage());
        }
    }

    public static String detectActivationType(String licenseInput) {
        LicensePayload payload = parsePayloadQuietly(licenseInput);
        if (payload == null) {
            return null;
        }

        try {
            ActivationType activationType = detectActivationType(payload);
            return activationType == null ? null : activationType.activadoPor;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Date[] extractDates(String licenseInput) {
        LicensePayload payload = parsePayloadQuietly(licenseInput);
        if (payload == null) {
            return null;
        }
        return new Date[]{new Date(payload.fechaInicio.getTime()), new Date(payload.fechaFin.getTime())};
    }

    public static String extractUuid(String licenseInput) {
        LicensePayload payload = parsePayloadQuietly(licenseInput);
        if (payload == null) {
            return null;
        }
        return payload.uuid;
    }

    public static String readLicenseContentFromFile(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            return null;
        }
        File licenseFile = resolveFile(filePath.trim());
        if (!licenseFile.isFile()) {
            return null;
        }
        return readFile(licenseFile).trim();
    }

    private static LicenseValidationResult invalidResult(String licensePath, String uuidLicencia,
            String uuidLocal, Date fechaInicio, Date fechaFin, Date fechaServidor, boolean firmaValida,
            boolean uuidValido, boolean vigente, boolean noIniciada, boolean archivoLeido,
            boolean jsonValido, String activadoPor, String mensajeError) {
        return new LicenseValidationResult(licensePath, uuidLicencia, uuidLocal, fechaInicio, fechaFin,
                fechaServidor, firmaValida, uuidValido, vigente, noIniciada, archivoLeido,
                jsonValido, activadoPor, mensajeError);
    }

    private static String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append('\n');
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return content.toString();
    }

    private static LicensePayload parsePayloadQuietly(String licenseInput) {
        try {
            LicenseSource source = resolveLicenseSource(licenseInput);
            return parseLicense(source.content);
        } catch (Exception ex) {
            return null;
        }
    }

    private static LicensePayload parseLicense(String json) throws LicenseValidationException {
        String uuid = null;
        String inicio = null;
        String fin = null;
        String firma = null;

        Matcher matcher = JSON_FIELD_PATTERN.matcher(json);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = unescapeJson(matcher.group(2));
            if ("uuid".equals(key)) {
                uuid = value;
            } else if ("inicio".equals(key)) {
                inicio = value;
            } else if ("fin".equals(key)) {
                fin = value;
            } else if ("firma".equals(key)) {
                firma = value;
            }
        }

        if (isBlank(uuid) || isBlank(inicio) || isBlank(fin) || isBlank(firma)) {
            throw new LicenseValidationException("El JSON de licencia no contiene todos los campos requeridos.");
        }

        return new LicensePayload(
                normalizeUuid(uuid),
                parseDate(inicio.trim(), "inicio"),
                parseDate(fin.trim(), "fin"),
                canonicalizeStartDateText(inicio.trim()),
                canonicalizeEndDateText(fin.trim()),
                firma.trim());
    }

    private static PublicKey readPublicKey(String keyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private static boolean verifySignature(LicensePayload payload, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(buildSignedPayload(payload).getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.getDecoder().decode(payload.firma));
    }

    private static ActivationType detectActivationType(LicensePayload payload) throws Exception {
        for (ActivationType candidate : ActivationType.values()) {
            if (verifySignature(payload, readPublicKey(candidate.publicKeyBase64))) {
                return candidate;
            }
        }
        return null;
    }

    private static String buildSignedPayload(LicensePayload payload) {
        return "uuid=" + payload.uuid + "\n"
                + "inicio=" + payload.inicioTexto + "\n"
                + "fin=" + payload.finTexto;
    }

    private static String canonicalizeStartDateText(String value) throws LicenseValidationException {
        return formatBoundaryDate(parseDate(value, "inicio"), "00:00:00");
    }

    private static String canonicalizeEndDateText(String value) throws LicenseValidationException {
        return formatBoundaryDate(parseDate(value, "fin"), "23:59:59");
    }

    private static VigenciaEstado evaluarVigencia(Date fechaInicio, Date fechaFin, Date fechaServidor) {
        if (fechaInicio == null || fechaFin == null || fechaServidor == null) {
            return new VigenciaEstado(false, false);
        }

        if (fechaServidor.before(fechaInicio)) {
            return new VigenciaEstado(false, true);
        }

        boolean vigente = !fechaServidor.after(fechaFin);
        return new VigenciaEstado(vigente, false);
    }

    private static Date parseDate(String value, String fieldName) throws LicenseValidationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setLenient(false);
        try {
            return dateFormat.parse(value);
        } catch (ParseException ex) {
            throw new LicenseValidationException("El campo " + fieldName + " no tiene el formato " + DATE_FORMAT + ".");
        }
    }

    private static String obtenerUuidLocalWindows() throws LicenseValidationException {
        for (String[] command : buildUuidCommands()) {
            String uuid = ejecutarComandoUuid(command);
            if (isValidUuid(uuid)) {
                return uuid;
            }
        }

        throw new LicenseValidationException("No fue posible obtener un UUID local valido en Windows.");
    }

    private static String[][] buildUuidCommands() {
        String windowsDir = resolveWindowsDirectory();
        String system32PowerShell = windowsDir + "\\System32\\WindowsPowerShell\\v1.0\\powershell.exe";
        String sysnativePowerShell = windowsDir + "\\Sysnative\\WindowsPowerShell\\v1.0\\powershell.exe";
        String system32Wmic = windowsDir + "\\System32\\wbem\\WMIC.exe";
        String sysnativeWmic = windowsDir + "\\Sysnative\\wbem\\WMIC.exe";

        return new String[][]{
            new String[]{system32PowerShell, "-NoProfile", "-Command", "(Get-CimInstance Win32_ComputerSystemProduct).UUID"},
            new String[]{sysnativePowerShell, "-NoProfile", "-Command", "(Get-CimInstance Win32_ComputerSystemProduct).UUID"},
            new String[]{system32Wmic, "csproduct", "get", "uuid"},
            new String[]{sysnativeWmic, "csproduct", "get", "uuid"}
        };
    }

    private static String resolveWindowsDirectory() {
        String windowsDir = System.getenv("WINDIR");
        if (windowsDir == null || windowsDir.trim().isEmpty()) {
            return "C:\\Windows";
        }
        return windowsDir.trim();
    }

    private static String ejecutarComandoUuid(String[] command) {
        Process process = null;
        try {
            process = new ProcessBuilder(command).redirectErrorStream(true).start();
            String output = readStream(process.getInputStream());
            process.waitFor();
            return normalizeUuidOutput(output);
        } catch (Exception ex) {
            return null;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private static String readStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return output.toString();
    }

    private static String normalizeUuidOutput(String output) {
        if (output == null) {
            return null;
        }

        String[] lines = output.split("\\R");
        for (String line : lines) {
            String normalized = line == null ? "" : line.trim();
            if (normalized.isEmpty()) {
                continue;
            }
            if ("uuid".equalsIgnoreCase(normalized)) {
                continue;
            }
            if (normalized.startsWith("UUID")) {
                normalized = normalized.substring(4).trim();
            }
            if (isValidUuid(normalized)) {
                return normalized.toUpperCase();
            }
        }
        return null;
    }

    private static boolean isValidUuid(String value) {
        return value != null
                && value.matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    private static String normalizeUuid(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toUpperCase();
    }

    private static String formatBoundaryDate(Date date, String timeSuffix) {
        return new SimpleDateFormat(DATE_ONLY_FORMAT).format(date) + " " + timeSuffix;
    }

    private static String unescapeJson(String value) {
        return value
                .replace("\\\\", "\\")
                .replace("\\\"", "\"")
                .replace("\\/", "/")
                .replace("\\b", "\b")
                .replace("\\f", "\f")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static LicenseSource resolveLicenseSource(String licenseInput) throws IOException, LicenseValidationException {
        String trimmed = licenseInput == null ? "" : licenseInput.trim();
        if (trimmed.startsWith("{")) {
            return new LicenseSource(trimmed, "configuracion.properties");
        }

        File file = resolveFile(trimmed);
        if (!file.isFile()) {
            throw new LicenseValidationException("No se encontro el archivo JSON de licencia.");
        }
        return new LicenseSource(readFile(file), file.getAbsolutePath());
    }

    private static String describeLicenseInput(String licenseInput) {
        if (licenseInput == null || licenseInput.trim().isEmpty()) {
            return null;
        }
        String trimmed = licenseInput.trim();
        return trimmed.startsWith("{") ? "configuracion.properties" : resolveFile(trimmed).getAbsolutePath();
    }

    private static File resolveFile(String path) {
        File file = new File(path);
        if (file.isAbsolute()) {
            return file;
        }
        return new File(System.getProperty("user.dir"), path);
    }

    private static final class LicenseSource {

        private final String content;
        private final String description;

        private LicenseSource(String content, String description) {
            this.content = content;
            this.description = description;
        }
    }

    private static final class LicensePayload {

        private final String uuid;
        private final Date fechaInicio;
        private final Date fechaFin;
        private final String inicioTexto;
        private final String finTexto;
        private final String firma;

        private LicensePayload(String uuid, Date fechaInicio, Date fechaFin,
                String inicioTexto, String finTexto, String firma) {
            this.uuid = uuid;
            this.fechaInicio = new Date(fechaInicio.getTime());
            this.fechaFin = new Date(fechaFin.getTime());
            this.inicioTexto = inicioTexto;
            this.finTexto = finTexto;
            this.firma = firma;
        }
    }

    private static final class VigenciaEstado {

        private final boolean vigente;
        private final boolean noIniciada;

        private VigenciaEstado(boolean vigente, boolean noIniciada) {
            this.vigente = vigente;
            this.noIniciada = noIniciada;
        }
    }

    private enum ActivationType {
        DISTRIBUIDOR_COLOMBIA(EMBEDDED_PUBLIC_KEY_BASE64, ACTIVADO_POR_DISTRIBUIDOR),
        DESARROLLADOR(EMBEDDED_PUBLIC_KEY_BASE64_DEV, ACTIVADO_POR_DESARROLLADOR);

        private final String publicKeyBase64;
        private final String activadoPor;

        private ActivationType(String publicKeyBase64, String activadoPor) {
            this.publicKeyBase64 = publicKeyBase64;
            this.activadoPor = activadoPor;
        }
    }

    private static final class LicenseValidationException extends Exception {

        private LicenseValidationException(String message) {
            super(message);
        }
    }
}
