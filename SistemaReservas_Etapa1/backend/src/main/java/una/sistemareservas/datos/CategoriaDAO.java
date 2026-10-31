
package una.sistemareservas.datos;

import una.sistemareservas.modelo.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    // Carga todas las categorias desde Oracle
    public List<Categoria> cargar() {

        List<Categoria> categorias = new ArrayList<>();

        String sql = "SELECT ID, DESCRIPCION FROM CATEGORIAS ORDER BY ID";

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getString("ID"),
                        rs.getString("DESCRIPCION")
                );

                categorias.add(categoria);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al cargar categorias desde Oracle", e
            );
        }

        return categorias;
    }

    // Guarda una lista de categorias en Oracle.
    // Inserta las nuevas y actualiza las existentes.
    public void guardar(List<Categoria> categorias) {

        String sql = """
                MERGE INTO CATEGORIAS c
                USING (
                    SELECT ? AS ID, ? AS DESCRIPCION
                    FROM DUAL
                ) datos
                ON (c.ID = datos.ID)
                WHEN MATCHED THEN
                    UPDATE SET c.DESCRIPCION = datos.DESCRIPCION
                WHEN NOT MATCHED THEN
                    INSERT (ID, DESCRIPCION)
                    VALUES (datos.ID, datos.DESCRIPCION)
                """;

        try (Connection conexion = ConexionOracle.conectar()) {

            conexion.setAutoCommit(false);

            try (PreparedStatement ps =
                         conexion.prepareStatement(sql)) {

                for (Categoria categoria : categorias) {
                    ps.setString(1, categoria.getId());
                    ps.setString(2, categoria.getDescripcion());
                    ps.executeUpdate();
                }

                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al guardar categorias en Oracle", e
            );
        }
    }

    // Agrega una nueva categoria
    public List<Categoria> agregar(String descripcion) {

        String sql = """
                INSERT INTO CATEGORIAS (ID, DESCRIPCION)
                VALUES (?, ?)
                """;

        try (Connection conexion = ConexionOracle.conectar()) {

            // Bloquea la tabla mientras genera el siguiente ID.
            // Evita IDs duplicados entre clientes simultaneos.
            conexion.setAutoCommit(false);

            try (Statement bloqueo = conexion.createStatement()) {
                bloqueo.execute("LOCK TABLE CATEGORIAS IN EXCLUSIVE MODE");
            }

            String nuevoId = generarSiguienteId(conexion);

            try (PreparedStatement ps =
                         conexion.prepareStatement(sql)) {

                ps.setString(1, nuevoId);
                ps.setString(2, descripcion);
                ps.executeUpdate();

                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al agregar categoria en Oracle", e
            );
        }

        return cargar();
    }

    // Modifica la descripcion
    public boolean modificar(String id, String nuevaDescripcion) {

        String sql = """
                UPDATE CATEGORIAS
                SET DESCRIPCION = ?
                WHERE UPPER(ID) = UPPER(?)
                """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nuevaDescripcion);
            ps.setString(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al modificar categoria en Oracle", e
            );
        }
    }

    // Elimina una categoria
    public boolean eliminar(String id) {

        String sql = """
                DELETE FROM CATEGORIAS
                WHERE UPPER(ID) = UPPER(?)
                """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al eliminar categoria en Oracle", e
            );
        }
    }

    // Busca una categoria por ID
    public Categoria buscarPorId(String id) {

        String sql = """
                SELECT ID, DESCRIPCION
                FROM CATEGORIAS
                WHERE UPPER(ID) = UPPER(?)
                """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Categoria(
                            rs.getString("ID"),
                            rs.getString("DESCRIPCION")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al buscar categoria en Oracle", e
            );
        }

        return null;
    }

    // Genera IDs como CAT-000001, CAT-000002...
    private String generarSiguienteId(Connection conexion)
            throws SQLException {

        String sql = """
                SELECT NVL(MAX(
                    CASE
                        WHEN REGEXP_LIKE(ID, '^CAT-[0-9]{6}$')
                        THEN TO_NUMBER(SUBSTR(ID, 5))
                    END
                ), 0) + 1 AS SIGUIENTE
                FROM CATEGORIAS
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return String.format(
                        "CAT-%06d",
                        rs.getInt("SIGUIENTE")
                );
            }
        }

        throw new SQLException("No se pudo generar el ID");
    }
}
