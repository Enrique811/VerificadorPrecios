package App;

import Conexion.Conexion;
import Metodos.ConfigManager;
import Ventanas.VentanaConfiguracion;
import Ventanas.VentanaInicio;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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

        if (Conexion.probarConexion(config)) {
            abrirVentanaInicio();
            return;
        }

        JOptionPane.showMessageDialog(null,
                "No fue posible conectar con la base de datos. Revise la configuracion.");
        abrirVentanaConfiguracion();
    }

    public static void abrirVentanaInicio() {
        new VentanaInicio().setVisible(true);
    }

    public static void abrirVentanaConfiguracion() {
        new VentanaConfiguracion().setVisible(true);
    }
}
