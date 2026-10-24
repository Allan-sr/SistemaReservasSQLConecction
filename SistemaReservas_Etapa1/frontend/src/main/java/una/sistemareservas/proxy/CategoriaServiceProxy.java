
package una.sistemareservas.proxy;

import una.sistemareservas.cliente.ClienteSocket;
import una.sistemareservas.contratos.ICategoriaService;
import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;


import java.util.List;

public class CategoriaServiceProxy implements ICategoriaService {

    private final ClienteSocket cliente;

    public CategoriaServiceProxy(ClienteSocket cliente) {
        this.cliente = cliente;
    }

    @Override
    public List<Categoria> listar() {

        try {
            Peticion peticion =
                    new Peticion(Operaciones.CATEGORIA_LISTAR, null);

            Respuesta respuesta =
                    cliente.enviarPeticion(peticion);

            if (!respuesta.isExito()) {
                throw new IllegalStateException(
                        respuesta.getMensaje()
                );
            }

            Object datos = respuesta.getDatos();

            if (!(datos instanceof List<?> lista)) {
                throw new IllegalStateException(
                        "El servidor no devolvió una lista válida."
                );
            }

            for (Object elemento : lista) {
                if (!(elemento instanceof Categoria)) {
                    throw new IllegalStateException(
                            "La lista contiene datos incorrectos."
                    );
                }
            }

            @SuppressWarnings("unchecked")
            List<Categoria> categorias = (List<Categoria>) lista;

            return categorias;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error al consultar categorías: " + e.getMessage(),
                    e
            );
        }
    }


    @Override
    public List<Categoria> buscar(String descripcion) {
        Respuesta respuesta = enviar(
                Operaciones.CATEGORIA_BUSCAR,
                descripcion
        );

        if (!(respuesta.getDatos() instanceof List<?> lista)) {
            throw new IllegalStateException(
                    "El servidor no devolvió una lista válida."
            );
        }

        for (Object elemento : lista) {
            if (!(elemento instanceof Categoria)) {
                throw new IllegalStateException(
                        "La lista contiene datos incorrectos."
                );
            }
        }

        @SuppressWarnings("unchecked")
        List<Categoria> categorias = (List<Categoria>) lista;

        return categorias;
    }

    @Override
    public String agregar(String descripcion) {
        return (String) enviar(
                Operaciones.CATEGORIA_AGREGAR,
                descripcion
        ).getDatos();
    }

    @Override
    public String modificar(String id, String descripcion) {
        return (String) enviar(
                Operaciones.CATEGORIA_MODIFICAR,
                new String[]{id, descripcion}
        ).getDatos();
    }

    @Override
    public String eliminar(String id) {
        return (String) enviar(
                Operaciones.CATEGORIA_ELIMINAR,
                id
        ).getDatos();
    }

    private Respuesta enviar(String operacion, Object datos) {
        try {
            Peticion peticion = new Peticion(operacion, datos);
            Respuesta respuesta = cliente.enviarPeticion(peticion);

            if (!respuesta.isExito()) {
                throw new IllegalStateException(
                        respuesta.getMensaje()
                );
            }

            return respuesta;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error en " + operacion + ": " + e.getMessage(),
                    e
            );
        }
    }

}

