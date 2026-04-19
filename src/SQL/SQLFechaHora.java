/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQL;

import Conexion.Conexion;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JOptionPane;

/**
 *
 * @author Usuario
 */
public class SQLFechaHora {
        
    public static String obtenerFechayHoraActualDelServidor() {
        String fechaYHora;
        try {
            Conexion.ConectarBDEmpresa();
            Conexion.cerrarRecursosConsulta();
            Conexion.consulta="SELECT CURRENT_TIMESTAMP AS HORAFECHA FROM RDB$DATABASE";
            Conexion.preparacion= Conexion.conexion.prepareStatement(Conexion.consulta);
            Conexion.resultado=Conexion.preparacion.executeQuery();         
            if (Conexion.resultado.next()) {
                fechaYHora = Conexion.resultado.getString("HORAFECHA");
                return fechaYHora.substring(0, 10);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error al consultar fecha del servidor","Error",JOptionPane.ERROR_MESSAGE);
        } finally {
            Conexion.cerrarRecursosConsulta();
        } 
        return null;
    }    
    



    public static Date obtenerFechayHoraActualDelServidorDate() {
        Date fechaYHora = null;
        try {
            Conexion.ConectarBDEmpresa();
            Conexion.cerrarRecursosConsulta();
            Conexion.consulta = "SELECT CURRENT_TIMESTAMP AS HORAFECHA FROM RDB$DATABASE";
            Conexion.preparacion = Conexion.conexion.prepareStatement(Conexion.consulta);
            Conexion.resultado = Conexion.preparacion.executeQuery();
            
            if (Conexion.resultado.next()) {
                Timestamp timestamp = Conexion.resultado.getTimestamp("HORAFECHA");
                fechaYHora = new Date(timestamp.getTime()); // Convertir a java.util.Date
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al consultar fecha del servidor", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Conexion.cerrarRecursosConsulta();
        }
        return fechaYHora;
    }
}

    

