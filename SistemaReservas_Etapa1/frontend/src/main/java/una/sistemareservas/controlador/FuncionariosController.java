package una.sistemareservas.controlador;

import java.io.File;
import una.sistemareservas.modelo.Funcionario;
import una.sistemareservas.negocio.FuncionarioService;
import una.sistemareservas.vista.FuncionariosView;

import java.util.List;

public class FuncionariosController {

    private final FuncionariosView vista;
    private final FuncionarioService servicio;

    public FuncionariosController(FuncionariosView vista) {

        this.vista = vista;
        this.servicio = new FuncionarioService();

        iniciarEventos();

        cargarListado(servicio.listar());
    }

    private void iniciarEventos() {

        vista.getBtnBuscar()
                .addActionListener(e -> buscar());

        vista.getBtnGuardar()
                .addActionListener(e -> guardar());

        vista.getBtnBorrar()
                .addActionListener(e -> borrar());

        vista.getBtnLimpiar()
                .addActionListener(e -> vista.limpiarFormulario());

        /*
         * La generación de reporte en PDF se agregará
         * de forma transversal a todas las pantallas
         * más adelante.
         */
        vista.getBtnImprimir().addActionListener(e -> generarPDF());

        vista.getTabla()
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        seleccionarFila();
                    }
                });
    }

    private void buscar() {

        List<Funcionario> resultado =
                servicio.buscar(
                        vista.getBuscarId(),
                        vista.getBuscarNombre()
                );

        cargarListado(resultado);
    }

    private void guardar() {

        String id = vista.getId();
        String nombre = vista.getNombre();
        String telefono = vista.getTelefono();

        if (id.isEmpty()) {

            vista.mostrarError("Debe indicar el id del funcionario.");

            return;
        }

        boolean esNuevo = servicio.buscarPorId(id) == null;

        String error = esNuevo
                ? servicio.agregar(id, nombre, telefono)
                : servicio.modificar(id, nombre, telefono);

        if (error != null) {

            vista.mostrarError(error);

            return;
        }

        vista.mostrarMensaje(
                esNuevo
                        ? "Funcionario agregado correctamente."
                        : "Funcionario modificado correctamente."
        );

        vista.limpiarFormulario();

        cargarListado(servicio.listar());
    }

    private void borrar() {

        String id = vista.getId();

        if (id.isEmpty()) {

            vista.mostrarError(
                    "Seleccione un funcionario de la lista para borrar."
            );

            return;
        }

        boolean confirmado =
                vista.confirmar(
                        "¿Desea eliminar al funcionario " + id + "?"
                );

        if (!confirmado) {
            return;
        }

        String error = servicio.eliminar(id);

        if (error != null) {

            vista.mostrarError(error);

            return;
        }

        vista.mostrarMensaje("Funcionario eliminado correctamente.");

        vista.limpiarFormulario();

        cargarListado(servicio.listar());
    }

    private void seleccionarFila() {

        int fila = vista.getFilaSeleccionada();

        if (fila < 0) {
            return;
        }

        String id = vista.getValorCelda(fila, 0);
        String nombre = vista.getValorCelda(fila, 1);
        String telefono = vista.getValorCelda(fila, 2);

        vista.setId(id);
        vista.setNombre(nombre);
        vista.setTelefono(telefono);

        vista.deshabilitarId();
    }

    private void cargarListado(List<Funcionario> funcionarios) {
        vista.cargarTabla(funcionarios);
    }

    private void generarPDF() {

        try {

            File archivo = una.sistemareservas.negocio.ReportePDFService.generarFuncionarios(servicio.listar());
            vista.mostrarMensaje("PDF generado correctamente:\n" + archivo.getAbsolutePath());
            java.awt.Desktop.getDesktop().open(archivo);
        } catch (Exception ex) {
            vista.mostrarError("Error al generar PDF: " + ex.getMessage()
            );
        }
    }
}
