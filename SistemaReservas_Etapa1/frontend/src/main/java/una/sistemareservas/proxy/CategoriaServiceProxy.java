
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
}

