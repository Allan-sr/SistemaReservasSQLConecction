package una.sistemareservas.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionOracle {

    private static final String URL =
            "jdbc:oracle:thin:@localhost:1521:xe";

    private static final String USUARIO = "system";

    private static final String CLAVE =
            System.getenv("RESERVAS_DB_PASSWORD");

    public static Connection conectar()
            throws SQLException {

        if (CLAVE == null || CLAVE.isBlank()) {
            throw new SQLException(
                    "Falta configurar RESERVAS_DB_PASSWORD"
            );
        }

        return DriverManager.getConnection(
                URL, USUARIO, CLAVE
        );
    }
}
