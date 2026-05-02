package Ventanas;

import App.Main;
import Conexion.Conexion;
import aplicacion.ConfigService;
import dominio.ConfiguracionApp;
import infraestructura.LegacyConfigRepository;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.SwingWorker;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class VentanaConfiguracion extends JFrame {

    private static final Color COLOR_FONDO = new Color(243, 244, 246);
    private static final Color COLOR_TARJETA = Color.WHITE;
    private static final Color COLOR_BORDE = new Color(220, 226, 232);
    private static final Color COLOR_TITULO = new Color(31, 41, 55);
    private static final Color COLOR_TEXTO = new Color(75, 85, 99);
    private static final Color COLOR_VERDE = new Color(22, 163, 74);
    private static final Color COLOR_GRIS = new Color(229, 231, 235);
    private static final Color COLOR_AZUL_SUAVE = new Color(232, 240, 254);
    private static final Dimension DIALOG_SIZE = new Dimension(740, 420);
    private static final Dimension MAX_DIALOG_SIZE = new Dimension(860, 480);

    private JTextField campoIpEmpresa;
    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private JTextField campoRutaEmpresa;
    private JTextField campoUuidEquipo;
    private JButton botonRutaEmpresa;
    private JButton botonCopiarUuid;
    private JButton botonGuardar;
    private final ConfigService configService = new ConfigService(new LegacyConfigRepository());

    public VentanaConfiguracion() {
        initComponents();
        cargarConfiguracionActual();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("VentanaConfiguracion");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        RoundedPanel root = new RoundedPanel(28, COLOR_FONDO);
        root.setLayout(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        root.add(crearHeader(), BorderLayout.NORTH);
        root.add(crearFormulario(), BorderLayout.CENTER);
        root.add(crearFooter(), BorderLayout.SOUTH);

        setContentPane(root);
        setPreferredSize(calcularTamanoDialogo());
        setMaximumSize(calcularTamanoDialogo());
        pack();
    }

    private Dimension calcularTamanoDialogo() {
        Dimension size = new Dimension(DIALOG_SIZE);
        size.width = Math.min(size.width, MAX_DIALOG_SIZE.width);
        size.height = Math.min(size.height, MAX_DIALOG_SIZE.height);
        return size;
    }

    private JPanel crearHeader() {
        RoundedPanel header = crearTarjeta();
        header.setLayout(new BorderLayout());

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Configuraci\u00f3n de conexi\u00f3n");
        titulo.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 24));
        titulo.setForeground(COLOR_TITULO);

        JLabel subtitulo = new JLabel("<html>Selecciona el archivo .fdb y completa los datos de acceso para iniciar.</html>");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(COLOR_TEXTO);

        textos.add(titulo);
        textos.add(javax.swing.Box.createVerticalStrut(4));
        textos.add(subtitulo);

        header.add(textos, BorderLayout.CENTER);
        return header;
    }

    private JPanel crearFormulario() {
        RoundedPanel tarjeta = crearTarjeta();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        JLabel etiquetaRuta = crearEtiquetaCampo("Base de datos");
        JLabel etiquetaIp = crearEtiquetaCampo("IP");
        JLabel etiquetaUsuario = crearEtiquetaCampo("Usuario");
        JLabel etiquetaPassword = crearEtiquetaCampo("Password");
        JLabel etiquetaUuid = crearEtiquetaCampo("UUID del equipo");

        panel.add(etiquetaRuta, gbc);
        gbc.gridy++;
        panel.add(etiquetaIp, gbc);
        gbc.gridy++;
        panel.add(etiquetaUsuario, gbc);
        gbc.gridy++;
        panel.add(etiquetaPassword, gbc);
        gbc.gridy++;
        panel.add(etiquetaUuid, gbc);

        campoIpEmpresa = new JTextField(28);
        campoUsuario = new JTextField(28);
        campoPassword = new JPasswordField(28);
        campoRutaEmpresa = new JTextField(28);
        campoUuidEquipo = new JTextField(28);
        campoUuidEquipo.setEditable(false);
        campoUuidEquipo.setBackground(Color.WHITE);
        botonCopiarUuid = new JButton("Copiar");
        botonCopiarUuid.addActionListener(e -> copiarUuidEquipo());
        botonRutaEmpresa = new JButton("Seleccionar");
        botonRutaEmpresa.addActionListener(e -> seleccionarRutaEmpresa());

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        JPanel contenedorRuta = new JPanel(new BorderLayout(8, 0));
        contenedorRuta.add(campoRutaEmpresa, BorderLayout.CENTER);
        contenedorRuta.add(botonRutaEmpresa, BorderLayout.EAST);
        panel.add(contenedorRuta, gbc);
        gbc.gridy++;
        panel.add(campoIpEmpresa, gbc);
        gbc.gridy++;
        panel.add(campoUsuario, gbc);
        gbc.gridy++;
        panel.add(campoPassword, gbc);
        gbc.gridy++;
        JPanel contenedorUuid = new JPanel(new BorderLayout(8, 0));
        contenedorUuid.add(campoUuidEquipo, BorderLayout.CENTER);
        contenedorUuid.add(botonCopiarUuid, BorderLayout.EAST);
        panel.add(contenedorUuid, gbc);

        tarjeta.add(panel);
        return tarjeta;
    }

    private JPanel crearFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        JButton botonCancelar = crearBoton("Cancelar", COLOR_GRIS, COLOR_TITULO);
        botonCancelar.addActionListener(e -> dispose());

        botonGuardar = crearBoton("Guardar", new Color(220, 252, 231), COLOR_VERDE);
        botonGuardar.addActionListener(e -> guardarConfiguracion());

        panel.add(botonCancelar);
        panel.add(botonGuardar);
        return panel;
    }

    private void cargarConfiguracionActual() {
        ConfiguracionApp config = configService.cargarConfiguracion();
        campoIpEmpresa.setText(config.getIpEmpresa());
        campoUsuario.setText(config.getUsuario());
        campoPassword.setText(config.getPassword());
        campoRutaEmpresa.setText(config.getRutaEmpresa());
        campoUuidEquipo.setText(obtenerUuidEquipoLocal());
    }

    private void guardarConfiguracion() {
        String ipEmpresa = campoIpEmpresa.getText().trim();
        String usuario = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword()).trim();
        String rutaEmpresa = campoRutaEmpresa.getText().trim();

        if (ipEmpresa.isEmpty() || usuario.isEmpty() || password.isEmpty() || rutaEmpresa.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Configuracion",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        setFormularioHabilitado(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String saveErrorMessage;

            @Override
            protected Boolean doInBackground() {
                try {
                    configService.guardarConexion(ipEmpresa, usuario, password, rutaEmpresa);
                } catch (IOException ex) {
                    saveErrorMessage = ex.getMessage();
                    return null;
                }

                return Boolean.valueOf(Conexion.probarConexion(new Metodos.ConfigManager()));
            }

            @Override
            protected void done() {
                setFormularioHabilitado(true);
                try {
                    Boolean conexionExitosa = get();
                    if (conexionExitosa == null) {
                        JOptionPane.showMessageDialog(VentanaConfiguracion.this,
                                "No se pudo guardar la configuracion.\n" + saveErrorMessage,
                                "Configuracion",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (conexionExitosa.booleanValue()) {
                        JOptionPane.showMessageDialog(VentanaConfiguracion.this,
                                "Conexion exitosa. Se abrira la ventana principal.",
                                "Configuracion",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        Main.abrirVentanaInicio();
                        return;
                    }
                } catch (Exception ex) {
                }

                JOptionPane.showMessageDialog(VentanaConfiguracion.this,
                        "La configuracion se guardo, pero la conexion fallo.\n"
                        + Conexion.getUltimoErrorConexion()
                        + "\nVerifique los datos e intente nuevamente.",
                        "Configuracion",
                        JOptionPane.ERROR_MESSAGE);
            }
        };
        worker.execute();
    }

    private void seleccionarRutaEmpresa() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar archivo .fdb");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Archivo Firebird (*.fdb)", "fdb"));

        String rutaActual = campoRutaEmpresa.getText().trim();
        if (!rutaActual.isEmpty()) {
            File actual = new File(rutaActual);
            if (actual.exists()) {
                chooser.setCurrentDirectory(actual.isDirectory() ? actual : actual.getParentFile());
                chooser.setSelectedFile(actual);
            }
        }

        int resultado = chooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
            campoRutaEmpresa.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void copiarUuidEquipo() {
        String uuid = campoUuidEquipo.getText().trim();
        if (uuid.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo obtener el UUID del equipo.",
                    "UUID",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringSelection seleccion = new StringSelection(uuid);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(seleccion, null);
        JOptionPane.showMessageDialog(this,
                "UUID copiado al portapapeles.",
                "UUID",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String obtenerUuidEquipoLocal() {
        String uuid = ejecutarComandoUuid(new String[]{"wmic", "csproduct", "get", "uuid"});
        if (esUuidValido(uuid)) {
            return uuid;
        }

        uuid = ejecutarComandoUuid(new String[]{
            "powershell",
            "-NoProfile",
            "-Command",
            "(Get-CimInstance Win32_ComputerSystemProduct).UUID"
        });
        if (esUuidValido(uuid)) {
            return uuid;
        }

        return "";
    }

    private String ejecutarComandoUuid(String[] command) {
        Process process = null;
        try {
            process = new ProcessBuilder(command).redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String valor = normalizarUuid(line);
                    if (esUuidValido(valor)) {
                        return valor;
                    }
                }
            } finally {
                reader.close();
            }
        } catch (IOException ex) {
            return "";
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return "";
    }

    private String normalizarUuid(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.trim().toUpperCase();
    }

    private boolean esUuidValido(String valor) {
        return valor != null && valor.matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        etiqueta.setForeground(COLOR_TITULO);
        return etiqueta;
    }

    private JButton crearBoton(String texto, Color fondo, Color colorTexto) {
        JButton boton = new JButton(texto);
        boton.setBackground(fondo);
        boton.setForeground(colorTexto);
        boton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(fondo.darker(), 18),
                new EmptyBorder(8, 16, 8, 16)));
        return boton;
    }

    private RoundedPanel crearTarjeta() {
        RoundedPanel tarjeta = new RoundedPanel(28, COLOR_TARJETA);
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 28),
                new EmptyBorder(16, 16, 16, 16)));
        return tarjeta;
    }

    private void setFormularioHabilitado(boolean enabled) {
        campoIpEmpresa.setEnabled(enabled);
        campoUsuario.setEnabled(enabled);
        campoPassword.setEnabled(enabled);
        campoRutaEmpresa.setEnabled(enabled);
        campoUuidEquipo.setEnabled(false);
        botonRutaEmpresa.setEnabled(enabled);
        botonCopiarUuid.setEnabled(enabled);
        if (botonGuardar != null) {
            botonGuardar.setEnabled(enabled);
        }
        setCursor(enabled ? null : java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
    }

    private static final class RoundedPanel extends JPanel {

        private final int radio;
        private final Color colorFondo;

        private RoundedPanel(int radio, Color colorFondo) {
            this.radio = radio;
            this.colorFondo = colorFondo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class RoundedBorder extends AbstractBorder {

        private final Color color;
        private final int radio;

        private RoundedBorder(Color color, int radio) {
            this.color = color;
            this.radio = radio;
        }

        @Override
        public void paintBorder(Component c, java.awt.Graphics g, int x, int y, int width, int height) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radio, radio);
            g2.dispose();
        }
    }
}
