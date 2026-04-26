package Metodos;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.swing.JOptionPane;

public class Configuracion {

    public static String ipEmpresa;
    public static String rutaEmpresa;
    public static String clave;
    public static String informacion;
    public static String impresora;
    public static String ambiente;
    public static String formatoPrecio;
    public static String reporte;
     
    private static String url = System.getProperty("user.dir") + "/configuracion.properties";
    public static String key = "Artemisa";

    public static void leerArchivoDePropiedades() {
        try {
            Properties propiedades = ConfigManager.loadProperties();
            ipEmpresa = propiedades.getProperty("ipEmpresa");
            rutaEmpresa = propiedades.getProperty("rutaEmpresa");
            clave = propiedades.getProperty("clave");
            informacion = propiedades.getProperty("informacion");
            impresora = propiedades.getProperty("impresora");
            ambiente = propiedades.getProperty("ambiente");
            formatoPrecio = propiedades.getProperty("formatoPrecio", "CO");
            reporte = ReporteManager.resolverReporteConfigurado(propiedades.getProperty("reporte", ""));

            System.out.println("IP EMPRESA: " + ipEmpresa);
            System.out.println("RUTA EMPRESA: " + rutaEmpresa);
            System.out.println("CLAVE: " + clave);
            System.out.println("INFORMACION: " + informacion);
            System.out.println("IMPRESORA: " + impresora);
            System.out.println("AMBIENTE: " + ambiente);//a=QA, b=PRODUCTIVO
            System.out.println("FORMATO PRECIO: " + formatoPrecio);
            System.out.println("REPORTE: " + reporte);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No se ha encontrado el archivo de configuración" + e, "FileNotFoundException", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "El archivo de configuración no puede ser leer" + e, "FileNotFoundException", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public static void guardarInformacionEnArchivo(String valor) {
        try {
            ConfigManager.saveInformation(valor);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar en el archivo: " + e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // Método para encriptar las fechas
    public static String encryptDates(Date fechaInicio, Date fechaFin, SecretKey secretKey) throws Exception {
        // Concatenar las fechas como una cadena separada por |
        String data = formatDate(fechaInicio) + "|" + formatDate(fechaFin);

        // Generar un IV aleatorio
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Crear el objeto Cipher para AES en modo CBC
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        // Encriptar la cadena de datos
        byte[] encryptedData = cipher.doFinal(data.getBytes());

        // Codificar en Base64 para que se pueda almacenar o transmitir fácilmente
        String encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedData);
        String ivBase64 = Base64.getEncoder().encodeToString(iv);

        // Retornar los datos encriptados junto con el IV (se deben guardar juntos)
        return ivBase64 + ":" + encryptedDataBase64;
    }

    // Método para desencriptar las fechas
    public static Date[] decryptDates(String encryptedData, SecretKey secretKey) throws Exception {
        // Separar el IV y los datos encriptados
        String[] parts = encryptedData.split(":");
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]);

        // Crear el objeto IvParameterSpec con el IV recibido
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Crear el objeto Cipher para AES en modo CBC
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        // Desencriptar los datos
        byte[] decryptedData = cipher.doFinal(encryptedBytes);
        String decryptedString = new String(decryptedData);

        // Separar las fechas por el delimitador "|"
        String[] dateStrings = decryptedString.split("\\|");

        // Convertir las fechas de texto a objetos Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fechaInicio = dateFormat.parse(dateStrings[0]);
        Date fechaFin = dateFormat.parse(dateStrings[1]);

        return new Date[]{fechaInicio, fechaFin};
    }

    // Método para crear una clave secreta (AES)
    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);  // Longitud de la clave (puede ser 128, 192 o 256 bits)
        return keyGenerator.generateKey();
    }

    // Método para formatear las fechas como cadenas
    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    // Método para generar una clave secreta fija a partir de una cadena (por ejemplo, "miClaveSecreta")
    public static SecretKey generateFixedSecretKey(String clave) throws NoSuchAlgorithmException {
        // Usamos SHA-256 para generar una clave de 256 bits a partir de la cadena
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] claveBytes = digest.digest(clave.getBytes());

        // Usar los primeros 256 bits (32 bytes) para crear la clave secreta
        return new SecretKeySpec(claveBytes, "AES");
    }

    // Método principal de prueba
    public static void main(String[] args) {
        try {

            // Generar una clave fija a partir de una cadena
            SecretKey secretKey = generateFixedSecretKey(key);
            //Artemisa
            // Imprimir la clave en formato hexadecimal (solo para visualización)
            byte[] claveBytes = secretKey.getEncoded();
            StringBuilder sb = new StringBuilder();
            for (byte b : claveBytes) {
                sb.append(String.format("%02x", b));
            }

            System.out.println("Clave secreta fija (en formato hexadecimal): " + sb.toString());

            // Fechas de inicio y fin de ejemplo
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fechaInicio = dateFormat.parse("2025-04-01 00:00:00");
            Date fechaFin = dateFormat.parse("2025-12-31 23:59:59");
            Date fechaHoy = new Date();
            dateFormat.format(fechaHoy);

            // Encriptar las fechas
            String encryptedData = encryptDates(fechaInicio, fechaFin, secretKey);
            System.out.println("Datos encriptados: " + encryptedData);

            // Desencriptar las fechas
            Date[] decryptedDates = decryptDates(encryptedData, secretKey);
            System.out.println("Fecha de inicio desencriptada: " + dateFormat.format(decryptedDates[0]));
            System.out.println("Fecha de fin desencriptada: " + dateFormat.format(decryptedDates[1]));
            System.out.println("" + dateFormat.format(fechaHoy));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
