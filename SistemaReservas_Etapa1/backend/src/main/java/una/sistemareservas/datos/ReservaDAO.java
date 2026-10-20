package una.sistemareservas.datos;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import una.sistemareservas.modelo.Funcionario;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.modelo.Usuario;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    private static final String RUTA_ARCHIVO = "reservas.xml";
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RecursoDAO recursoDAO = new RecursoDAO();

    public List<Reserva> cargar() {

        File archivo = new File(RUTA_ARCHIVO);

        if (!archivo.exists()) {
            return new ArrayList<>();
        }

        try {
            JAXBContext contexto = JAXBContext.newInstance(ReservaLista.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();
            ReservaLista lista = (ReservaLista) unmarshaller.unmarshal(archivo);

            List<Reserva> reservas = lista.getReservas();
            reconstruirRelaciones(reservas);

            return reservas;

        } catch (JAXBException e) {
            throw new RuntimeException("Error al cargar reservas.xml", e);
        }
    }

    public void guardar(List<Reserva> reservas) {

        try {
            ReservaLista lista = new ReservaLista();
            lista.setReservas(reservas);

            JAXBContext contexto = JAXBContext.newInstance(ReservaLista.class);
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(lista, new File(RUTA_ARCHIVO));

        } catch (JAXBException e) {
            throw new RuntimeException("Error al guardar reservas.xml", e);
        }
    }

    public void agregar(Reserva nuevaReserva) {
        List<Reserva> reservas = cargar();
        reservas.add(nuevaReserva);
        guardar(reservas);
    }

    public boolean modificar(Reserva reservaModificada) {
        List<Reserva> reservas = cargar();

        for (int i = 0; i < reservas.size(); i++) {
            if (reservas.get(i).getId().equalsIgnoreCase(reservaModificada.getId())) {
                reservas.set(i, reservaModificada);
                guardar(reservas);
                return true;
            }
        }

        return false;
    }

    public Reserva buscarPorId(String id) {
        for (Reserva reserva : cargar()) {
            if (reserva.getId().equalsIgnoreCase(id)) {
                return reserva;
            }
        }
        return null;
    }

    /**
     * Reconstruye el Funcionario y la lista de Recurso de cada reserva,
     * usando los ids guardados en el XML.
     */
    private void reconstruirRelaciones(List<Reserva> reservas) {

        for (Reserva reserva : reservas) {

            Usuario usuario = usuarioDAO.buscarPorId(reserva.getFuncionarioId());
            if (usuario instanceof Funcionario funcionario) {
                reserva.setFuncionario(funcionario);
            }

            List<Recurso> recursosDeLaReserva = new ArrayList<>();
            for (String recursoId : reserva.getRecursoIds()) {
                Recurso recurso = recursoDAO.buscarPorId(recursoId);
                if (recurso != null) {
                    recursosDeLaReserva.add(recurso);
                }
            }
            reserva.setRecursos(recursosDeLaReserva);
        }
    }
}