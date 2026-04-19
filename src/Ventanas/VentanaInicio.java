package Ventanas;

import Conexion.Conexion;
import Metodos.Configuracion;
import Metodos.DatosReporte;
import Metodos.PrecioFormatter;
import static SQL.SQLFechaHora.obtenerFechayHoraActualDelServidor;
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
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import net.sf.jasperreports.view.JasperViewer;

public class VentanaInicio extends JFrame {

    public static boolean controlAdministracion;
    public static boolean controlRutas;

    public static JTextField codigoBarras;
    public static JTextField informacion;
    public static JTextArea descripcion;
    public static JTextArea presentacion;
    public static JTextArea formato;
    public static JTextArea noEncontrado;
    public static JLabel precio;

    private static final Color COLOR_FONDO = new Color(243, 244, 246);
    private static final Color COLOR_TARJETA = Color.WHITE;
    private static final Color COLOR_BORDE = new Color(220, 226, 232);
    private static final Color COLOR_TITULO = new Color(31, 41, 55);
    private static final Color COLOR_TEXTO = new Color(75, 85, 99);
    private static final Color COLOR_AZUL = new Color(45, 111, 214);
    private static final Color COLOR_AZUL_SUAVE = new Color(232, 240, 254);
    private static final Color COLOR_GRIS_BOTON = new Color(229, 231, 235);
    private static final Color COLOR_ROJO_SUAVE = new Color(254, 226, 226);
    private static final Color COLOR_ROJO_TEXTO = new Color(185, 28, 28);
    private static final Color COLOR_VERDE_SUAVE = new Color(220, 252, 231);
    private static final Color COLOR_VERDE_TEXTO = new Color(21, 128, 61);

    private JLabel etiquetaTotales;
    private JLabel etiquetaEncabezado;
    private JLabel etiquetaSubtitulo;
    private JLabel etiquetaAccesos;
    private JButton botonBuscar;
    private JButton botonImprimir;
    private JButton botonConfiguracion;

    public VentanaInicio() {
        initComponents();
        Configuracion.leerArchivoDePropiedades();
        informacion.setText(Configuracion.informacion);
        configurarAcciones();
        setLocationRelativeTo(null);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        codigoBarras.requestFocusInWindow();
        Conexion.ConectarBDEmpresa();
        Conexion.tieneLicenciavalida();
    }

