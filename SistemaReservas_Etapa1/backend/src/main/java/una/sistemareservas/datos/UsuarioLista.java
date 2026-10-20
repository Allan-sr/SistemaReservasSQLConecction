package una.sistemareservas.datos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;

import una.sistemareservas.modelo.Administrador;
import una.sistemareservas.modelo.Funcionario;
import una.sistemareservas.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "usuarios")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsuarioLista {

    @XmlElements({
            @XmlElement(name = "administrador", type = Administrador.class),
            @XmlElement(name = "funcionario", type = Funcionario.class)
    })
    private List<Usuario> usuarios = new ArrayList<>();

    public UsuarioLista() {
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
