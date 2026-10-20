package una.sistemareservas.datos;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import una.sistemareservas.modelo.Administrador;
import una.sistemareservas.modelo.Funcionario;
import una.sistemareservas.modelo.Rol;
import una.sistemareservas.modelo.Usuario;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String RUTA_ARCHIVO = "usuarios.xml";

    public UsuarioDAO() {
        inicializarArchivo();
    }

    /**
     * Carga todos los usuarios desde el XML.
     */

    public List<Usuario> cargar() {

        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try {

            JAXBContext contexto = JAXBContext.newInstance(UsuarioLista.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();
            UsuarioLista lista = (UsuarioLista) unmarshaller.unmarshal(archivo);
            return lista.getUsuarios();

        } catch (JAXBException e) {
            throw new RuntimeException("Error al cargar usuarios.xml", e);
        }
    }

    /**
     * Guarda todos los usuarios en el XML.
     */
    public void guardar(List<Usuario> usuarios) {

        try {

            UsuarioLista lista = new UsuarioLista();
            lista.setUsuarios(usuarios);
            JAXBContext contexto = JAXBContext.newInstance(UsuarioLista.class);
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(lista, new File(RUTA_ARCHIVO)
            );

        } catch (JAXBException e) {

            throw new RuntimeException(
                    "Error al guardar usuarios.xml", e
            );
        }
    }

    /**
     * Busca un usuario por su ID.
     */
    public Usuario buscarPorId(String id) {

        for (Usuario usuario : cargar()) {

            if (usuario.getId().equalsIgnoreCase(id)) {
                return usuario;
            }
        }

        return null;
    }

    /**
     * Agrega un nuevo usuario.
     */
    public boolean agregar(Usuario nuevoUsuario) {

        List<Usuario> usuarios = cargar();

        if (buscarPorId(nuevoUsuario.getId()) != null) {
            return false;
        }

        usuarios.add(nuevoUsuario);

        guardar(usuarios);

        return true;
    }

    /**
     * Modifica un usuario existente.
     */
    public boolean modificar(Usuario usuarioModificado) {

        List<Usuario> usuarios = cargar();

        for (int i = 0; i < usuarios.size(); i++) {

            if (usuarios.get(i)
                    .getId()
                    .equalsIgnoreCase(usuarioModificado.getId())) {

                usuarios.set(i, usuarioModificado);

                guardar(usuarios);

                return true;
            }
        }

        return false;
    }

    /**
     * Elimina un usuario.
     */
    public boolean eliminar(String id) {

        List<Usuario> usuarios = cargar();

        for (int i = 0; i < usuarios.size(); i++) {

            if (usuarios.get(i)
                    .getId()
                    .equalsIgnoreCase(id)) {

                usuarios.remove(i);

                guardar(usuarios);

                return true;
            }
        }

        return false;
    }

    /**
     * Crea usuarios iniciales si el XML todavía no existe.
     */
    private void inicializarArchivo() {

        File archivo = new File(RUTA_ARCHIVO);

        if (archivo.exists()) {
            return;
        }

        List<Usuario> usuarios = new ArrayList<>();

        Administrador administrador = new Administrador("admin", "admin", Rol.ADMINISTRADOR);
        Funcionario funcionario = new Funcionario("1001", "1001", Rol.FUNCIONARIO, "Funcionario de prueba", "8888-8888");

        usuarios.add(administrador);
        usuarios.add(funcionario);

        guardar(usuarios);
    }
}