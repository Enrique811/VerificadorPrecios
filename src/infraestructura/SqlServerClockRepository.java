package infraestructura;

import aplicacion.ServerClockRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import javax.swing.JOptionPane;

public final class SqlServerClockRepository implements ServerClockRepository {

    private static final String SERVER_CLOCK_QUERY = "SELECT CURRENT_TIMESTAMP AS HORAFECHA FROM RDB$DATABASE";

    private final FirebirdConnectionFactory connectionFactory = new FirebirdConnectionFactory();

    @Override
    public String obtenerFechaActual() {
        try (Connection connection = connectionFactory.openConnection();
                PreparedStatement statement = connection.prepareStatement(SERVER_CLOCK_QUERY);
                ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                String fechaYHora = result.getString("HORAFECHA");
                return fechaYHora == null || fechaYHora.length() < 10 ? fechaYHora : fechaYHora.substring(0, 10);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al consultar fecha del servidor", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    @Override
    public Date obtenerFechaHoraActual() {
        try (Connection connection = connectionFactory.openConnection();
                PreparedStatement statement = connection.prepareStatement(SERVER_CLOCK_QUERY);
                ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                Timestamp timestamp = result.getTimestamp("HORAFECHA");
                return timestamp == null ? null : new Date(timestamp.getTime());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al consultar fecha del servidor", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
