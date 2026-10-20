package una.sistemareservas.datos;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import una.sistemareservas.modelo.Categoria;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    private static final String RUTA_ARCHIVO = "categorias.xml";

    public List<Categoria> cargar() {

        File archivo = new File(RUTA_ARCHIVO);

        if (!archivo.exists()) {
            return new ArrayList<>();
        }

        try {
            JAXBContext contexto = JAXBContext.newInstance(CategoriaLista.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();
            CategoriaLista lista = (CategoriaLista) unmarshaller.unmarshal(archivo);
            return lista.getCategorias();

        } catch (JAXBException e) {
            throw new RuntimeException("Error al cargar categorias.xml", e);
        }
    }

    public void guardar(List<Categoria> categorias) {

        try {
            CategoriaLista lista = new CategoriaLista();
            lista.setCategorias(categorias);

            JAXBContext contexto = JAXBContext.newInstance(CategoriaLista.class);
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(lista, new File(RUTA_ARCHIVO));

        } catch (JAXBException e) {
            throw new RuntimeException("Error al guardar categorias.xml", e);
        }
    }

    public List<Categoria> agregar(String descripcion) {
        List<Categoria> categorias = cargar();

        String nuevoId = generarSiguienteId(categorias);
        Categoria nueva = new Categoria(nuevoId, descripcion);
        categorias.add(nueva);

        guardar(categorias);

        return categorias;
    }

    public boolean modificar(String id, String nuevaDescripcion) {
        List<Categoria> categorias = cargar();

        for (Categoria categoria : categorias) {
            if (categoria.getId().equalsIgnoreCase(id)) {
                categoria.setDescripcion(nuevaDescripcion);
                guardar(categorias);
                return true;
            }
        }

        return false;
    }

    public boolean eliminar(String id) {
        List<Categoria> categorias = cargar();

        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId().equalsIgnoreCase(id)) {
                categorias.remove(i);
                guardar(categorias);
                return true;
            }
        }

        return false;
    }

    public Categoria buscarPorId(String id) {
        for (Categoria categoria : cargar()) {
            if (categoria.getId().equalsIgnoreCase(id)) {
                return categoria;
            }
        }
        return null;
    }

    private String generarSiguienteId(List<Categoria> categorias) {

        int maximo = 0;

        for (Categoria categoria : categorias) {

            String id = categoria.getId();

            if (id != null && id.startsWith("CAT-")) {

                try {
                    int numero = Integer.parseInt(id.substring(4));
                    maximo = Math.max(maximo, numero);
                } catch (NumberFormatException ignorado) {
                    // Id con formato inesperado; se ignora para el cálculo.
                }
            }
        }

        return String.format("CAT-%06d", maximo + 1);
    }
}