
package una.sistemareservas.proxy;

import una.sistemareservas.cliente.ClienteSocket;
import una.sistemareservas.contratos.ICalendarioActividadesService;
import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.time.LocalDate;
import java.util.List;

public class CalendarioActividadesServiceProxy
        implements ICalendarioActividadesService {

    private final ClienteSocket cliente;

    public CalendarioActividadesServiceProxy(ClienteSocket cliente) {
        this.cliente = cliente;
    }

    private Object enviar(String operacion, Object datos) {
        try {
            Peticion peticion = new Peticion(operacion, datos);

            Respuesta respuesta = cliente.enviarPeticion(peticion);

            if (!respuesta.isExito()) {
                throw new IllegalStateException(
                        respuesta.getMensaje()
                );
            }

            return respuesta.getDatos();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error en " + operacion + ": " + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public List<Reserva> listarReservasSemana(LocalDate fecha) {

        Object datos = enviar(
                Operaciones.CALENDARIO_ACTIVIDADES_SEMANA,
                fecha
        );

        if (!(datos instanceof List<?> lista)) {
            throw new IllegalStateException(
                    "El servidor no devolvió una lista válida."
            );
        }

        for (Object elemento : lista) {
            if (!(elemento instanceof Reserva)) {
                throw new IllegalStateException(
                        "La lista contiene reservas inválidas."
                );
            }
        }

        @SuppressWarnings("unchecked")
        List<Reserva> reservas = (List<Reserva>) lista;

        return reservas;
    }

    @Override
    public LocalDate obtenerLunes(LocalDate fecha) {

        Object datos = enviar(
                Operaciones.CALENDARIO_ACTIVIDADES_LUNES,
                fecha
        );

        if (!(datos instanceof LocalDate)) {
            throw new IllegalStateException(
                    "El servidor no devolvió una fecha válida."
            );
        }

        return (LocalDate) datos;
    }
}
