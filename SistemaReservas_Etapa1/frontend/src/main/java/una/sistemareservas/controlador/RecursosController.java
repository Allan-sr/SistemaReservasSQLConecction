package una.sistemareservas.controlador;

import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.negocio.RecursoService;
import una.sistemareservas.vista.RecursosView;

import java.util.List;

public class RecursosController {

    private final RecursosView vista;
    private final RecursoService servicio;

    public RecursosController(RecursosView vista) {

        this.vista = vista;
        this.servicio = new RecursoService();

        vista.cargarCategorias(servicio.listarCategorias());

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

        List<Recurso> resultado =
                servicio.buscar(
                        vista.getFiltroCategoriaId(),
                        vista.getFiltroDescripcion()
                );

        cargarListado(resultado);
    }

    private void guardar() {

        String id = vista.getId();
        String categoriaId = vista.getCategoriaId();
        String descripcion = vista.getDescripcion();

        if (id.isEmpty()) {

            vista.mostrarError("Debe indicar el id (número de activo) del recurso.");

            return;
        }

        boolean esNuevo = servicio.buscarPorId(id) == null;

        String error = esNuevo
                ? servicio.agregar(id, categoriaId, descripcion)
                : servicio.modificar(id, categoriaId, descripcion);

        if (error != null) {

            vista.mostrarError(error);

            return;
        }

        vista.mostrarMensaje(
                esNuevo
                        ? "Recurso agregado correctamente."
                        : "Recurso modificado correctamente."
        );

        vista.limpiarFormulario();

        cargarListado(servicio.listar());
    }

    private void borrar() {

        String id = vista.getId();

        if (id.isEmpty()) {

            vista.mostrarError(
                    "Seleccione un recurso de la lista para borrar."
            );

            return;
        }

        boolean confirmado =
                vista.confirmar(
                        "¿Desea eliminar el recurso " + id + "?"
                );

        if (!confirmado) {
            return;
        }

        String error = servicio.eliminar(id);

        if (error != null) {

            vista.mostrarError(error);

            return;
        }

        vista.mostrarMensaje("Recurso eliminado correctamente.");

        vista.limpiarFormulario();

        cargarListado(servicio.listar());
    }

    private void seleccionarFila() {

        int fila = vista.getFilaSeleccionada();

        if (fila < 0) {
            return;
        }

        String id = vista.getValorCelda(fila, 0);

        Recurso recurso = servicio.buscarPorId(id);

        if (recurso == null) {
            return;
        }

        vista.setId(recurso.getId());
        vista.setCategoriaSeleccionada(recurso.getCategoriaId());
        vista.setDescripcion(recurso.getDescripcion());

        vista.deshabilitarId();
    }

    private void cargarListado(List<Recurso> recursos) {
        vista.cargarTabla(recursos);
    }

    private void generarPDF() {

        try {

            java.io.File archivo =
                    una.sistemareservas.negocio.ReportePDFService
                            .generarRecursos(
                                    servicio.listar()
                            );

            vista.mostrarMensaje(
                    "PDF generado correctamente:\n"
                            + archivo.getAbsolutePath()
            );

            java.awt.Desktop.getDesktop().open(archivo);

        } catch (Exception ex) {

            vista.mostrarError(
                    "Error al generar PDF: "
                            + ex.getMessage()
            );
        }
    }
}