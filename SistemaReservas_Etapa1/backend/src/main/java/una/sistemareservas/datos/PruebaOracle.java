
package una.sistemareservas.datos;

import java.sql.Connection;
import java.sql.SQLException;

public class PruebaOracle {

    public static void main(String[] args) {

        try (Connection conexion =
                     ConexionOracle.conectar()) {

            System.out.println(
                    "Conexion con Oracle exitosa"
            );

        } catch (SQLException e) {
            System.out.println(
                    "Error: " + e.getMessage()
            );
        }
    }
}
