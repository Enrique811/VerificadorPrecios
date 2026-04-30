package App;

import Conexion.Conexion;
import Metodos.ConfigManager;
import Ventanas.VentanaConfiguracion;
import Ventanas.VentanaInicio;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciarAplicacion);
    }

    public static void iniciarAplicacion() {
        ConfigManager config = new ConfigManager();

        if (!config.exists() || !config.configCompleta()) {
            abrirVentanaConfiguracion();
            return;
        }

        final JDialog loadingDialog = createLoadingDialog();
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return Conexion.probarConexion(config);
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                boolean conexionExitosa = false;
                try {
                    conexionExitosa = get().booleanValue();
                } catch (Exception ex) {
                    conexionExitosa = false;
                }

                if (conexionExitosa) {
                    abrirVentanaInicio();
                    return;
                }

                JOptionPane.showMessageDialog(null,
                        "No fue posible conectar con la base de datos.\n"
                        + Conexion.getUltimoErrorConexion()
                        + "\nRevise la configuracion e intente nuevamente.");
                abrirVentanaConfiguracion();
            }
        };
        worker.execute();
        loadingDialog.setVisible(true);
    }

    public static void abrirVentanaInicio() {
        new VentanaInicio().setVisible(true);
    }

    public static void abrirVentanaConfiguracion() {
        new VentanaConfiguracion().setVisible(true);
    }

    private static JDialog createLoadingDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("VerificadorPrecios");
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.add(new JLabel("Validando configuracion y conexion...", JLabel.CENTER));
        dialog.setSize(320, 110);
        dialog.setLocationRelativeTo(null);
        return dialog;
    }
}
