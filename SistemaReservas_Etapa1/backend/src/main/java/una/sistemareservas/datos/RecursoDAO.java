package una.sistemareservas.datos;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Recurso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {

    private static final String RUTA_ARCHIVO = "recursos.xml";
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public List<Recurso> cargar() {

        File archivo = new File(RUTA_ARCHIVO);

        if (!archivo.exists()) {
            return new ArrayList<>();
        }

        try {
            JAXBContext contexto = JAXBContext.newInstance(RecursoLista.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();
            RecursoLista lista = (RecursoLista) unmarshaller.unmarshal(archivo);

            List<Recurso> recursos = lista.getRecursos();
            reconstruirCategorias(recursos);

            return recursos;

        } catch (JAXBException e) {
            throw new RuntimeException("Error al cargar recursos.xml", e);
        }
    }

    public void guardar(List<Recurso> recursos) {

        try {
            RecursoLista lista = new RecursoLista();
            lista.setRecursos(recursos);

            JAXBContext contexto = JAXBContext.newInstance(RecursoLista.class);
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(lista, new File(RUTA_ARCHIVO));

        } catch (JAXBException e) {
            throw new RuntimeException("Error al guardar recursos.xml", e);
        }
    }

    public Recurso buscarPorId(String id) {
        for (Recurso recurso : cargar()) {
            if (recurso.getId().equalsIgnoreCase(id)) {
                return recurso;
            }
        }
        return null;
    }

    public boolean modificar(Recurso recursoModificado) {
        List<Recurso> recursos = cargar();

        for (int i = 0; i < recursos.size(); i++) {
            if (recursos.get(i).getId().equalsIgnoreCase(recursoModificado.getId())) {
                recursos.set(i, recursoModificado);
                guardar(recursos);
                return true;
            }
        }

        return false;
    }

    public boolean eliminar(String id) {
        List<Recurso> recursos = cargar();

        for (int i = 0; i < recursos.size(); i++) {
            if (recursos.get(i).getId().equalsIgnoreCase(id)) {
                recursos.remove(i);
                guardar(recursos);
                return true;
            }
        }

        return false;
    }

    public void agregar(Recurso nuevoRecurso) {
        List<Recurso> recursos = cargar();
        recursos.add(nuevoRecurso);
        guardar(recursos);
    }

    /**
     * Reconstruye el objeto Categoria completo de cada recurso,
     * usando el categoriaId guardado en el XML.
     */
    private void reconstruirCategorias(List<Recurso> recursos) {
        for (Recurso recurso : recursos) {
            Categoria categoria = categoriaDAO.buscarPorId(recurso.getCategoriaId());
            recurso.setCategoria(categoria);
        }
    }
}