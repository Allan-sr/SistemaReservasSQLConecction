
package una.sistemareservas.datos;

import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Recurso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {

    // Cargar todos los recursos desde Oracle
    public List<Recurso> cargar() {

        List<Recurso> recursos = new ArrayList<>();

        String sql = """
                SELECT r.ID,
                       r.CATEGORIA_ID,
                       r.DESCRIPCION,
                       c.DESCRIPCION AS NOMBRE_CATEGORIA
                FROM RECURSOS r
                INNER JOIN CATEGORIAS c
                    ON r.CATEGORIA_ID = c.ID
                ORDER BY r.ID
                """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Recurso recurso = construirRecurso(rs);
                recursos.add(recurso);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al cargar recursos desde Oracle", e
            );
        }

        return recursos;
    }

    // Construir el objeto Recurso con su Categoria

    private Recurso construirRecurso(ResultSet rs)
            throws SQLException {

        String id = rs.getString("ID");
        String categoriaId = rs.getString("CATEGORIA_ID");
        String descripcion = rs.getString("DESCRIPCION");
        String nombreCategoria = rs.getString("NOMBRE_CATEGORIA");

        // Construimos la categoria
        Categoria categoria = new Categoria(
                categoriaId,
                nombreCategoria
        );

        // Construimos el recurso usando su constructor
        Recurso recurso = new Recurso(
                id,
                categoria,
                descripcion
        );

        return recurso;
    }


    // Buscar un recurso por su identificador
    public Recurso buscarPorId(String id) {

        String sql = """
                SELECT r.ID,
                       r.CATEGORIA_ID,
                       r.DESCRIPCION,
                       c.DESCRIPCION AS NOMBRE_CATEGORIA
                FROM RECURSOS r
                INNER JOIN CATEGORIAS c
                    ON r.CATEGORIA_ID = c.ID
                WHERE UPPER(r.ID) = UPPER(?)
                """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return construirRecurso(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al buscar recurso en Oracle", e
            );
        }

        return null;
    }

    // Agregar un nuevo recurso
    public void agregar(Recurso nuevoRecurso) {

        String sql = """
                INSERT INTO RECURSOS
                    (ID, CATEGORIA_ID, DESCRIPCION)
                VALUES (?, ?, ?)
                """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nuevoRecurso.getId());
            ps.setString(2, nuevoRecurso.getCategoriaId());
            ps.setString(3, nuevoRecurso.getDescripcion());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al agregar recurso en Oracle", e
            );
        }
    }

    // Modificar un recurso existente
    public boolean modificar(Recurso recursoModificado) {

        String sql = """
                UPDATE RECURSOS
                SET CATEGORIA_ID = ?,
                    DESCRIPCION = ?
                WHERE UPPER(ID) = UPPER(?)
                """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, recursoModificado.getCategoriaId());
            ps.setString(2, recursoModificado.getDescripcion());
            ps.setString(3, recursoModificado.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al modificar recurso en Oracle", e
            );
        }
    }

    // Eliminar un recurso
    public boolean eliminar(String id) {

        String sql = """
                DELETE FROM RECURSOS
                WHERE UPPER(ID) = UPPER(?)
                """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al eliminar recurso en Oracle", e
            );
        }
    }

    // Guardar una lista de recursos
    public void guardar(List<Recurso> recursos) {

        String sql = """
                MERGE INTO RECURSOS r
                USING (
                    SELECT ? AS ID,
                           ? AS CATEGORIA_ID,
                           ? AS DESCRIPCION
                    FROM DUAL
                ) datos
                ON (r.ID = datos.ID)
                WHEN MATCHED THEN
                    UPDATE SET
                        r.CATEGORIA_ID = datos.CATEGORIA_ID,
                        r.DESCRIPCION = datos.DESCRIPCION
                WHEN NOT MATCHED THEN
                    INSERT (ID, CATEGORIA_ID, DESCRIPCION)
                    VALUES (
                        datos.ID,
                        datos.CATEGORIA_ID,
                        datos.DESCRIPCION
                    )
                """;

        try (Connection conexion = ConexionOracle.conectar()) {

            conexion.setAutoCommit(false);

            try (PreparedStatement ps =
                         conexion.prepareStatement(sql)) {

                for (Recurso recurso : recursos) {

                    ps.setString(1, recurso.getId());
                    ps.setString(2, recurso.getCategoriaId());
                    ps.setString(3, recurso.getDescripcion());

                    ps.executeUpdate();
                }

                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al guardar recursos en Oracle", e
            );
        }
    }
}
