
package una.sistemareservas.proxy;

import una.sistemareservas.cliente.ClienteSocket;
import una.sistemareservas.contratos.ICalendarioRecursosService;
import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CalendarioRecursosServiceProxy
        implements ICalendarioRecursosService {

    private final ClienteSocket cliente;

    public CalendarioRecursosServiceProxy(ClienteSocket cliente) {
        this.cliente = cliente;
    }

    private Object enviar(String operacion, Object datos) {
        try {
            Respuesta respuesta = cliente.enviarPeticion(
                    new Peticion(operacion, datos)
            );

            if (!respuesta.isExito()) {
                throw new IllegalStateException(respuesta.getMensaje());
            }

            return respuesta.getDatos();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error en " + operacion + ": " + e.getMessage(), e
            );
        }
    }

    private <T> List<T> convertirLista(Object datos, Class<T> tipo) {
        if (!(datos instanceof List<?> lista)) {
            throw new IllegalStateException(
                    "El servidor no devolvió una lista válida."
            );
        }

        for (Object elemento : lista) {
            if (!tipo.isInstance(elemento)) {
                throw new IllegalStateException(
                        "La lista contiene elementos incorrectos."
                );
            }
        }

        return lista.stream().map(tipo::cast).toList();
    }

    @Override
    public List<Categoria> listarCategorias() {
        return convertirLista(
                enviar(Operaciones.CALENDARIO_RECURSOS_CATEGORIAS, null),
                Categoria.class
        );
    }

    @Override
    public List<Recurso> listarRecursosPorCategoria(String categoriaId) {
        return convertirLista(
                enviar(Operaciones.CALENDARIO_RECURSOS_LISTAR, categoriaId),
                Recurso.class
        );
    }

    @Override
    public List<LocalTime> obtenerHorasDelDia() {
        return convertirLista(
                enviar(Operaciones.CALENDARIO_RECURSOS_HORAS, null),
                LocalTime.class
        );
    }

    @Override
    public Reserva obtenerReservaDelRecurso(
            Recurso recurso,
            LocalDate fecha,
            LocalTime hora) {

        Object[] datos = {recurso, fecha, hora};

        return (Reserva) enviar(
                Operaciones.CALENDARIO_RECURSOS_RESERVA,
                datos
        );
    }
}

