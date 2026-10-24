
package una.sistemareservas.proxy;

import una.sistemareservas.cliente.ClienteSocket;
import una.sistemareservas.contratos.IFuncionarioService;
import una.sistemareservas.modelo.Funcionario;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.util.List;

public class FuncionarioServiceProxy implements IFuncionarioService {

    private final ClienteSocket cliente;

    public FuncionarioServiceProxy(ClienteSocket cliente) {
        this.cliente = cliente;
    }

    private Object enviar(String operacion, Object datos) {
        try {
            Peticion peticion = new Peticion(operacion, datos);
            Respuesta respuesta = cliente.enviarPeticion(peticion);

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

    @SuppressWarnings("unchecked")
    private List<Funcionario> convertirLista(Object datos) {
        if (!(datos instanceof List<?> lista)) {
            throw new IllegalStateException("Lista inválida.");
        }

        for (Object elemento : lista) {
            if (!(elemento instanceof Funcionario)) {
                throw new IllegalStateException("Funcionario inválido.");
            }
        }

        return (List<Funcionario>) lista;
    }

    @Override
    public List<Funcionario> listar() {
        return convertirLista(enviar(Operaciones.FUNCIONARIO_LISTAR, null));
    }

    @Override
    public List<Funcionario> buscar(String id, String nombre) {
        return convertirLista(enviar(
                Operaciones.FUNCIONARIO_BUSCAR,
                new String[]{id, nombre}
        ));
    }

    @Override
    public Funcionario buscarPorId(String id) {
        return (Funcionario) enviar(
                Operaciones.FUNCIONARIO_BUSCAR_ID, id
        );
    }

    @Override
    public String agregar(String id, String nombre, String telefono) {
        return (String) enviar(
                Operaciones.FUNCIONARIO_AGREGAR,
                new String[]{id, nombre, telefono}
        );
    }

    @Override
    public String modificar(String id, String nombre, String telefono) {
        return (String) enviar(
                Operaciones.FUNCIONARIO_MODIFICAR,
                new String[]{id, nombre, telefono}
        );
    }

    @Override
    public String eliminar(String id) {
        return (String) enviar(
                Operaciones.FUNCIONARIO_ELIMINAR, id
        );
    }
}
