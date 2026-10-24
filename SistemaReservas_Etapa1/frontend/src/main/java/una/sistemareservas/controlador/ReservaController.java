package una.sistemareservas.controlador;

import una.sistemareservas.contratos.ICategoriaService;
import una.sistemareservas.contratos.IReservaService;
import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.modelo.Usuario;
import una.sistemareservas.negocio.ResultadoReserva;
import una.sistemareservas.proxy.Servicios;
import una.sistemareservas.util.ReportePDFService;
import una.sistemareservas.util.Sesion;
import una.sistemareservas.vista.ReservasView;
import una.sistemareservas.negocio.ReservaExtraidaDTO;

import javax.swing.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaController {

    private final ReservasView vista;
    private final IReservaService servicio;
    private final ICategoriaService categoriaServicio;

    public ReservaController(ReservasView vista) {
        this.vista = vista;
        this.servicio = Servicios.reservas();
        this.categoriaServicio = Servicios.categorias();

        try {
            vista.cargarCategorias(categoriaServicio.listar());
        } catch (RuntimeException e) {
            vista.mostrarError("No se pudieron cargar las categorías: " + e.getMessage());
        }

        vista.limpiarFormulario();
        iniciarEventos();
        cargarListado();
    }

    private void iniciarEventos() {

        vista.getBtnReservar().addActionListener(e -> reservar());
        vista.getBtnCancelar().addActionListener(e -> cancelar());
        vista.getBtnLimpiar().addActionListener(e -> vista.limpiarFormulario());
        vista.getBtnExtraerIA().addActionListener(e -> procesarFraseIA());
        vista.getBtnImprimir().addActionListener(e -> generarReportePDF());
    }

    /**
     * Id del funcionario con sesión activa. Viaja en cada petición, porque
     * el servidor no guarda quién es el usuario entre una petición y otra.
     */
    private String idFuncionario() {

        Usuario usuario = Sesion.getUsuarioActual();

        return usuario == null ? null : usuario.getId();
    }

    private void reservar() {

        String actividad = vista.getActividad();
        LocalDate fecha = vista.getFecha();
        LocalTime horaInicio = vista.getHoraInicio();
        LocalTime horaFin = vista.getHoraFin();
        List<String> categoriaIds = vista.getCategoriaIdsSeleccionadas();

        ResultadoReserva resultado;

        try {
            resultado = servicio.crear(
                    idFuncionario(), actividad, fecha, horaInicio, horaFin, categoriaIds);
        } catch (RuntimeException e) {
            vista.mostrarError("No se pudo registrar la reserva: " + e.getMessage());
            return;
        }

        if (resultado.esErrorValidacion()) {

            vista.mostrarError(resultado.getMensajeError());

            return;
        }

        if (resultado.esSinDisponibilidad()) {

            vista.mostrarError(
                    "No hubo disponibilidad para: " + nombresCategorias(resultado.getCategoriasNoDisponibles())
                            + ". Puede modificar la reserva e intentar de nuevo."
            );

            return;
        }

        Reserva reserva = resultado.getReserva();

        vista.mostrarMensaje(
                "Reserva registrada con éxito. Recursos asignados: " + nombresRecursos(reserva)
        );

        vista.limpiarFormulario();

        cargarListado();
    }

    private void cancelar() {

        int fila = vista.getFilaSeleccionada();

        if (fila < 0) {

            vista.mostrarError("Seleccione una reserva de la lista para cancelar.");

            return;
        }

        String id = vista.getValorCelda(fila, 0);

        boolean confirmado =
                vista.confirmar("¿Desea cancelar la reserva " + id + "?");

        if (!confirmado) {
            return;
        }

        ResultadoReserva resultado;

        try {
            resultado = servicio.cancelar(idFuncionario(), id);
        } catch (RuntimeException e) {
            vista.mostrarError("No se pudo cancelar la reserva: " + e.getMessage());
            return;
        }

        if (resultado.esErrorValidacion()) {

            vista.mostrarError(resultado.getMensajeError());

            return;
        }

        vista.mostrarMensaje("Reserva cancelada correctamente. Los recursos fueron liberados.");

        cargarListado();
    }

    private void cargarListado() {

        try {
            vista.cargarTabla(servicio.listarMisReservas(idFuncionario()));
        } catch (RuntimeException e) {
            vista.mostrarError("No se pudo cargar el listado de reservas: " + e.getMessage());
        }
    }

    private String nombresCategorias(List<Categoria> categorias) {

        StringBuilder texto = new StringBuilder();

        for (Categoria categoria : categorias) {

            if (texto.length() > 0) {
                texto.append(", ");
            }

            texto.append(categoria.getDescripcion());
        }

        return texto.toString();
    }

    private String nombresRecursos(Reserva reserva) {

        StringBuilder texto = new StringBuilder();

        for (Recurso recurso : reserva.getRecursos()) {

            if (texto.length() > 0) {
                texto.append(", ");
            }

            texto.append(recurso.getId());
        }

        return texto.toString();
    }

    private void procesarFraseIA() {
        String frase = vista.getTxtFrase().getText();

        if (frase == null || frase.trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor, ingrese una frase para extraer la información.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // La IA se consulta en el servidor; aquí solo se recibe el resultado
            ReservaExtraidaDTO dto = servicio.extraerDatosReserva(frase);

            // Auto-llenar los componentes de la vista con los tipos convertidos a LocalDate/LocalTime
            if (dto.getActividad() != null) {
                vista.setTxtActividad(dto.getActividad());
            }
            if (dto.getFecha() != null) {
                vista.setFecha(LocalDate.parse(dto.getFecha()));
            }
            if (dto.getHoraInicio() != null) {
                vista.setHoraInicio(LocalTime.parse(dto.getHoraInicio()));
            }
            if (dto.getHoraFin() != null) {
                vista.setHoraFin(LocalTime.parse(dto.getHoraFin()));
            }
            if (dto.getCategorias() != null && !dto.getCategorias().isEmpty()) {
                vista.seleccionarCategoriasPorNombre(dto.getCategorias());
            }

            JOptionPane.showMessageDialog(vista, "Datos extraídos correctamente por la IA.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al procesar la frase: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarReportePDF() {
        int filaSeleccionada = vista.getTablaReservas().getSelectedRow();

        if (filaSeleccionada == -1) {
            vista.mostrarMensaje("Por favor, seleccione una reserva de la tabla para imprimir el comprobante.");
            return;
        }

        // Obtener el ID de la reserva seleccionada en la tabla
        String idReserva = vista.getTablaReservas().getValueAt(filaSeleccionada, 0).toString();

        // Buscar la reserva completa desde el servidor
        List<Reserva> misReservas;

        try {
            misReservas = servicio.listarMisReservas(idFuncionario());
        } catch (RuntimeException e) {
            vista.mostrarMensaje("No se pudo consultar la reserva: " + e.getMessage());
            return;
        }

        Reserva seleccionada = misReservas.stream()
                .filter(r -> r.getId().equals(idReserva))
                .findFirst()
                .orElse(null);

        if (seleccionada == null) {
            vista.mostrarMensaje("No se pudo obtener la información de la reserva seleccionada.");
            return;
        }

        try {
            File pdfFile = ReportePDFService.generarComprobanteReserva(seleccionada);

            int respuesta = JOptionPane.showConfirmDialog(
                    vista,
                    "Comprobante PDF generado exitosamente.\n¿Desea abrir el archivo en este momento?",
                    "Reporte Generado",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (respuesta == JOptionPane.YES_OPTION && java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(pdfFile);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            vista.mostrarMensaje("Error al generar el comprobante PDF: " + ex.getMessage());
        }
    }
}