package Ventanas;

import Metodos.LicenseJsonValidator;
import Metodos.LicenseValidationResult;
import Metodos.PrinterUtils;
import Metodos.ReporteManager;
import App.ApplicationContext;
import aplicacion.ConfigService;
import dominio.ConfiguracionApp;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ConfiguracionWindow extends JDialog {

    private static final Color COLOR_FONDO = new Color(243, 244, 246);
    private static final Color COLOR_TARJETA = Color.WHITE;
    private static final Color COLOR_BORDE = new Color(220, 226, 232);
    private static final Color COLOR_TITULO = new Color(31, 41, 55);
    private static final Color COLOR_TEXTO = new Color(75, 85, 99);
    private static final Color COLOR_VERDE = new Color(22, 163, 74);
    private static final Color COLOR_GRIS = new Color(229, 231, 235);
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(107, 114, 128);
    private static final Dimension DIALOG_SIZE = new Dimension(820, 540);
    private static final Dimension MAX_DIALOG_SIZE = new Dimension(900, 560);
    private static final String TEXTO_FECHA_NO_DISPONIBLE = "-";

    private JComboBox<ItemAmbiente> comboAmbiente;
    private JComboBox<ItemFormatoPrecio> comboFormatoPrecio;
    private JTextArea campoClave;
    private JLabel etiquetaUuidLicencia;
    private JLabel etiquetaFechaInicio;
    private JLabel etiquetaFechaFin;
    private JLabel etiquetaArchivoLicencia;
    private JComboBox<String> comboImpresora;
    private JComboBox<String> comboReporte;

    private final VentanaInicio ownerFrame;
    private final ConfigService configService;

    public ConfiguracionWindow(Frame owner, boolean modal, ApplicationContext applicationContext) {
        super(owner, modal);
        this.ownerFrame = owner instanceof VentanaInicio ? (VentanaInicio) owner : null;
        this.configService = applicationContext.getConfigService();
        initComponents();
        cargarConfiguracion();
    }

    private void initComponents() {
        setTitle("Configuraci\u00f3n del sistema");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        RoundedPanel root = new RoundedPanel(28, COLOR_FONDO);
        root.setLayout(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.add(crearHeader(), BorderLayout.NORTH);
        root.add(crearScrollFormulario(), BorderLayout.CENTER);
        root.add(crearFooter(), BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        setPreferredSize(calcularTamanoDialogo());
        setMaximumSize(calcularTamanoDialogo());
        pack();
        setLocationRelativeTo(ownerFrame);
    }

    private Dimension calcularTamanoDialogo() {
        Dimension size = new Dimension(DIALOG_SIZE);
        if (ownerFrame != null) {
            size.width = Math.min(size.width, ownerFrame.getWidth());
            size.height = Math.min(size.height, ownerFrame.getHeight());
        }
        size.width = Math.min(size.width, MAX_DIALOG_SIZE.width);
        size.height = Math.min(size.height, MAX_DIALOG_SIZE.height);
        return size;
    }

    private JPanel crearHeader() {
        RoundedPanel header = crearTarjeta();
        header.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Configuraci\u00f3n");
        titulo.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 24));
        titulo.setForeground(COLOR_TITULO);

        JLabel subtitulo = new JLabel("<html>Administra ambiente, licencia, impresora y reporte.<br>"
                + "Archivo: " + Metodos.ConfigManager.getConfigPath() + "</html>");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(COLOR_TEXTO);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo);
        textos.add(javax.swing.Box.createVerticalStrut(4));
        textos.add(subtitulo);

        header.add(textos, BorderLayout.CENTER);
        return header;
    }

    private JScrollPane crearScrollFormulario() {
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(crearFormulario());

        JScrollPane scrollPane = new JScrollPane(contenido);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel crearFormulario() {
        RoundedPanel tarjeta = crearTarjeta();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboAmbiente = new JComboBox<ItemAmbiente>(new ItemAmbiente[]{
            new ItemAmbiente("a", "Pruebas"),
            new ItemAmbiente("b", "Producci\u00f3n")
        });
        configurarComboBox(comboAmbiente);
        tarjeta.add(crearCampoFormulario("Ambiente", comboAmbiente));
        tarjeta.add(javax.swing.Box.createVerticalStrut(10));

        comboFormatoPrecio = new JComboBox<ItemFormatoPrecio>(new ItemFormatoPrecio[]{
            new ItemFormatoPrecio("CO", "Sin decimales"),
            new ItemFormatoPrecio("MX", "2 decimales")
        });
        configurarComboBox(comboFormatoPrecio);
        tarjeta.add(crearCampoFormulario("Formato de Precio", comboFormatoPrecio));
        tarjeta.add(javax.swing.Box.createVerticalStrut(10));

        campoClave = crearTextArea();
        campoClave.setVisible(false);
        tarjeta.add(crearCampoFormulario("Licencia", crearPanelLicencia()));
        tarjeta.add(javax.swing.Box.createVerticalStrut(6));
        tarjeta.add(crearPanelDatosLicencia());
        tarjeta.add(javax.swing.Box.createVerticalStrut(10));

        comboImpresora = crearComboBox();
        tarjeta.add(crearCampoFormulario("Impresora", comboImpresora));
        tarjeta.add(javax.swing.Box.createVerticalStrut(10));

        comboReporte = crearComboBox();
        tarjeta.add(crearCampoFormulario("Reporte", comboReporte));

        return tarjeta;
    }

    private JPanel crearFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);

        JButton botonCancelar = crearBoton("Cancelar", COLOR_GRIS, COLOR_TITULO);
        botonCancelar.addActionListener(e -> dispose());

        JButton botonGuardar = crearBoton("Guardar", new Color(220, 252, 231), COLOR_VERDE);
        botonGuardar.addActionListener(e -> guardarConfiguracion());

        footer.add(botonCancelar);
        footer.add(botonGuardar);
        return footer;
    }

    private JPanel crearCampoFormulario(String label, Component componente) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel etiqueta = new JLabel(label);
        etiqueta.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        etiqueta.setForeground(COLOR_TITULO);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (componente instanceof JComponent) {
            JComponent componenteSwing = (JComponent) componente;
            componenteSwing.setAlignmentX(Component.LEFT_ALIGNMENT);
            componenteSwing.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                    componenteSwing.getPreferredSize().height));
        }

        panel.add(etiqueta);
        panel.add(javax.swing.Box.createVerticalStrut(5));
        panel.add(componente);
        return panel;
    }

    private JTextField crearTextField() {
        JTextField textField = new JTextField();
        textField.setColumns(24);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textField.setForeground(COLOR_TITULO);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 18),
                new EmptyBorder(9, 12, 9, 12)));
        return textField;
    }

    private JTextArea crearTextArea() {
        JTextArea textArea = new JTextArea(3, 24);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textArea.setForeground(COLOR_TITULO);
        textArea.setBorder(new EmptyBorder(9, 12, 9, 12));
        return textArea;
    }

    private JScrollPane crearScrollAreaCampo(JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 18),
                BorderFactory.createEmptyBorder()));
        Dimension preferred = scrollPane.getPreferredSize();
        scrollPane.setPreferredSize(new Dimension(preferred.width, textArea.getPreferredSize().height + 6));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, textArea.getPreferredSize().height + 6));
        return scrollPane;
    }

    private JPanel crearPanelLicencia() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton botonImportar = crearBoton("Importar .lic", new Color(239, 246, 255), COLOR_TITULO);
        botonImportar.addActionListener(e -> importarLicenciaDesdeArchivo());
        panel.add(botonImportar);
        return panel;
    }

    private void configurarComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboBox.getPreferredSize().height));
    }

    private JComboBox<String> crearComboBox() {
        JComboBox<String> comboBox = new JComboBox<String>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 18),
                new EmptyBorder(8, 10, 8, 10)));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboBox.getPreferredSize().height));
        return comboBox;
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

    private void cargarConfiguracion() {
        ConfiguracionApp config = configService.cargarConfiguracion();
        seleccionarAmbiente(valorSeguro(config.getAmbiente(), "a"));
        seleccionarFormatoPrecio(valorSeguro(config.getFormatoPrecio(), "CO"));
        cargarLicenciaGuardada(valorSeguro(config.getClave(), ""));
        actualizarFechasLicencia();
        cargarImpresoras(valorSeguro(config.getImpresora(), ""));
        cargarReportes(valorSeguro(config.getReporte(), ""));
    }

    private void cargarImpresoras(String impresoraSeleccionada) {
        comboImpresora.removeAllItems();
        List<String> impresoras = PrinterUtils.getInstalledPrinterNames();
        if (impresoras.isEmpty()) {
            comboImpresora.addItem("Sin impresoras detectadas");
            comboImpresora.setEnabled(false);
            return;
        }

        comboImpresora.setEnabled(true);
        boolean encontrada = false;
        for (String impresora : impresoras) {
            comboImpresora.addItem(impresora);
            if (impresora.equalsIgnoreCase(impresoraSeleccionada)) {
                encontrada = true;
            }
        }

        if (encontrada) {
            comboImpresora.setSelectedItem(impresoraSeleccionada);
        } else if (impresoraSeleccionada != null && !impresoraSeleccionada.trim().isEmpty()) {
            comboImpresora.addItem(impresoraSeleccionada);
            comboImpresora.setSelectedItem(impresoraSeleccionada);
        } else {
            comboImpresora.setSelectedIndex(0);
        }
    }

    private void cargarReportes(String reporteSeleccionado) {
        comboReporte.removeAllItems();
        List<String> reportes = ReporteManager.listarReportesDisponibles();
        if (reportes.isEmpty()) {
            comboReporte.addItem("Sin reportes detectados");
            comboReporte.setEnabled(false);
            return;
        }

        comboReporte.setEnabled(true);
        for (String reporte : reportes) {
            comboReporte.addItem(reporte);
        }
        comboReporte.setSelectedItem(ReporteManager.resolverReporteConfigurado(reporteSeleccionado));
    }

    private void seleccionarAmbiente(String ambiente) {
        for (int i = 0; i < comboAmbiente.getItemCount(); i++) {
            ItemAmbiente item = comboAmbiente.getItemAt(i);
            if (item.codigo.equalsIgnoreCase(ambiente)) {
                comboAmbiente.setSelectedIndex(i);
                return;
            }
        }
        comboAmbiente.setSelectedIndex(0);
    }

    private void seleccionarFormatoPrecio(String formatoPrecio) {
        for (int i = 0; i < comboFormatoPrecio.getItemCount(); i++) {
            ItemFormatoPrecio item = comboFormatoPrecio.getItemAt(i);
            if (item.codigo.equalsIgnoreCase(formatoPrecio)) {
                comboFormatoPrecio.setSelectedIndex(i);
                return;
            }
        }
        comboFormatoPrecio.setSelectedIndex(0);
    }

    private void guardarConfiguracion() {
        String clave = normalizarLicenciaJson(campoClave.getText());

        if (clave.isEmpty()) {
            campoClave.requestFocusInWindow();
            ToastNotification.showWarning(this, "La licencia no puede estar vac\u00eda", 2000);
            return;
        }

        LicenseValidationResult validationResult = LicenseJsonValidator.validate(clave);
        if (!validationResult.isLicenciaValida()) {
            campoClave.requestFocusInWindow();
            JOptionPane.showMessageDialog(this,
                    validationResult.buildSummary(),
                    "Licencia no valida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            campoClave.setText(clave);
            ItemAmbiente ambienteSeleccionado = (ItemAmbiente) comboAmbiente.getSelectedItem();
            ItemFormatoPrecio formatoSeleccionado = (ItemFormatoPrecio) comboFormatoPrecio.getSelectedItem();
            String impresora = comboImpresora.isEnabled() && comboImpresora.getSelectedItem() != null
                    ? comboImpresora.getSelectedItem().toString()
                    : "";
            String reporte = comboReporte.isEnabled() && comboReporte.getSelectedItem() != null
                    ? comboReporte.getSelectedItem().toString()
                    : "";

            configService.guardarPreferencias(
                    ambienteSeleccionado != null ? ambienteSeleccionado.codigo : "a",
                    clave,
                    impresora,
                    formatoSeleccionado != null ? formatoSeleccionado.codigo : "CO",
                    reporte);
            if (ownerFrame != null) {
                ownerFrame.aplicarConfiguracionActual();
            }

            ToastNotification.showSuccess(this, "Configuraci\u00f3n guardada correctamente", 2000);
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la configuraci\u00f3n.\n" + ex.getMessage(),
                    "Configuraci\u00f3n",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearPanelDatosLicencia() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        etiquetaArchivoLicencia = crearEtiquetaLicencia("Archivo importado: " + TEXTO_FECHA_NO_DISPONIBLE);
        etiquetaUuidLicencia = crearEtiquetaLicencia("UUID licencia: " + TEXTO_FECHA_NO_DISPONIBLE);
        etiquetaFechaInicio = crearEtiquetaLicencia("Vigencia desde: " + TEXTO_FECHA_NO_DISPONIBLE);
        etiquetaFechaFin = crearEtiquetaLicencia("Vigencia hasta: " + TEXTO_FECHA_NO_DISPONIBLE);

        panel.add(etiquetaArchivoLicencia);
        panel.add(javax.swing.Box.createVerticalStrut(2));
        panel.add(etiquetaUuidLicencia);
        panel.add(javax.swing.Box.createVerticalStrut(2));
        panel.add(etiquetaFechaInicio);
        panel.add(javax.swing.Box.createVerticalStrut(2));
        panel.add(etiquetaFechaFin);

        campoClave.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarFechasLicencia();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarFechasLicencia();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarFechasLicencia();
            }
        });

        return panel;
    }

    private JLabel crearEtiquetaLicencia(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        etiqueta.setForeground(COLOR_TEXTO_SECUNDARIO);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    private void actualizarFechasLicencia() {
        String licenciaJson = normalizarLicenciaJson(campoClave.getText());
        String uuid = LicenseJsonValidator.extractUuid(licenciaJson);
        Date[] fechas = obtenerFechasLicencia(licenciaJson);
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (uuid == null || fechas == null) {
            if (etiquetaArchivoLicencia != null && etiquetaArchivoLicencia.getText().trim().isEmpty()) {
                etiquetaArchivoLicencia.setText("Archivo importado: " + TEXTO_FECHA_NO_DISPONIBLE);
            }
            etiquetaUuidLicencia.setText("UUID licencia: " + TEXTO_FECHA_NO_DISPONIBLE);
            etiquetaFechaInicio.setText("Vigencia desde: " + TEXTO_FECHA_NO_DISPONIBLE);
            etiquetaFechaFin.setText("Vigencia hasta: " + TEXTO_FECHA_NO_DISPONIBLE);
            return;
        }

        etiquetaUuidLicencia.setText("UUID licencia: " + uuid);
        etiquetaFechaInicio.setText("Vigencia desde: " + formato.format(fechas[0]));
        etiquetaFechaFin.setText("Vigencia hasta: " + formato.format(fechas[1]));
    }

    private Date[] obtenerFechasLicencia(String licenciaJson) {
        if (licenciaJson == null || licenciaJson.isEmpty()) {
            return null;
        }

        return LicenseJsonValidator.extractDates(licenciaJson);
    }

    private String normalizarLicenciaJson(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.trim();
    }

    private String valorSeguro(String valor, String fallback) {
        if (valor == null || valor.trim().isEmpty()) {
            return fallback;
        }
        return valor;
    }

    private void importarLicenciaDesdeArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar licencia");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Licencias (*.lic)", "lic"));

        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();
        if (selectedFile == null) {
            return;
        }

        try {
            String licenciaJson = LicenseJsonValidator.readLicenseContentFromFile(selectedFile.getAbsolutePath());
            if (licenciaJson == null || licenciaJson.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "El archivo seleccionado no contiene una licencia valida.",
                        "Importar licencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            campoClave.setText(licenciaJson.trim());
            etiquetaArchivoLicencia.setText("Archivo importado: " + selectedFile.getName());
            actualizarFechasLicencia();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo leer el archivo de licencia.\n" + ex.getMessage(),
                    "Importar licencia",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarLicenciaGuardada(String valorGuardado) {
        String licencia = normalizarLicenciaJson(valorGuardado);
        if (licencia.isEmpty()) {
            campoClave.setText("");
            if (etiquetaArchivoLicencia != null) {
                etiquetaArchivoLicencia.setText("Archivo importado: " + TEXTO_FECHA_NO_DISPONIBLE);
            }
            return;
        }

        if (licencia.startsWith("{")) {
            campoClave.setText(licencia);
            if (etiquetaArchivoLicencia != null) {
                etiquetaArchivoLicencia.setText("Archivo importado: configuracion.properties");
            }
            return;
        }

        try {
            String contenido = LicenseJsonValidator.readLicenseContentFromFile(licencia);
            if (contenido != null && !contenido.trim().isEmpty()) {
                campoClave.setText(contenido.trim());
                if (etiquetaArchivoLicencia != null) {
                    etiquetaArchivoLicencia.setText("Archivo importado: " + new File(licencia).getName());
                }
                return;
            }
        } catch (IOException ex) {
        }

        campoClave.setText(licencia);
        if (etiquetaArchivoLicencia != null) {
            etiquetaArchivoLicencia.setText("Archivo importado: " + TEXTO_FECHA_NO_DISPONIBLE);
        }
    }

    private static final class ItemAmbiente {

        private final String codigo;
        private final String descripcion;

        private ItemAmbiente(String codigo, String descripcion) {
            this.codigo = codigo;
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return codigo + " - " + descripcion;
        }
    }

    private static final class ItemFormatoPrecio {

        private final String codigo;
        private final String descripcion;

        private ItemFormatoPrecio(String codigo, String descripcion) {
            this.codigo = codigo;
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return codigo + " - " + descripcion;
        }
    }

    private static final class RoundedPanel extends JPanel {

        private final int radio;
        private final Color fondo;

        private RoundedPanel(int radio, Color fondo) {
            this.radio = radio;
            this.fondo = fondo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fondo);
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
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radio, radio);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = 1;
            insets.right = 1;
            insets.top = 1;
            insets.bottom = 1;
            return insets;
        }
    }
}
