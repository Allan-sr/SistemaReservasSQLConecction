package una.sistemareservas.modelo;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Funcionario.class, Administrador.class})
public abstract class Usuario {

    protected String id;
    protected String clave;
    protected Rol rol;

    public Usuario() {
    }

    public Usuario(String id, String clave, Rol rol) {
        this.id = id;
        this.clave = clave;
        this.rol = rol;
    }

    public String getClave() {
        return clave;
    }

    public String getId() {
        return id;
    }

    public Rol getRol() {
        return rol;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public abstract String getNombreCompleto();
}
