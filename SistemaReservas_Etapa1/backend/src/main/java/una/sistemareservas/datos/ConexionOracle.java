package una.sistemareservas.datos;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConexionOracle {

    private static final String URL =
            "jdbc:oracle:thin:@localhost:1521:xe";

    // Por defecto usa el usuario "reservas"; se puede cambiar con la
    // variable de entorno RESERVAS_DB_USER.
    private static final String USUARIO =
            System.getenv("RESERVAS_DB_USER") != null
                    ? System.getenv("RESERVAS_DB_USER")
                    : "reservas";

    private static final String CLAVE =
            System.getenv("RESERVAS_DB_PASSWORD");

    // Pool de conexiones: se crea una sola vez y se reutiliza
    private static volatile HikariDataSource pool;

    public static Connection conectar() throws SQLException {

        if (CLAVE == null || CLAVE.isBlank()) {
            throw new SQLException(
                    "Falta configurar RESERVAS_DB_PASSWORD"
            );
        }

        return obtenerPool().getConnection();
    }

    private static HikariDataSource obtenerPool() {

        if (pool == null) {
            synchronized (ConexionOracle.class) {
                if (pool == null) {

                    HikariConfig config = new HikariConfig();
                    config.setJdbcUrl(URL);
                    config.setUsername(USUARIO);
                    config.setPassword(CLAVE);
                    config.setMaximumPoolSize(10);
                    config.setMinimumIdle(2);
                    config.setConnectionTimeout(15000);

                    pool = new HikariDataSource(config);
                }
            }
        }

        return pool;
    }
}