package Ventanas;

import Metodos.ActivationEmailService;
import Metodos.ConfigManager;
import Metodos.Configuracion;
import Metodos.LicenseJsonValidator;
import Metodos.LicenseValidationResult;
import Metodos.PrinterUtils;
import Metodos.ReporteManager;
import java.awt.Desktop;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
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
    private static final Color COLOR_ERROR = new Color(220, 38, 38);
    private static final Dimension DIALOG_SIZE = new Dimension(820, 540);
    private static final Dimension MAX_DIALOG_SIZE = new Dimension(900, 560);
    private static final String TEXTO_FECHA_NO_DISPONIBLE = "-";
    private static final String TEXTO_SELECCIONAR_OPCION = "Seleccionar opción";

    private JComboBox<ItemAmbiente> comboAmbiente;
    private JComboBox<ItemFormatoPrecio> comboFormatoPrecio;
    private JTextArea campoClave;
    private JLabel etiquetaUuidLicencia;
    private JLabel etiquetaFechaInicio;
    private JLabel etiquetaFechaFin;
    private JLabel etiquetaArchivoLicencia;
    private JComboBox<String> comboImpresora;
    private JComboBox<String> comboReporte;
    private String uuidEquipoLocal;

    private final VentanaInicio ownerFrame;

    public ConfiguracionWindow(Frame owner, boolean modal) {
        super(owner, modal);
        this.ownerFrame = owner instanceof VentanaInicio ? (VentanaInicio) owner : null;
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
        uuidEquipoLocal = obtenerUuidEquipoLocal();

        JLabel etiquetaUuidEquipo = new JLabel("UUID del equipo: " + valorVisibleUuid(uuidEquipoLocal));
        etiquetaUuidEquipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        etiquetaUuidEquipo.setForeground(COLOR_TEXTO);

        JButton botonCopiarUuid = crearBoton("Copiar", new Color(239, 246, 255), COLOR_TITULO);
        botonCopiarUuid.addActionListener(e -> copiarUuidEquipo());

        JPanel filaUuid = new JPanel(new BorderLayout(8, 0));
        filaUuid.setOpaque(false);
        filaUuid.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaUuid.setMaximumSize(new Dimension(Integer.MAX_VALUE, botonCopiarUuid.getPreferredSize().height));
        filaUuid.add(etiquetaUuidEquipo, BorderLayout.CENTER);
        filaUuid.add(botonCopiarUuid, BorderLayout.EAST);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo);
        textos.add(javax.swing.Box.createVerticalStrut(4));
        textos.add(filaUuid);

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
            new ItemAmbiente("", TEXTO_SELECCIONAR_OPCION),
            new ItemAmbiente("a", "Pruebas"),
            new ItemAmbiente("b", "Producci\u00f3n")
        });
        configurarComboBox(comboAmbiente);
        tarjeta.add(crearCampoFormulario("Ambiente", comboAmbiente));
        tarjeta.add(javax.swing.Box.createVerticalStrut(10));

        comboFormatoPrecio = new JComboBox<ItemFormatoPrecio>(new ItemFormatoPrecio[]{
            new ItemFormatoPrecio("", TEXTO_SELECCIONAR_OPCION),
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
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton botonImportar = crearBoton("Importar .lic", new Color(239, 246, 255), COLOR_TITULO);
        botonImportar.addActionListener(e -> importarLicenciaDesdeArchivo());
        JButton botonSolicitarLicencia = crearBoton("Solicitar licencia por correo",
                new Color(239, 246, 255), COLOR_TITULO);
        botonSolicitarLicencia.addActionListener(e -> solicitarLicenciaPorCorreo());
        panel.add(botonImportar);
        panel.add(botonSolicitarLicencia);
        return panel;
    }

    private void configurarComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 18),
                new EmptyBorder(8, 10, 8, 10)));
        comboBox.addActionListener(e -> limpiarValidacionComboBox(comboBox));
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
        comboBox.addActionListener(e -> limpiarValidacionComboBox(comboBox));
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
        try {
            Properties properties = ConfigManager.loadProperties();
            seleccionarAmbiente(properties.getProperty("ambiente", ""));
            seleccionarFormatoPrecio(properties.getProperty("formatoPrecio", ""));
            cargarLicenciaGuardada(properties.getProperty("clave", ""));
            actualizarFechasLicencia();
            cargarImpresoras(properties.getProperty("impresora", ""));
            cargarReportes(properties.getProperty("reporte", ""));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo leer el archivo de configuraci\u00f3n.\n" + ex.getMessage(),
                    "Configuraci\u00f3n",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarImpresoras(String impresoraSeleccionada) {
        comboImpresora.removeAllItems();
        comboImpresora.addItem(TEXTO_SELECCIONAR_OPCION);
        List<String> impresoras = PrinterUtils.getInstalledPrinterNames();
        if (impresoras.isEmpty()) {
            comboImpresora.setEnabled(false);
            comboImpresora.setSelectedIndex(0);
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
        comboReporte.addItem(TEXTO_SELECCIONAR_OPCION);
        List<String> reportes = ReporteManager.listarReportesDisponibles();
        if (reportes.isEmpty()) {
            comboReporte.setEnabled(false);
            comboReporte.setSelectedIndex(0);
            return;
        }

        comboReporte.setEnabled(true);
        for (String reporte : reportes) {
            comboReporte.addItem(reporte);
        }
        String reporteResuelto = ReporteManager.resolverReporteConfigurado(reporteSeleccionado);
        if (reporteResuelto != null && !reporteResuelto.trim().isEmpty()) {
            comboReporte.setSelectedItem(reporteResuelto);
        } else {
            comboReporte.setSelectedIndex(0);
        }
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
        String licenciaGuardada = normalizarLicenciaJson(ConfigManager.getStoredLicenseKey());
        ActivationEmailService.ActivationEvent activationEvent
                = resolverEventoActivacion(licenciaGuardada, clave);
        limpiarValidacionComboBox(comboAmbiente);
        limpiarValidacionComboBox(comboFormatoPrecio);
        limpiarValidacionComboBox(comboImpresora);
        limpiarValidacionComboBox(comboReporte);

        if (!validarComboObligatorio(comboAmbiente, "Debe seleccionar un Ambiente.")) {
            return;
        }

        if (!validarComboObligatorio(comboFormatoPrecio, "Debe seleccionar un Formato de Precio.")) {
            return;
        }

        if (!validarComboObligatorio(comboImpresora, "Debe seleccionar una Impresora.")) {
            return;
        }

        if (!validarComboObligatorio(comboReporte, "Debe seleccionar un Reporte.")) {
            return;
        }

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

            try {
                ActivationEmailService.enviarCorreoActivacion(
                        validationResult,
                        obtenerArchivoLicenciaActual(),
                        activationEvent);
            } catch (ActivationEmailService.ActivationEmailException ex) {
                // El correo interno es informativo; si falla, la configuración debe continuar.
            }

            ConfigManager.saveConfiguration(
                    ambienteSeleccionado != null ? ambienteSeleccionado.codigo : "",
                    clave,
                    impresora,
                    formatoSeleccionado != null ? formatoSeleccionado.codigo : "",
                    reporte);

            Configuracion.leerArchivoDePropiedades();
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ocurrio un error general durante la activacion.\n" + ex.getMessage(),
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

    private String obtenerArchivoLicenciaActual() {
        if (etiquetaArchivoLicencia == null) {
            return TEXTO_FECHA_NO_DISPONIBLE;
        }

        String texto = etiquetaArchivoLicencia.getText();
        String prefijo = "Archivo importado: ";
        if (texto == null || texto.trim().isEmpty()) {
            return TEXTO_FECHA_NO_DISPONIBLE;
        }

        if (texto.startsWith(prefijo)) {
            return texto.substring(prefijo.length()).trim();
        }

        return texto.trim();
    }

    private ActivationEmailService.ActivationEvent resolverEventoActivacion(String licenciaAnterior,
            String licenciaNueva) {
        boolean primeraConfiguracion = ConfigManager.isFirstConfiguration();
        boolean habiaLicencia = licenciaAnterior != null && !licenciaAnterior.trim().isEmpty();
        boolean licenciaCambio = !normalizarLicenciaJson(licenciaAnterior)
                .equals(normalizarLicenciaJson(licenciaNueva));

        if (primeraConfiguracion) {
            return ActivationEmailService.ActivationEvent.ACTIVACION_INICIAL;
        }

        if (habiaLicencia && licenciaCambio) {
            return ActivationEmailService.ActivationEvent.REACTIVACION;
        }

        return ActivationEmailService.ActivationEvent.SIN_ENVIO;
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

    private boolean validarComboObligatorio(JComboBox<?> comboBox, String mensaje) {
        if (!comboBox.isEnabled()) {
            resaltarComboBoxInvalido(comboBox);
            JOptionPane.showMessageDialog(this, mensaje, "Configuración", JOptionPane.WARNING_MESSAGE);
            comboBox.requestFocusInWindow();
            return false;
        }

        Object seleccionado = comboBox.getSelectedItem();
        if (seleccionado == null) {
            resaltarComboBoxInvalido(comboBox);
            JOptionPane.showMessageDialog(this, mensaje, "Configuración", JOptionPane.WARNING_MESSAGE);
            comboBox.requestFocusInWindow();
            return false;
        }

        if (seleccionado instanceof ItemAmbiente) {
            if (((ItemAmbiente) seleccionado).codigo.trim().isEmpty()) {
                resaltarComboBoxInvalido(comboBox);
                JOptionPane.showMessageDialog(this, mensaje, "Configuración", JOptionPane.WARNING_MESSAGE);
                comboBox.requestFocusInWindow();
                return false;
            }
            return true;
        }

        if (seleccionado instanceof ItemFormatoPrecio) {
            if (((ItemFormatoPrecio) seleccionado).codigo.trim().isEmpty()) {
                resaltarComboBoxInvalido(comboBox);
                JOptionPane.showMessageDialog(this, mensaje, "Configuración", JOptionPane.WARNING_MESSAGE);
                comboBox.requestFocusInWindow();
                return false;
            }
            return true;
        }

        String valor = seleccionado.toString().trim();
        if (valor.isEmpty() || TEXTO_SELECCIONAR_OPCION.equals(valor)) {
            resaltarComboBoxInvalido(comboBox);
            JOptionPane.showMessageDialog(this, mensaje, "Configuración", JOptionPane.WARNING_MESSAGE);
            comboBox.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private void resaltarComboBoxInvalido(JComboBox<?> comboBox) {
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_ERROR, 18),
                new EmptyBorder(8, 10, 8, 10)));
    }

    private void limpiarValidacionComboBox(JComboBox<?> comboBox) {
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 18),
                new EmptyBorder(8, 10, 8, 10)));
    }

    private void copiarUuidEquipo() {
        if (uuidEquipoLocal == null || uuidEquipoLocal.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo obtener el UUID del equipo.",
                    "UUID",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringSelection seleccion = new StringSelection(uuidEquipoLocal);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(seleccion, null);
        JOptionPane.showMessageDialog(this,
                "UUID copiado al portapapeles.",
                "UUID",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void solicitarLicenciaPorCorreo() {
        if (uuidEquipoLocal == null || uuidEquipoLocal.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo obtener el UUID del equipo.",
                    "UUID",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String correoSolicitudLicencia = ConfigManager.getLicenseRequestEmail();
        if (correoSolicitudLicencia.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontro el correo de licenciamiento en contacto.properties.\n"
                    + "Ruta esperada: " + ConfigManager.getContactConfigPath(),
                    "Correo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String asunto = "Solicitud de activaci\u00f3n de sistema";
        String cuerpo = "Hola..\r\n\r\n"
                + "Solicito la activaci\u00f3n/licenciamiento del sistema para el siguiente equipo:\r\n\r\n"
                + "UUID:\r\n"
                + uuidEquipoLocal
                + "\r\n\r\n"
                + "Gracias.";

        String[] opciones = {"Cliente predeterminado", "Gmail", "Outlook", "Cancelar"};
        int opcion = JOptionPane.showOptionDialog(
                this,
                "Selecciona c\u00f3mo deseas crear el correo.",
                "Solicitar licencia",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                opciones,
                opciones[0]);

        try {
            if (opcion == 0) {
                abrirClienteCorreoPredeterminado(asunto, cuerpo);
            } else if (opcion == 1) {
                abrirEnNavegador("https://mail.google.com/mail/?view=cm&fs=1&to="
                        + codificarParaMailto(correoSolicitudLicencia)
                        + "&su="
                        + codificarParaMailto(asunto)
                        + "&body="
                        + codificarParaMailto(cuerpo));
            } else if (opcion == 2) {
                abrirEnNavegador("https://outlook.office.com/mail/deeplink/compose?to="
                        + codificarParaMailto(correoSolicitudLicencia)
                        + "&subject="
                        + codificarParaMailto(asunto)
                        + "&body="
                        + codificarParaMailto(cuerpo));
            }
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "No fue posible abrir la opci\u00f3n de correo seleccionada.",
                    "Correo",
                    JOptionPane.ERROR_MESSAGE);
        }
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

    private String valorVisibleUuid(String uuid) {
        return uuid == null || uuid.trim().isEmpty() ? TEXTO_FECHA_NO_DISPONIBLE : uuid;
    }

    private void abrirClienteCorreoPredeterminado(String asunto, String cuerpo) throws IOException {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
            throw new IOException("Cliente de correo no disponible");
        }

        String correoSolicitudLicencia = ConfigManager.getLicenseRequestEmail();
        if (correoSolicitudLicencia.isEmpty()) {
            throw new IOException("Correo de licenciamiento no configurado");
        }

        String mailto = "mailto:" + codificarParaMailto(correoSolicitudLicencia)
                + "?subject=" + codificarParaMailto(asunto)
                + "&body=" + codificarParaMailto(cuerpo);
        Desktop.getDesktop().mail(URI.create(mailto));
    }

    private void abrirEnNavegador(String url) throws IOException {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            throw new IOException("Navegador no disponible");
        }

        Desktop.getDesktop().browse(URI.create(url));
    }

    private String codificarParaMailto(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8).replace("+", "%20");
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
            if (codigo == null || codigo.trim().isEmpty()) {
                return descripcion;
            }
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
            if (codigo == null || codigo.trim().isEmpty()) {
                return descripcion;
            }
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
