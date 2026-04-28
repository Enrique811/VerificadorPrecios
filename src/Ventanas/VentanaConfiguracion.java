package Ventanas;

import App.Main;
import Conexion.Conexion;
import Metodos.ConfigManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class VentanaConfiguracion extends JFrame {

    private JTextField campoIpEmpresa;
    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private JTextField campoRutaEmpresa;
    private JButton botonRutaEmpresa;

    public VentanaConfiguracion() {
        initComponents();
        cargarConfiguracionActual();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("VentanaConfiguracion");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(520, 300));

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("Configuracion de conexion", SwingConstants.LEFT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        root.add(titulo, BorderLayout.NORTH);
        root.add(crearFormulario(), BorderLayout.CENTER);
        root.add(crearFooter(), BorderLayout.SOUTH);

        setContentPane(root);
        pack();
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        panel.add(new JLabel("ipEmpresa"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("usuario"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("password"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("rutaEmpresa"), gbc);

        campoIpEmpresa = new JTextField(28);
        campoUsuario = new JTextField(28);
        campoPassword = new JPasswordField(28);
        campoRutaEmpresa = new JTextField(28);
        botonRutaEmpresa = new JButton("Seleccionar");
        botonRutaEmpresa.addActionListener(e -> seleccionarRutaEmpresa());

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        panel.add(campoIpEmpresa, gbc);
        gbc.gridy++;
        panel.add(campoUsuario, gbc);
        gbc.gridy++;
        panel.add(campoPassword, gbc);
        gbc.gridy++;
        JPanel contenedorRuta = new JPanel(new BorderLayout(8, 0));
        contenedorRuta.add(campoRutaEmpresa, BorderLayout.CENTER);
        contenedorRuta.add(botonRutaEmpresa, BorderLayout.EAST);
        panel.add(contenedorRuta, gbc);

        return panel;
    }

    private JPanel crearFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(e -> guardarConfiguracion());
        panel.add(botonGuardar, BorderLayout.EAST);
        return panel;
    }

    private void cargarConfiguracionActual() {
        ConfigManager config = new ConfigManager();
        campoIpEmpresa.setText(config.get("ipEmpresa"));
        campoUsuario.setText(config.get("usuario"));
        campoPassword.setText(config.get("password"));
        campoRutaEmpresa.setText(config.get("rutaEmpresa"));
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

        ConfigManager config = new ConfigManager();
        config.set("ipEmpresa", ipEmpresa);
        config.set("usuario", usuario);
        config.set("password", password);
        config.set("rutaEmpresa", rutaEmpresa);

        try {
            config.save();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la configuracion.\n" + ex.getMessage(),
                    "Configuracion",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (Conexion.probarConexion(config)) {
            JOptionPane.showMessageDialog(this,
                    "Conexion exitosa. Se abrira la ventana principal.",
                    "Configuracion",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            Main.abrirVentanaInicio();
            return;
        }

        JOptionPane.showMessageDialog(this,
                "La conexion fallo. Verifique ipEmpresa, usuario, password y rutaEmpresa.",
                "Configuracion",
                JOptionPane.ERROR_MESSAGE);
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
}