    private void initComponents() {
        setTitle("Verificador de precios");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));

        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(COLOR_FONDO);
        contenedor.setBorder(new EmptyBorder(18, 20, 18, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 18, 0);
        contenedor.add(crearHeader(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 1;
        contenedor.add(crearContenidoPrincipal(), gbc);

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.insets = new Insets(18, 0, 0, 0);
        contenedor.add(crearFooter(), gbc);

        setContentPane(contenedor);

        pack();
    }

    private JPanel crearHeader() {
        RoundedPanel header = new RoundedPanel(28, COLOR_TARJETA);
        header.setLayout(new BorderLayout(12, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 28),
                new EmptyBorder(18, 20, 18, 20)));

        JPanel bloqueTitulos = new JPanel();
        bloqueTitulos.setOpaque(false);
        bloqueTitulos.setLayout(new BoxLayout(bloqueTitulos, BoxLayout.Y_AXIS));

        etiquetaEncabezado = new JLabel("Verificador de precios");
        etiquetaEncabezado.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 30));
        etiquetaEncabezado.setForeground(COLOR_TITULO);

        etiquetaSubtitulo = new JLabel("Consulta rápida, búsqueda de productos e impresión de etiquetas");
        etiquetaSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        etiquetaSubtitulo.setForeground(COLOR_TEXTO);

        bloqueTitulos.add(etiquetaEncabezado);
        bloqueTitulos.add(Box.createVerticalStrut(4));
        bloqueTitulos.add(etiquetaSubtitulo);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);

        botonBuscar = crearBoton("Buscar productos", COLOR_AZUL_SUAVE, COLOR_AZUL);
        botonImprimir = crearBoton("Imprimir etiqueta", COLOR_GRIS_BOTON, COLOR_TITULO);
        botonConfiguracion = crearBoton("Configuración", new Color(243, 244, 246), COLOR_TITULO);

        acciones.add(botonBuscar);
        acciones.add(botonImprimir);
        acciones.add(botonConfiguracion);

        header.add(bloqueTitulos, BorderLayout.CENTER);
        header.add(acciones, BorderLayout.EAST);
        return header;
    }

    private JPanel crearContenidoPrincipal() {
        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setOpaque(false);

        JPanel columnaIzquierda = new JPanel();
        columnaIzquierda.setOpaque(false);
        columnaIzquierda.setLayout(new GridBagLayout());

        GridBagConstraints gbcIzquierda = new GridBagConstraints();
        gbcIzquierda.gridx = 0;
        gbcIzquierda.weightx = 1;
        gbcIzquierda.fill = GridBagConstraints.BOTH;

        gbcIzquierda.gridy = 0;
        gbcIzquierda.weighty = 0;
        gbcIzquierda.insets = new Insets(0, 0, 18, 0);
        columnaIzquierda.add(crearTarjetaCaptura(), gbcIzquierda);

        gbcIzquierda.gridy = 1;
        gbcIzquierda.weighty = 1;
        gbcIzquierda.insets = new Insets(0, 0, 0, 0);
        columnaIzquierda.add(crearTarjetaDetalle(), gbcIzquierda);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        contenido.add(columnaIzquierda, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.36;
        gbc.insets = new Insets(0, 20, 0, 0);
        contenido.add(crearTarjetaPrecio(), gbc);
        return contenido;
    }

    private JPanel crearTarjetaCaptura() {
        RoundedPanel tarjeta = crearTarjetaBase();
        tarjeta.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 6, 0);
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tarjeta.add(crearEtiquetaSeccion("Código de barras"), gbc);

        codigoBarras = crearCampoEntrada(30);
        codigoBarras.setToolTipText("Escanea o escribe el código y presiona Enter");
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 14, 0);
        tarjeta.add(codigoBarras, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 6, 0);
        tarjeta.add(crearEtiquetaSeccion("Información adicional"), gbc);

        informacion = crearCampoEntrada(20);
        informacion.setToolTipText("Texto libre que se guarda para la etiqueta");
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        tarjeta.add(informacion, gbc);

        return tarjeta;
    }

    private JPanel crearTarjetaDetalle() {
        RoundedPanel tarjeta = crearTarjetaBase();
        tarjeta.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 12, 0);

        descripcion = crearCampoSalida(2, 22);
        presentacion = crearCampoSalida(1, 18);
        formato = crearCampoSalida(1, 18);

        tarjeta.add(crearBloqueDetalle("Descripción", descripcion), gbc);
        gbc.gridy++;
        tarjeta.add(crearBloqueDetalle("Presentación", presentacion), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        tarjeta.add(crearBloqueDetalle("Existencia", formato), gbc);

        return tarjeta;
    }

    private JPanel crearTarjetaPrecio() {
        RoundedPanel tarjeta = crearTarjetaBase();
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setPreferredSize(new Dimension(300, 0));

        JLabel etiquetaPrecio = crearEtiquetaSeccion("Precio final");

        precio = new JLabel("$ 0");
        precio.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 42));
        precio.setForeground(COLOR_AZUL);
        precio.setBorder(new EmptyBorder(4, 0, 10, 0));

        etiquetaAccesos = new JLabel("<html><div style='line-height:1.6;'>"
                + "<b>Accesos rápidos</b><br>"
                + "Enter: Consultar producto<br>"
                + "Ctrl + F7: Buscar productos<br>"
                + "Ctrl + F8: Imprimir etiqueta<br>"
                + "Ctrl + F9: Configuración<br>"
                + "Esc: Salir o cerrar vista previa"
                + "</div></html>");
        etiquetaAccesos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        etiquetaAccesos.setForeground(COLOR_TEXTO);
        etiquetaAccesos.setBorder(new EmptyBorder(4, 0, 10, 0));

        etiquetaTotales = new JLabel("Esperando consulta");
        etiquetaTotales.setOpaque(true);
        etiquetaTotales.setBackground(COLOR_AZUL_SUAVE);
        etiquetaTotales.setForeground(COLOR_AZUL);
        etiquetaTotales.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        etiquetaTotales.setBorder(new EmptyBorder(10, 12, 10, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        tarjeta.add(etiquetaPrecio, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        tarjeta.add(precio, gbc);

        gbc.gridy = 2;
        tarjeta.add(etiquetaAccesos, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel separadorFlexible = new JPanel();
        separadorFlexible.setOpaque(false);
        tarjeta.add(separadorFlexible, gbc);

        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        tarjeta.add(etiquetaTotales, gbc);
        return tarjeta;
    }

    private JPanel crearFooter() {
        RoundedPanel footer = crearTarjetaBase();
        footer.setLayout(new GridBagLayout());

        noEncontrado = new JTextArea();
        noEncontrado.setEditable(false);
        noEncontrado.setFocusable(false);
        noEncontrado.setOpaque(true);
        noEncontrado.setLineWrap(true);
        noEncontrado.setWrapStyleWord(true);
        noEncontrado.setRows(1);
        noEncontrado.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        noEncontrado.setBorder(new EmptyBorder(14, 16, 14, 16));
        mostrarEstadoNeutral("Listo para consultar productos");

        JLabel ayuda = new JLabel("<html><div style='text-align:right; line-height:1.5;'>"
                + "La ventana se adapta al tamaño disponible y mantiene visibles los datos clave.<br>"
                + "Usa la búsqueda modal para localizar productos sin perder contexto."
                + "</div></html>");
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ayuda.setForeground(COLOR_TEXTO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 12);
        footer.add(noEncontrado, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.42;
        gbc.insets = new Insets(0, 0, 0, 0);
        footer.add(ayuda, gbc);
        return footer;
    }

    private RoundedPanel crearTarjetaBase() {
        RoundedPanel tarjeta = new RoundedPanel(28, COLOR_TARJETA);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 28),
                new EmptyBorder(18, 20, 18, 20)));
        return tarjeta;
    }

    private JLabel crearEtiquetaSeccion(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
        etiqueta.setForeground(COLOR_TITULO);
        return etiqueta;
    }

    private JTextField crearCampoEntrada(int tamanoFuente) {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, tamanoFuente));
        campo.setForeground(COLOR_TITULO);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(COLOR_TITULO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 18),
                new EmptyBorder(12, 14, 12, 14)));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        return campo;
    }

    private JTextArea crearCampoSalida(int filas, int tamanoFuente) {
        JTextArea area = new JTextArea(filas, 20);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setForeground(COLOR_TEXTO);
        area.setFont(new Font("Segoe UI", Font.PLAIN, tamanoFuente));
        area.setText("-");
        return area;
    }

    private JPanel crearBloqueDetalle(String titulo, JTextArea area) {
        JPanel bloque = new JPanel();
        bloque.setOpaque(false);
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));

        JLabel etiqueta = crearEtiquetaSeccion(titulo);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);

        bloque.add(etiqueta);
        bloque.add(Box.createVerticalStrut(4));
        bloque.add(area);
        return bloque;
    }

    private JButton crearBoton(String texto, Color fondo, Color textoColor) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        boton.setForeground(textoColor);
        boton.setBackground(fondo);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(fondo.darker(), 18),
                new EmptyBorder(8, 16, 8, 16)));
        boton.setContentAreaFilled(true);
        return boton;
    }

    private void configurarAcciones() {
        botonBuscar.addActionListener(e -> abrirBusqueda());
        botonImprimir.addActionListener(e -> imprimirEtiqueta());
        botonConfiguracion.addActionListener(e -> abrirConfiguracion());

        codigoBarras.addActionListener(e -> consultarArticulo());
        informacion.addActionListener(e -> consultarArticulo());
        informacion.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                Configuracion.guardarInformacionEnArchivo(informacion.getText());
            }
        });

        getRootPane().registerKeyboardAction(e -> Close(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, KeyEvent.CTRL_DOWN_MASK), "buscarProductos");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, KeyEvent.CTRL_DOWN_MASK), "imprimirEtiqueta");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, KeyEvent.CTRL_DOWN_MASK), "abrirConfiguracion");

        getRootPane().getActionMap().put("buscarProductos", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                abrirBusqueda();
            }
        });
        getRootPane().getActionMap().put("imprimirEtiqueta", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                imprimirEtiqueta();
            }
        });
        getRootPane().getActionMap().put("abrirConfiguracion", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                abrirConfiguracion();
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                Close();
            }
        });
    }

    private void abrirBusqueda() {
        BusquedaDialog dialogo = new BusquedaDialog(this, true);
        dialogo.setVisible(true);
    }

    private void abrirConfiguracion() {
        ConfiguracionWindow configuracionWindow = new ConfiguracionWindow(this, true);
        configuracionWindow.setVisible(true);
    }

    private void consultarArticulo() {
        String codigo = codigoBarras.getText().trim();
        if (codigo.isEmpty()) {
            limpiarDatosArticulo();
            mostrarEstadoNeutral("Escribe o escanea un código para consultar");
            return;
        }

        SQL.SQLArticulo.buscarArticuloPorCodigoBarra(codigo, String.valueOf(8));
        if (SQL.SQLArticulo.controlConsulta) {
            actualizarDesdeArticuloActual();
            mostrarEstadoExito("Producto encontrado");
        } else {
            limpiarDatosArticulo();
            mostrarEstadoError("Producto no encontrado");
            ToastNotification.showWarning(this, "Producto no encontrado", 2000);
        }
        codigoBarras.selectAll();
    }

    public void cargarArticuloDesdeBusqueda(String codigo) {
        codigoBarras.setText(codigo);
        consultarArticulo();
        SwingUtilities.invokeLater(() -> codigoBarras.requestFocusInWindow());
    }

    public void aplicarConfiguracionActual() {
        Configuracion.leerArchivoDePropiedades();
        informacion.setText(Configuracion.informacion);
        mostrarEstadoNeutral("Configuración actualizada");
        SwingUtilities.invokeLater(() -> codigoBarras.requestFocusInWindow());
    }

    private void actualizarDesdeArticuloActual() {
        double precioVentaIva = Double.parseDouble(SQL.SQLArticulo.precio_venta_iva);
        String precioFormateado = formatearPrecio(precioVentaIva);
        actualizarDatosArticulo(SQL.SQLArticulo.descripcion, SQL.SQLArticulo.presentacion,
                SQL.SQLArticulo.formato, precioFormateado);
    }

    public static String formatearPrecio(double precioValor) {
        return PrecioFormatter.formatearPrecio(precioValor);
    }

    public final void actualizarDatosArticulo(String descripcionTexto, String presentacionTexto, String existenciaTexto, String precioTexto) {
        descripcion.setText(valorVisible(descripcionTexto));
        presentacion.setText(valorVisible(presentacionTexto));
        formato.setText(valorVisible(existenciaTexto));
        precio.setText(valorVisible(precioTexto));
        etiquetaTotales.setText("Último precio consultado: " + valorVisible(precioTexto));
    }

    public final void limpiarDatosArticulo() {
        descripcion.setText("-");
        presentacion.setText("-");
        formato.setText("-");
        precio.setText("$ 0");
        etiquetaTotales.setText("Esperando consulta");
    }

    private String valorVisible(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "-";
        }
        return valor.trim();
    }

    private void mostrarEstadoNeutral(String mensaje) {
        noEncontrado.setBackground(COLOR_AZUL_SUAVE);
        noEncontrado.setForeground(COLOR_AZUL);
        noEncontrado.setText(mensaje);
    }

    private void mostrarEstadoExito(String mensaje) {
        noEncontrado.setBackground(COLOR_VERDE_SUAVE);
        noEncontrado.setForeground(COLOR_VERDE_TEXTO);
        noEncontrado.setText(mensaje);
    }

    private void mostrarEstadoError(String mensaje) {
        noEncontrado.setBackground(COLOR_ROJO_SUAVE);
        noEncontrado.setForeground(COLOR_ROJO_TEXTO);
        noEncontrado.setText(mensaje);
    }

    private void imprimirEtiqueta() {
        String rutaReporte = "\\reportes\\EtiquetaPrecio.jasper";
        try {
            String url = System.getProperty("user.dir") + rutaReporte;
            ArrayList<DatosReporte> parametros = new ArrayList<DatosReporte>();
            parametros.add(new DatosReporte(SQL.SQLArticulo.codigo_barras,
                    SQL.SQLArticulo.identificacion,
                    descripcion.getText(),
                    precio.getText(),
                    obtenerFechayHoraActualDelServidor(),
                    informacion.getText()));

            JRDataSource dataSource = new JRBeanCollectionDataSource(parametros);
            JasperPrint informe = JasperFillManager.fillReport(url, null, dataSource);

            if (Configuracion.ambiente.equalsIgnoreCase("a")) {
                mostrarVistaPreviaJasper(informe);
            } else if (Configuracion.ambiente.equalsIgnoreCase("b")) {
                PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
                PrintService impresoraSeleccionada = null;

                for (PrintService service : services) {
                    if (service.getName().equalsIgnoreCase(Configuracion.impresora)) {
                        impresoraSeleccionada = service;
                        break;
                    }
                }

                if (impresoraSeleccionada != null) {
                    JRPrintServiceExporter exportador = new JRPrintServiceExporter();
                    exportador.setExporterInput(new SimpleExporterInput(informe));

                    SimplePrintServiceExporterConfiguration config = new SimplePrintServiceExporterConfiguration();
                    config.setPrintService(impresoraSeleccionada);
                    config.setPrintRequestAttributeSet(new javax.print.attribute.HashPrintRequestAttributeSet());
                    config.setDisplayPageDialog(false);
                    config.setDisplayPrintDialog(false);

                    exportador.setConfiguration(config);
                    exportador.exportReport();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró la impresora: " + Configuracion.impresora);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Configurar ambiente: " + Configuracion.ambiente);
            }
        } catch (JRException ex) {
            ex.printStackTrace();
            ToastNotification.showError(this, "No se pudo generar la etiqueta", 2200);
        }
    }

    private void mostrarVistaPreviaJasper(JasperPrint informe) {
        JasperViewer viewer = new JasperViewer(informe, false);
        viewer.setTitle("Vista previa de etiqueta");
        viewer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        viewer.getRootPane().registerKeyboardAction(e -> viewer.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        viewer.setVisible(true);
    }

    private void Close() {
        java.awt.Toolkit.getDefaultToolkit().beep();
        if (JOptionPane.showConfirmDialog(this,
                "¿Desea salir del sistema?", "Sistema", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VentanaInicio().setVisible(true);
            }
        });
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
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
