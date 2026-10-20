package una.sistemareservas.controlador;

import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.negocio.CategoriaService;
import una.sistemareservas.vista.CategoriasView;

import java.util.List;

public class CategoriasController {

    private final CategoriasView vista;
    private final CategoriaService servicio;

    public CategoriasController(CategoriasView vista) {

        this.vista = vista;
        this.servicio = new CategoriaService();

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

        List<Categoria> resultado =
                servicio.buscar(vista.getBuscarDescripcion());

        cargarListado(resultado);
    }

    private void guardar() {

        String id = vista.getId();
        String descripcion = vista.getDescripcion();

        boolean esNuevo = id.isEmpty();

        String error = esNuevo
                ? servicio.agregar(descripcion)
                : servicio.modificar(id, descripcion);

        if (error != null) {

            vista.mostrarError(error);

            return;
        }

        vista.mostrarMensaje(
                esNuevo
                        ? "Categoría agregada correctamente."
                        : "Categoría modificada correctamente."
        );

        vista.limpiarFormulario();

        cargarListado(servicio.listar());
    }

    private void borrar() {

        String id = vista.getId();

        if (id.isEmpty()) {

            vista.mostrarError(
                    "Seleccione una categoría de la lista para borrar."
            );

            return;
        }

        boolean confirmado =
                vista.confirmar(
                        "¿Desea eliminar la categoría " + id + "?"
                );

        if (!confirmado) {
            return;
        }

        String error = servicio.eliminar(id);

        if (error != null) {

            vista.mostrarError(error);

            return;
        }

        vista.mostrarMensaje("Categoría eliminada correctamente.");

        vista.limpiarFormulario();

        cargarListado(servicio.listar());
    }

    private void seleccionarFila() {

        int fila = vista.getFilaSeleccionada();

        if (fila < 0) {
            return;
        }

        String id = vista.getValorCelda(fila, 0);
        String descripcion = vista.getValorCelda(fila, 1);

        vista.setId(id);
        vista.setDescripcion(descripcion);
    }

    private void cargarListado(List<Categoria> categorias) {
        vista.cargarTabla(categorias);
    }

    private void generarPDF() {
        try {
            java.io.File archivo = una.sistemareservas.negocio.ReportePDFService.generarCategorias(servicio.listar());
            vista.mostrarMensaje("PDF generado correctamente:\n" + archivo.getAbsolutePath());
            java.awt.Desktop.getDesktop().open(archivo);
        } catch (Exception ex) {
            vista.mostrarError("Error al generar PDF: " + ex.getMessage());
        }
    }
}