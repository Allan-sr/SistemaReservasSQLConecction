package una.sistemareservas.proxy;

import una.sistemareservas.cliente.ClienteSocket;
import una.sistemareservas.contratos.IReservaService;
import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.negocio.ResultadoReserva;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;
import una.sistemareservas.negocio.ReservaExtraidaDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaServiceProxy implements IReservaService {

    // La "radio" para hablar con el servidor
    private final ClienteSocket cliente;

    public ReservaServiceProxy(ClienteSocket cliente) {
        this.cliente = cliente;
    }

    @Override
    public ResultadoReserva crear(
            String funcionarioId,
            String actividad,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            List<String> categoriaIds) {

        // Se copia la lista a un ArrayList para asegurar que sea serializable
        List<String> ids = categoriaIds == null ? null : new ArrayList<>(categoriaIds);

        Object datos = enviar(
                Operaciones.RESERVA_CREAR,
                new Object[]{funcionarioId, actividad, fecha, horaInicio, horaFin, ids}
        );

        return (ResultadoReserva) datos;
    }

    @Override
    public ResultadoReserva cancelar(String funcionarioId, String id) {

        Object datos = enviar(
                Operaciones.RESERVA_CANCELAR,
                new Object[]{funcionarioId, id}
        );

        return (ResultadoReserva) datos;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Reserva> listarMisReservas(String funcionarioId) {

        Object datos = enviar(
                Operaciones.RESERVA_LISTAR_MIAS,
                new Object[]{funcionarioId}
        );

        return (List<Reserva>) datos;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Reserva> listar() {

        Object datos = enviar(Operaciones.RESERVA_LISTAR, null);

        return (List<Reserva>) datos;
    }

    @Override
    public ReservaExtraidaDTO extraerDatosReserva(String frase) {

        Object datos = enviar(
                Operaciones.RESERVA_EXTRAER_IA,
                new Object[]{frase}
        );

        return (ReservaExtraidaDTO) datos;
    }

    /**
     * Envía una petición al servidor y devuelve los datos de la respuesta.
     * Concentra aquí el manejo de errores de red para no repetirlo en cada método.
     */
    private Object enviar(String operacion, Object datos) {
        try {
            Respuesta respuesta = cliente.enviarPeticion(new Peticion(operacion, datos));

            if (!respuesta.isExito()) {
                throw new RuntimeException(respuesta.getMensaje());
            }

            return respuesta.getDatos();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("No se pudo comunicar con el servidor", e);
        }
    }
}