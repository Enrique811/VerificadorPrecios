package Ventanas;

import Metodos.ConfigManager;
import Metodos.Configuracion;
import Metodos.PrinterUtils;
import Metodos.ReporteManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class ConfiguracionWindow extends JDialog {

    private static final Color COLOR_FONDO = new Color(243, 244, 246);
    private static final Color COLOR_TARJETA = Color.WHITE;
    private static final Color COLOR_BORDE = new Color(220, 226, 232);
    private static final Color COLOR_TITULO = new Color(31, 41, 55);
    private static final Color COLOR_TEXTO = new Color(75, 85, 99);
    private static final Color COLOR_AZUL = new Color(45, 111, 214);
    private static final Color COLOR_VERDE = new Color(22, 163, 74);
    private static final Color COLOR_GRIS = new Color(229, 231, 235);

    private JComboBox<ItemAmbiente> comboAmbiente;
    private JComboBox<ItemFormatoPrecio> comboFormatoPrecio;
    private JTextField campoClave;
    private JComboBox<String> comboImpresora;
    private JTextField campoInformacion;
    private JTextField campoIpEmpresa;
    private JComboBox<String> comboReporte;

    private final VentanaInicio ownerFrame;

    public ConfiguracionWindow(Frame owner, boolean modal) {
        super(owner, modal);
        this.ownerFrame = owner instanceof VentanaInicio ? (VentanaInicio) owner : null;
        initComponents();
        cargarConfiguracion();
    }

    private void initComponents() {
        setTitle("ConfiguraciÃ³n del sistema");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(560, 520));

        RoundedPanel root = new RoundedPanel(28, COLOR_FONDO);
        root.setLayout(new BorderLayout(20, 20));
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        root.add(crearHeader(), BorderLayout.NORTH);
        root.add(crearFormulario(), BorderLayout.CENTER);
        root.add(crearFooter(), BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();
        setLocationRelativeTo(ownerFrame);
    }

    private JPanel crearHeader() {
        RoundedPanel header = crearTarjeta();
        header.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("ConfiguraciÃ³n");
        titulo.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 28));
        titulo.setForeground(COLOR_TITULO);

        JLabel subtitulo = new JLabel("<html>Administra ambiente, licencia, impresora e IP del servidor.<br>"
                + "Archivo: " + ConfigManager.getConfigPath() + "</html>");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(COLOR_TEXTO);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo);
        textos.add(javax.swing.Box.createVerticalStrut(6));
        textos.add(subtitulo);

        header.add(textos, BorderLayout.CENTER);
        return header;
    }

    private JPanel crearFormulario() {
        RoundedPanel tarjeta = crearTarjeta();
        tarjeta.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 16, 0);

        comboAmbiente = new JComboBox<ItemAmbiente>(new ItemAmbiente[]{
            new ItemAmbiente("a", "Pruebas"),
            new ItemAmbiente("b", "ProducciÃ³n")
        });
        tarjeta.add(crearCampoFormulario("Ambiente", comboAmbiente), gbc);

        gbc.gridy++;
        comboFormatoPrecio = new JComboBox<ItemFormatoPrecio>(new ItemFormatoPrecio[]{
            new ItemFormatoPrecio("CO", "Sin decimales"),
            new ItemFormatoPrecio("MX", "2 decimales")
        });
        tarjeta.add(crearCampoFormulario("Formato de Precio", comboFormatoPrecio), gbc);

        gbc.gridy++;
        campoClave = crearTextField();
        tarjeta.add(crearCampoFormulario("Clave (Licencia)", campoClave), gbc);

        gbc.gridy++;
        comboImpresora = new JComboBox<String>();
        comboImpresora.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboImpresora.setBackground(Color.WHITE);
        comboImpresora.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 18),
                new EmptyBorder(10, 12, 10, 12)));
        tarjeta.add(crearCampoFormulario("Impresora", comboImpresora), gbc);

        gbc.gridy++;
        campoInformacion = crearTextField();
        tarjeta.add(crearCampoFormulario("InformaciÃ³n", campoInformacion), gbc);

        gbc.gridy++;
        comboReporte = new JComboBox<String>();
        comboReporte.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboReporte.setBackground(Color.WHITE);
        comboReporte.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 18),
                new EmptyBorder(10, 12, 10, 12)));
        tarjeta.add(crearCampoFormulario("Reporte", comboReporte), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        campoIpEmpresa = crearTextField();
        tarjeta.add(crearCampoFormulario("IP Empresa", campoIpEmpresa), gbc);

        return tarjeta;
    }

    private JPanel crearFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
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

        JLabel etiqueta = new JLabel(label);
        etiqueta.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        etiqueta.setForeground(COLOR_TITULO);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (componente instanceof JComponent) {
            ((JComponent) componente).setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        panel.add(etiqueta);
        panel.add(javax.swing.Box.createVerticalStrut(8));
        panel.add(componente);
        return panel;
    }

    private JTextField crearTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setForeground(COLOR_TITULO);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 18),
                new EmptyBorder(12, 14, 12, 14)));
        return textField;
    }

    private JButton crearBoton(String texto, Color fondo, Color colorTexto) {
        JButton boton = new JButton(texto);
        boton.setBackground(fondo);
        boton.setForeground(colorTexto);
        boton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(fondo.darker(), 18),
                new EmptyBorder(10, 18, 10, 18)));
        return boton;
    }

    private RoundedPanel crearTarjeta() {
        RoundedPanel tarjeta = new RoundedPanel(28, COLOR_TARJETA);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 28),
                new EmptyBorder(22, 22, 22, 22)));
        return tarjeta;
    }

    private void cargarConfiguracion() {
        try {
            Properties properties = ConfigManager.loadProperties();
            seleccionarAmbiente(properties.getProperty("ambiente", "a"));
            seleccionarFormatoPrecio(properties.getProperty("formatoPrecio", "CO"));
            campoClave.setText(properties.getProperty("clave", ""));
            campoInformacion.setText(properties.getProperty("informacion", ""));
            campoIpEmpresa.setText(properties.getProperty("ipEmpresa", ""));
            cargarImpresoras(properties.getProperty("impresora", ""));
            cargarReportes(properties.getProperty("reporte", ""));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo leer el archivo de configuraciÃ³n.\n" + ex.getMessage(),
                    "ConfiguraciÃ³n",
                    JOptionPane.ERROR_MESSAGE);
        }
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
        String clave = campoClave.getText().trim();
        String ipEmpresa = campoIpEmpresa.getText().trim();
        String informacion = campoInformacion.getText().trim();

        if (clave.isEmpty()) {
            campoClave.requestFocusInWindow();
            ToastNotification.showWarning(this, "La clave no puede estar vacÃ­a", 2000);
            return;
        }

        if (ipEmpresa.isEmpty()) {
            campoIpEmpresa.requestFocusInWindow();
            ToastNotification.showWarning(this, "La IP Empresa no puede estar vacÃ­a", 2000);
            return;
        }

        try {
            ItemAmbiente ambienteSeleccionado = (ItemAmbiente) comboAmbiente.getSelectedItem();
            ItemFormatoPrecio formatoSeleccionado = (ItemFormatoPrecio) comboFormatoPrecio.getSelectedItem();
            String impresora = comboImpresora.isEnabled() && comboImpresora.getSelectedItem() != null
                    ? comboImpresora.getSelectedItem().toString()
                    : "";
            String reporte = comboReporte.isEnabled() && comboReporte.getSelectedItem() != null
                    ? comboReporte.getSelectedItem().toString()
                    : "";

            ConfigManager.saveConfiguration(
                    ambienteSeleccionado != null ? ambienteSeleccionado.codigo : "a",
                    clave,
                    impresora,
                    informacion,
                    ipEmpresa,
                    formatoSeleccionado != null ? formatoSeleccionado.codigo : "CO",
                    reporte);

            Configuracion.leerArchivoDePropiedades();
            if (ownerFrame != null) {
                ownerFrame.aplicarConfiguracionActual();
            }

            ToastNotification.showSuccess(this, "ConfiguraciÃ³n guardada correctamente", 2000);
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la configuraciÃ³n.\n" + ex.getMessage(),
                    "ConfiguraciÃ³n",
                    JOptionPane.ERROR_MESSAGE);
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
