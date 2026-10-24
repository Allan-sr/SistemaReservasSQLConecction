
package una.sistemareservas.contratos;

import una.sistemareservas.modelo.Funcionario;
import java.util.List;

public interface IFuncionarioService {

    List<Funcionario> listar();

    List<Funcionario> buscar(String id, String nombre);

    Funcionario buscarPorId(String id);

    String agregar(String id, String nombre, String telefono);

    String modificar(String id, String nombre, String telefono);

    String eliminar(String id);
}

