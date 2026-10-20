package una.sistemareservas.controlador;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import una.sistemareservas.modelo.EstadoReserva;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.negocio.RecursoService;
import una.sistemareservas.negocio.ReportePDFService;
import una.sistemareservas.negocio.ReservaService;
import una.sistemareservas.vista.EstadisticasView;

import java.awt.Desktop;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EstadisticasController {

    private final EstadisticasView vista;
    private final ReservaService reservaService;

    private List<Reserva> reservasActuales;
    private Map<String, Integer> datosActuales;

    public EstadisticasController(EstadisticasView vista) {
        this.vista = vista;
        this.reservaService = new ReservaService();

        iniciarEventos();
    }

    private void iniciarEventos() {

        vista.getBtnGenerar().addActionListener(
                e -> generarReporte()
        );

        vista.getBtnPDF().addActionListener(
                e -> generarPDF()
        );
    }

    private void generarReporte() {

        LocalDate inicio = vista.getFechaInicio();
        LocalDate fin = vista.getFechaFin();

        if (inicio.isAfter(fin)) {
            vista.mostrarMensaje(
                    "La fecha de inicio no puede ser posterior a la fecha fin."
            );
            return;
        }

        reservasActuales = reservaService.listar()
                .stream()
                .filter(r ->
                        r.getEstado() == EstadoReserva.ACTIVA
                                && r.getFecha() != null
                                && !r.getFecha().isBefore(inicio)
                                && !r.getFecha().isAfter(fin)
                )
                .collect(Collectors.toList());

        if (reservasActuales.isEmpty()) {
            vista.mostrarMensaje(
                    "No se encontraron reservas activas en el período seleccionado."
            );
            return;
        }

        if (vista.getTipoGraficoSeleccionado() == 0) {

            datosActuales =
                    obtenerCategorias(reservasActuales);

            generarGraficoCategorias(datosActuales);

        } else {

            datosActuales =
                    obtenerActividadesPorSemana(reservasActuales);

            generarGraficoActividades(datosActuales);
        }
    }

    private Map<String, Integer> obtenerCategorias(
            List<Reserva> reservas) {

        Map<String, Integer> conteo =
                new LinkedHashMap<>();

        RecursoService recursoService =
                new RecursoService();

        for (Reserva reserva : reservas) {

            if (reserva.getRecursos() != null
                    && !reserva.getRecursos().isEmpty()) {

                for (Recurso recurso :
                        reserva.getRecursos()) {

                    if (recurso.getCategoria() != null) {

                        String categoria =
                                recurso.getCategoria()
                                        .getDescripcion();

                        conteo.put(
                                categoria,
                                conteo.getOrDefault(
                                        categoria, 0
                                ) + 1
                        );
                    }
                }

            } else if (reserva.getRecursoIds() != null) {

                for (String id :
                        reserva.getRecursoIds()) {

                    Recurso recurso =
                            recursoService.buscarPorId(id);

                    if (recurso != null
                            && recurso.getCategoria() != null) {

                        String categoria =
                                recurso.getCategoria()
                                        .getDescripcion();

                        conteo.put(
                                categoria,
                                conteo.getOrDefault(
                                        categoria, 0
                                ) + 1
                        );
                    }
                }
            }
        }

        return conteo;
    }

    private Map<String, Integer> obtenerActividadesPorSemana(
            List<Reserva> reservas) {

        Map<String, Integer> conteo =
                new LinkedHashMap<>();

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM");

        for (Reserva reserva : reservas) {

            LocalDate lunes =
                    reserva.getFecha()
                            .with(DayOfWeek.MONDAY);

            LocalDate domingo =
                    lunes.plusDays(6);

            String semana =
                    lunes.format(formato)
                            + " - "
                            + domingo.format(formato);

            conteo.put(
                    semana,
                    conteo.getOrDefault(semana, 0) + 1
            );
        }

        return conteo;
    }

    private void generarGraficoCategorias(
            Map<String, Integer> datos) {

        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();

        datos.forEach(
                (categoria, cantidad) ->
                        dataset.addValue(
                                cantidad,
                                "Recursos",
                                categoria
                        )
        );

        JFreeChart chart =
                ChartFactory.createBarChart(
                        "Recursos Reservados por Categoría",
                        "Categoría",
                        "Cantidad",
                        dataset,
                        PlotOrientation.VERTICAL,
                        false,
                        true,
                        false
                );

        chart.setBackgroundPaint(
                java.awt.Color.WHITE
        );

        vista.mostrarGrafico(chart);
    }

    private void generarGraficoActividades(
            Map<String, Integer> datos) {

        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();

        datos.forEach(
                (semana, cantidad) ->
                        dataset.addValue(
                                cantidad,
                                "Actividades",
                                semana
                        )
        );

        JFreeChart chart =
                ChartFactory.createBarChart(
                        "Actividades Programadas por Semana",
                        "Semana",
                        "Cantidad de Actividades",
                        dataset,
                        PlotOrientation.VERTICAL,
                        false,
                        true,
                        false
                );

        chart.setBackgroundPaint(
                java.awt.Color.WHITE
        );

        vista.mostrarGrafico(chart);
    }

    private void generarPDF() {

        if (reservasActuales == null
                || reservasActuales.isEmpty()) {

            vista.mostrarMensaje(
                    "Primero debe generar una estadística."
            );

            return;
        }

        try {

            File archivo;

            if (vista.getTipoGraficoSeleccionado() == 0) {

                archivo =
                        ReportePDFService
                                .generarEstadisticasCategorias(
                                        vista.getFechaInicio(),
                                        vista.getFechaFin(),
                                        datosActuales
                                );

            } else {

                archivo =
                        ReportePDFService
                                .generarEstadisticasActividades(
                                        vista.getFechaInicio(),
                                        vista.getFechaFin(),
                                        datosActuales
                                );
            }

            int opcion =
                    javax.swing.JOptionPane.showConfirmDialog(
                            null,
                            "PDF generado correctamente.\n"
                                    + archivo.getAbsolutePath()
                                    + "\n\n¿Desea abrirlo?",
                            "Reporte PDF",
                            javax.swing.JOptionPane.YES_NO_OPTION
                    );

            if (opcion ==
                    javax.swing.JOptionPane.YES_OPTION) {

                Desktop.getDesktop().open(archivo);
            }

        } catch (Exception ex) {

            vista.mostrarMensaje(
                    "Error al generar PDF: "
                            + ex.getMessage()
            );
        }
    }
}