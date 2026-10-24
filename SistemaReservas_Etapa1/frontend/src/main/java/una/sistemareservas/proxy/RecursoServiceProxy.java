
package una.sistemareservas.proxy;

import una.sistemareservas.cliente.ClienteSocket;
import una.sistemareservas.contratos.IRecursoService;
import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.util.List;

public class RecursoServiceProxy implements IRecursoService {

    private final ClienteSocket cliente;

    public RecursoServiceProxy(ClienteSocket cliente) {
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
            throw new IllegalStateException("Lista inválida del servidor.");
        }

        for (Object elemento : lista) {
            if (!tipo.isInstance(elemento)) {
                throw new IllegalStateException(
                        "Elemento inválido en la lista."
                );
            }
        }

        return lista.stream().map(tipo::cast).toList();
    }

    @Override
    public List<Recurso> listar() {
        return convertirLista(
                enviar(Operaciones.RECURSO_LISTAR, null),
                Recurso.class
        );
    }

    @Override
    public List<Categoria> listarCategorias() {
        return convertirLista(
                enviar(Operaciones.RECURSO_LISTAR_CATEGORIAS, null),
                Categoria.class
        );
    }

    @Override
    public List<Recurso> buscar(String categoriaId, String descripcion) {
        return convertirLista(
                enviar(Operaciones.RECURSO_BUSCAR,
                        new String[]{categoriaId, descripcion}),
                Recurso.class
        );
    }

    @Override
    public Recurso buscarPorId(String id) {
        return (Recurso) enviar(Operaciones.RECURSO_BUSCAR_ID, id);
    }

    @Override
    public String agregar(String id, String categoriaId, String descripcion) {
        return (String) enviar(
                Operaciones.RECURSO_AGREGAR,
                new String[]{id, categoriaId, descripcion}
        );
    }

    @Override
    public String modificar(String id, String categoriaId, String descripcion) {
        return (String) enviar(
                Operaciones.RECURSO_MODIFICAR,
                new String[]{id, categoriaId, descripcion}
        );
    }

    @Override
    public String eliminar(String id) {
        return (String) enviar(Operaciones.RECURSO_ELIMINAR, id);
    }
}

