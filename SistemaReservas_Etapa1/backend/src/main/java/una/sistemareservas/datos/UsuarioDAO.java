
package una.sistemareservas.datos;

import una.sistemareservas.modelo.Administrador;
import una.sistemareservas.modelo.Funcionario;
import una.sistemareservas.modelo.Rol;
import una.sistemareservas.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public UsuarioDAO() {
        // Ahora los usuarios se almacenan en Oracle.
    }

    // Convierte una fila de Oracle en un objeto Usuario.
    private Usuario construirUsuario(ResultSet rs)
            throws SQLException {

        String id = rs.getString("ID");
        String clave = rs.getString("CLAVE");
        Rol rol = Rol.valueOf(rs.getString("ROL"));

        if (rol == Rol.ADMINISTRADOR) {
            return new Administrador(id, clave, rol);
        }

        if (rol == Rol.FUNCIONARIO) {
            return new Funcionario(
                    id,
                    clave,
                    rol,
                    rs.getString("NOMBRE"),
                    rs.getString("TELEFONO")
            );
        }

        throw new SQLException("Rol no reconocido: " + rol);
    }

    // Carga todos los usuarios desde Oracle.
    public List<Usuario> cargar() {

        List<Usuario> usuarios = new ArrayList<>();

        String sql = """
                SELECT u.ID, u.CLAVE, u.ROL,
                       f.NOMBRE, f.TELEFONO
                FROM USUARIOS u
                LEFT JOIN FUNCIONARIOS f
                    ON u.ID = f.ID
                ORDER BY u.ID
                """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(construirUsuario(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al cargar usuarios desde Oracle", e
            );
        }

        return usuarios;
    }

    // Busca un usuario por su ID.
    public Usuario buscarPorId(String id) {

        if (id == null || id.isBlank()) {
            return null;
        }

        String sql = """
                SELECT u.ID, u.CLAVE, u.ROL,
                       f.NOMBRE, f.TELEFONO
                FROM USUARIOS u
                LEFT JOIN FUNCIONARIOS f
                    ON u.ID = f.ID
                WHERE UPPER(u.ID) = UPPER(?)
                """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, id.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construirUsuario(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al buscar usuario en Oracle", e
            );
        }

        return null;
    }

    // Agrega un usuario y, si corresponde, su funcionario.
    public boolean agregar(Usuario nuevoUsuario) {

        if (buscarPorId(nuevoUsuario.getId()) != null) {
            return false;
        }

        String sqlUsuario = """
                INSERT INTO USUARIOS (ID, CLAVE, ROL)
                VALUES (?, ?, ?)
                """;

        String sqlFuncionario = """
                INSERT INTO FUNCIONARIOS (ID, NOMBRE, TELEFONO)
                VALUES (?, ?, ?)
                """;

        try (Connection conexion = ConexionOracle.conectar()) {

            conexion.setAutoCommit(false);

            try {
                System.out.println(
                        "GUARDANDO EN ORACLE: " + nuevoUsuario.getId()
                );

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlUsuario)) {

                    ps.setString(1, nuevoUsuario.getId());
                    ps.setString(2, nuevoUsuario.getClave());
                    ps.setString(3, nuevoUsuario.getRol().name());
                    ps.executeUpdate();
                }

                if (nuevoUsuario instanceof Funcionario funcionario) {

                    try (PreparedStatement ps =
                                 conexion.prepareStatement(sqlFuncionario)) {

                        ps.setString(1, funcionario.getId());
                        ps.setString(2, funcionario.getNombre());
                        ps.setString(3, funcionario.getTelefono());
                        ps.executeUpdate();
                    }
                }

                conexion.commit();

                System.out.println(
                        "USUARIO GUARDADO CORRECTAMENTE EN ORACLE"
                );

                return true;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al agregar usuario en Oracle", e
            );
        }
    }

    // Modifica un usuario existente.
    public boolean modificar(Usuario usuarioModificado) {

        String sqlUsuario = """
                UPDATE USUARIOS
                SET CLAVE = ?, ROL = ?
                WHERE UPPER(ID) = UPPER(?)
                """;

        String sqlFuncionario = """
                MERGE INTO FUNCIONARIOS f
                USING (
                    SELECT ? AS ID,
                           ? AS NOMBRE,
                           ? AS TELEFONO
                    FROM DUAL
                ) datos
                ON (f.ID = datos.ID)
                WHEN MATCHED THEN
                    UPDATE SET
                        f.NOMBRE = datos.NOMBRE,
                        f.TELEFONO = datos.TELEFONO
                WHEN NOT MATCHED THEN
                    INSERT (ID, NOMBRE, TELEFONO)
                    VALUES (
                        datos.ID,
                        datos.NOMBRE,
                        datos.TELEFONO
                    )
                """;

        String sqlEliminarFuncionario = """
                DELETE FROM FUNCIONARIOS
                WHERE UPPER(ID) = UPPER(?)
                """;

        try (Connection conexion = ConexionOracle.conectar()) {

            conexion.setAutoCommit(false);

            try {
                int filas;

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlUsuario)) {

                    ps.setString(1, usuarioModificado.getClave());
                    ps.setString(2, usuarioModificado.getRol().name());
                    ps.setString(3, usuarioModificado.getId());

                    filas = ps.executeUpdate();
                }

                if (filas == 0) {
                    conexion.rollback();
                    return false;
                }

                if (usuarioModificado instanceof Funcionario funcionario) {

                    try (PreparedStatement ps =
                                 conexion.prepareStatement(sqlFuncionario)) {

                        ps.setString(1, funcionario.getId());
                        ps.setString(2, funcionario.getNombre());
                        ps.setString(3, funcionario.getTelefono());
                        ps.executeUpdate();
                    }

                } else {

                    try (PreparedStatement ps =
                                 conexion.prepareStatement(sqlEliminarFuncionario)) {

                        ps.setString(1, usuarioModificado.getId());
                        ps.executeUpdate();
                    }
                }

                conexion.commit();
                return true;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al modificar usuario en Oracle", e
            );
        }
    }

    // Elimina un usuario.
    public boolean eliminar(String id) {

        String sqlFuncionario = """
                DELETE FROM FUNCIONARIOS
                WHERE UPPER(ID) = UPPER(?)
                """;

        String sqlUsuario = """
                DELETE FROM USUARIOS
                WHERE UPPER(ID) = UPPER(?)
                """;

        try (Connection conexion = ConexionOracle.conectar()) {

            conexion.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlFuncionario)) {

                    ps.setString(1, id);
                    ps.executeUpdate();
                }

                int filas;

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlUsuario)) {

                    ps.setString(1, id);
                    filas = ps.executeUpdate();
                }

                conexion.commit();
                return filas > 0;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al eliminar usuario en Oracle", e
            );
        }
    }

    // Guarda usuarios existentes o agrega nuevos.
    public void guardar(List<Usuario> usuarios) {

        for (Usuario usuario : usuarios) {

            if (buscarPorId(usuario.getId()) == null) {
                agregar(usuario);
            } else {
                modificar(usuario);
            }
        }
    }
}
