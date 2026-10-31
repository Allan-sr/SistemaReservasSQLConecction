
package una.sistemareservas.datos;

import una.sistemareservas.modelo.*;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RecursoDAO recursoDAO = new RecursoDAO();

    // Obtener todas las reservas desde Oracle
    public List<Reserva> cargar() {

        List<Reserva> reservas = new ArrayList<>();

        String sql = """
                SELECT ID, ACTIVIDAD, FECHA,
                       HORA_INICIO, HORA_FIN,
                       FUNCIONARIO_ID, ESTADO
                FROM RESERVAS
                ORDER BY FECHA, HORA_INICIO
                """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reservas.add(construirReserva(rs, conexion));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al cargar reservas desde Oracle", e);
        }

        return reservas;
    }

    // Buscar reserva por ID
    public Reserva buscarPorId(String id) {

        String sql = """
                SELECT ID, ACTIVIDAD, FECHA,
                       HORA_INICIO, HORA_FIN,
                       FUNCIONARIO_ID, ESTADO
                FROM RESERVAS
                WHERE ID = ?
                """;

        try (Connection conexion = ConexionOracle.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construirReserva(rs, conexion);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al buscar reserva en Oracle", e);
        }

        return null;
    }

    // Agregar una reserva y sus recursos
    public void agregar(Reserva reserva) {

        String sql = """
                INSERT INTO RESERVAS
                (ID, ACTIVIDAD, FECHA, HORA_INICIO,
                 HORA_FIN, FUNCIONARIO_ID, ESTADO)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionOracle.conectar()) {

            conexion.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                             conexion.prepareStatement(sql)) {

                    establecerParametros(ps, reserva);
                    ps.executeUpdate();
                }

                insertarRecursos(conexion, reserva);

                conexion.commit();

            } catch (Exception e) {
                conexion.rollback();
                throw e;
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al agregar reserva en Oracle", e);
        }
    }

    // Modificar reserva existente
    public boolean modificar(Reserva reserva) {

        String sql = """
                UPDATE RESERVAS
                SET ACTIVIDAD = ?,
                    FECHA = ?,
                    HORA_INICIO = ?,
                    HORA_FIN = ?,
                    FUNCIONARIO_ID = ?,
                    ESTADO = ?
                WHERE ID = ?
                """;

        try (Connection conexion = ConexionOracle.conectar()) {

            conexion.setAutoCommit(false);

            try {
                int filas;

                try (PreparedStatement ps =
                             conexion.prepareStatement(sql)) {

                    ps.setString(1, reserva.getActividad());
                    ps.setDate(2, Date.valueOf(reserva.getFecha()));
                    ps.setString(3, reserva.getHoraInicio().toString());
                    ps.setString(4, reserva.getHoraFin().toString());
                    ps.setString(5, reserva.getFuncionarioId());
                    ps.setString(6, reserva.getEstado().name());
                    ps.setString(7, reserva.getId());

                    filas = ps.executeUpdate();
                }

                if (filas == 0) {
                    conexion.rollback();
                    return false;
                }

                try (PreparedStatement ps =
                             conexion.prepareStatement(
                                     "DELETE FROM RESERVA_RECURSOS WHERE RESERVA_ID = ?")) {

                    ps.setString(1, reserva.getId());
                    ps.executeUpdate();
                }

                insertarRecursos(conexion, reserva);

                conexion.commit();
                return true;

            } catch (Exception e) {
                conexion.rollback();
                throw e;
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al modificar reserva en Oracle", e);
        }
    }

    // Mantener compatibilidad con el método anterior
    public void guardar(List<Reserva> reservas) {

        for (Reserva reserva : reservas) {

            if (buscarPorId(reserva.getId()) == null) {
                agregar(reserva);
            } else {
                modificar(reserva);
            }
        }
    }

    // Insertar recursos relacionados con la reserva
    private void insertarRecursos(
            Connection conexion, Reserva reserva) throws SQLException {

        String sql = """
                INSERT INTO RESERVA_RECURSOS
                (RESERVA_ID, RECURSO_ID)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            for (String recursoId : reserva.getRecursoIds()) {

                ps.setString(1, reserva.getId());
                ps.setString(2, recursoId);
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    // Establecer los datos para INSERT
    private void establecerParametros(
            PreparedStatement ps, Reserva reserva) throws SQLException {

        ps.setString(1, reserva.getId());
        ps.setString(2, reserva.getActividad());
        ps.setDate(3, Date.valueOf(reserva.getFecha()));
        ps.setString(4, reserva.getHoraInicio().toString());
        ps.setString(5, reserva.getHoraFin().toString());
        ps.setString(6, reserva.getFuncionarioId());
        ps.setString(7, reserva.getEstado().name());
    }

    // Reconstruir objeto Reserva desde Oracle
    private Reserva construirReserva(
            ResultSet rs, Connection conexion) throws SQLException {

        String id = rs.getString("ID");
        String actividad = rs.getString("ACTIVIDAD");

        java.time.LocalDate fecha =
                rs.getDate("FECHA").toLocalDate();

        LocalTime inicio =
                LocalTime.parse(rs.getString("HORA_INICIO"));

        LocalTime fin =
                LocalTime.parse(rs.getString("HORA_FIN"));

        String funcionarioId = rs.getString("FUNCIONARIO_ID");

        Usuario usuario = usuarioDAO.buscarPorId(funcionarioId);

        if (!(usuario instanceof Funcionario funcionario)) {
            throw new SQLException(
                    "Funcionario no encontrado: " + funcionarioId);
        }

        Reserva reserva = new Reserva(
                id, actividad, fecha, inicio, fin, funcionario);

        String estado = rs.getString("ESTADO");

        if ("CANCELADA".equalsIgnoreCase(estado)) {
            reserva.cancelar();
        } else if (!"ACTIVA".equalsIgnoreCase(estado)) {
            throw new SQLException(
                    "Estado de reserva no reconocido: " + estado);
        }

        String sqlRecursos = """
                SELECT RECURSO_ID
                FROM RESERVA_RECURSOS
                WHERE RESERVA_ID = ?
                """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sqlRecursos)) {

            ps.setString(1, id);

            try (ResultSet recursosRs = ps.executeQuery()) {

                while (recursosRs.next()) {

                    String recursoId =
                            recursosRs.getString("RECURSO_ID");

                    Recurso recurso =
                            recursoDAO.buscarPorId(recursoId);

                    if (recurso == null) {
                        throw new SQLException(
                                "Recurso no encontrado: " + recursoId);
                    }

                    reserva.agregarRecurso(recurso);
                }
            }
        }

        return reserva;
    }
}
