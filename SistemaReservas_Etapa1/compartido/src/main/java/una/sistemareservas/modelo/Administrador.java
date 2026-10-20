package una.sistemareservas.modelo;

public class Administrador extends Usuario {

    public Administrador() {super();
    }

    public Administrador(String id, String clave, Rol rol) {
        super(id, clave, rol);
    }

    @Override
    public String getNombreCompleto() {
        return id;
    }
}