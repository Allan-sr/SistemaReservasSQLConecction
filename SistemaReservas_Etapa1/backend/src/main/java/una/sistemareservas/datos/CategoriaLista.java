package una.sistemareservas.datos;

import una.sistemareservas.modelo.Categoria;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "categorias")
@XmlAccessorType(XmlAccessType.FIELD) // Indica a JAXB que lea directamente los atributos
public class CategoriaLista {

    @XmlElement(name = "categoria")
    private List<Categoria> categorias = new ArrayList<>();

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }
}