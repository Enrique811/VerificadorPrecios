package Ventanas;

import Metodos.Articulos;
import Metodos.PrecioFormatter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class BusquedaDialog extends JDialog {

    public static final String[] TITULO = {"C\u00f3digo barras", "Descripci\u00f3n", "Precio", "Existencia"};

    public static DefaultTableModel modelo = new DefaultTableModel(new Object[][]{}, TITULO) {
        @Override
        public boolean isCellEditable(int rowIndex, int colIndex) {
            return false;
        }
    };

    public static JTextField Nombre;
    public static JTable listaArticulos;
    public static JLabel etiquetaMostrarFormaDeBusqueda;

    private static final Color COLOR_OVERLAY = new Color(0, 0, 0, 100);
    private static final Color COLOR_FONDO = new Color(248, 250, 252);
    private static final Color COLOR_BORDE = new Color(218, 223, 230);
    private static final Color COLOR_TITULO = new Color(31, 41, 55);
    private static final Color COLOR_TEXTO = new Color(107, 114, 128);
    private static final Color COLOR_AZUL = new Color(45, 111, 214);
    private static final Color COLOR_AZUL_SUAVE = new Color(239, 246, 255);

    private JPanel overlay;
    private ComponentAdapter overlayResizeListener;
    private final VentanaInicio ownerFrame;

    public BusquedaDialog(java.awt.Frame owner, boolean modal) {
        super(owner, modal);
        this.ownerFrame = owner instanceof VentanaInicio ? (VentanaInicio) owner : null;
        initComponents();
        configurarDialogo();
    }

    private void initComponents() {
        setTitle("Buscar productos");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setPreferredSize(new Dimension(920, 580));

        RoundedPanel panelPrincipal = new RoundedPanel(30, Color.WHITE);
        panelPrincipal.setLayout(new BorderLayout(18, 18));
        panelPrincipal.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(),
                new EmptyBorder(24, 24, 24, 24)));

        panelPrincipal.add(crearEncabezado(), BorderLayout.NORTH);
        panelPrincipal.add(crearTabla(), BorderLayout.CENTER);
        panelPrincipal.add(crearPie(), BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
        pack();
    }

    private JPanel crearEncabezado() {
        RoundedPanel tarjeta = crearTarjetaSeccion();
        tarjeta.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);

        JLabel titulo = new JLabel("B\u00fasqueda de productos");
        titulo.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 24));
        titulo.setForeground(COLOR_TITULO);
        tarjeta.add(titulo, gbc);

        gbc.gridy++;
        etiquetaMostrarFormaDeBusqueda = new JLabel("Descripci\u00f3n");
        etiquetaMostrarFormaDeBusqueda.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        etiquetaMostrarFormaDeBusqueda.setForeground(COLOR_TEXTO);
        tarjeta.add(etiquetaMostrarFormaDeBusqueda, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        Nombre = new JTextField();
        Nombre.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        Nombre.setForeground(COLOR_TITULO);
        Nombre.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 18),
                new EmptyBorder(14, 16, 14, 16)));
        tarjeta.add(Nombre, gbc);

        return tarjeta;
    }

    private JComponent crearTabla() {
        listaArticulos = new JTable(modelo);
        listaArticulos.setDefaultRenderer(Object.class, new Metodos.FormatoTablaBArt());
        listaArticulos.setRowHeight(34);
        listaArticulos.setGridColor(new Color(229, 231, 235));
        listaArticulos.setSelectionBackground(COLOR_AZUL_SUAVE);
        listaArticulos.setSelectionForeground(COLOR_TITULO);
        listaArticulos.setShowVerticalLines(false);
        listaArticulos.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        listaArticulos.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(listaArticulos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(Color.WHITE);

        RoundedPanel tarjeta = crearTarjetaSeccion();
        tarjeta.setLayout(new BorderLayout());
        tarjeta.add(scroll, BorderLayout.CENTER);
        return tarjeta;
    }

    private JPanel crearPie() {
        RoundedPanel tarjeta = crearTarjetaSeccion();
        tarjeta.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));

        tarjeta.add(crearEtiquetaAyuda("ESC", "Cerrar"));
        tarjeta.add(crearEtiquetaAyuda("ENTER", "Seleccionar"));
        tarjeta.add(crearEtiquetaAyuda("\u2191 \u2193 ", "Moverse"));
        return tarjeta;
    }

    private RoundedPanel crearTarjetaSeccion() {
        RoundedPanel tarjeta = new RoundedPanel(24, Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE, 24),
                new EmptyBorder(18, 18, 18, 18)));
        return tarjeta;
    }

    private JPanel crearEtiquetaAyuda(String atajo, String texto) {
        JPanel ayuda = new JPanel();
        ayuda.setOpaque(true);
        ayuda.setBackground(COLOR_AZUL_SUAVE);
        ayuda.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(191, 219, 254), 16),
                new EmptyBorder(8, 12, 8, 12)));
        ayuda.setLayout(new BoxLayout(ayuda, BoxLayout.X_AXIS));

        JLabel etiquetaAtajo = new JLabel(atajo);
        etiquetaAtajo.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        etiquetaAtajo.setForeground(COLOR_AZUL);

        JLabel etiquetaTexto = new JLabel(" " + texto);
        etiquetaTexto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        etiquetaTexto.setForeground(COLOR_TITULO);

        ayuda.add(etiquetaAtajo);
        ayuda.add(etiquetaTexto);
        return ayuda;
    }

    private void configurarDialogo() {
        vacioArt();

        Nombre.addActionListener(e -> buscarArticulos());
        Nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarArticulos();
                }
            }
        });

        listaArticulos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    seleccionarArticulo();
                }
            }
        });
        listaArticulos.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    evt.consume();
                    seleccionarArticulo();
                }
            }
        });

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrar");
        getRootPane().getActionMap().put("cerrar", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                Nombre.requestFocusInWindow();
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                ocultarOverlay();
            }
        });
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            mostrarOverlay();
            if (ownerFrame != null) {
                setLocationRelativeTo(ownerFrame);
            } else {
                setLocationRelativeTo(getOwner());
            }
        } else {
            ocultarOverlay();
        }
        super.setVisible(b);
    }

    private void mostrarOverlay() {
        if (ownerFrame == null || overlay != null) {
            return;
        }

        overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(COLOR_OVERLAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, ownerFrame.getWidth(), ownerFrame.getHeight());

        overlayResizeListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (overlay != null) {
                    overlay.setBounds(0, 0, ownerFrame.getWidth(), ownerFrame.getHeight());
                    overlay.revalidate();
                    overlay.repaint();
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                if (isShowing()) {
                    setLocationRelativeTo(ownerFrame);
                }
            }
        };

        ownerFrame.getLayeredPane().add(overlay, javax.swing.JLayeredPane.MODAL_LAYER);
        ownerFrame.addComponentListener(overlayResizeListener);
        ownerFrame.getLayeredPane().repaint();
    }

    private void ocultarOverlay() {
        if (ownerFrame != null && overlay != null) {
            ownerFrame.getLayeredPane().remove(overlay);
            ownerFrame.getLayeredPane().revalidate();
            ownerFrame.getLayeredPane().repaint();
            if (overlayResizeListener != null) {
                ownerFrame.removeComponentListener(overlayResizeListener);
            }
        }
        overlay = null;
        overlayResizeListener = null;
    }

    private void buscarArticulos() {
        Object[] fila = new Object[4];
        limpiarTablaArticulo();
        ArrayList<Articulos> listaArt = new ArrayList<Articulos>(SQL.SQLArticulo.buscarArticuloPorDescripcion(Nombre.getText()));

        for (int x = 0; x < listaArt.size(); x++) {
            fila[0] = listaArt.get(x).getCodigoBarras();
            fila[1] = listaArt.get(x).getDescripcion();
            fila[2] = PrecioFormatter.formatearPrecio(Double.parseDouble(listaArt.get(x).getPrecioIva()));
            fila[3] = listaArt.get(x).getStock();
            modelo.addRow(fila);
        }

        if (modelo.getRowCount() > 0) {
            listaArticulos.setRowSelectionInterval(0, 0);
            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
        } else {
            vacioArt();
            ToastNotification.showWarning(SwingUtilities.getWindowAncestor(this), "No se encontraron productos", 1800);
        }
    }

    private void seleccionarArticulo() {
        int selectedRow = listaArticulos.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        Object valor = listaArticulos.getValueAt(selectedRow, 0);
        if (valor == null) {
            return;
        }

        if (ownerFrame != null) {
            ownerFrame.cargarArticuloDesdeBusqueda(valor.toString());
        }
        dispose();
    }

    public static void vacioArt() {
        limpiarTablaArticulo();
        modelo.addRow(new Object[]{"", "", "", ""});
        listaArticulos.setModel(modelo);
    }

    public static void limpiarTablaArticulo() {
        while (modelo.getRowCount() > 0) {
            modelo.removeRow(0);
        }
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

    private static final class ShadowBorder extends AbstractBorder {

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(15, 23, 42, 18));
            g2.fillRoundRect(x + 2, y + 4, width - 4, height - 4, 30, 30);
            g2.setColor(COLOR_BORDE);
            g2.drawRoundRect(x, y, width - 1, height - 1, 30, 30);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 8, 8, 8);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = 8;
            insets.right = 8;
            insets.top = 8;
            insets.bottom = 8;
            return insets;
        }
    }
}
