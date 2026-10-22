package una.sistemareservas.modelo;
import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "recurso")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recurso implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement
    private String id;

    @XmlElement
    private String categoriaId;

    @XmlElement
    private String descripcion;

    @XmlTransient
    private Categoria categoria;

    public Recurso() {
    }

    public Recurso(String id, Categoria categoria, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
        setCategoria(categoria);
    }

    public String getId() {
        return id;
    }

    public String getCategoriaId() {
        return categoriaId;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
        this.categoriaId = (categoria != null) ? categoria.getId() : null;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}