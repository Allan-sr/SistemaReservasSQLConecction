package una.sistemareservas.controlador;

import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.negocio.CalendarioRecursosService;
import una.sistemareservas.vista.CalendarioRecursosView;

import una.sistemareservas.negocio.ReportePDFService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CalendarioRecursosController {

    private final CalendarioRecursosView vista;
    private final CalendarioRecursosService servicio;

    public CalendarioRecursosController(
            CalendarioRecursosView vista) {

        this.vista = vista;
        this.servicio =
                new CalendarioRecursosService();

        iniciarEventos();
        cargarCategorias();
    }

    private void iniciarEventos() {
        vista.getBtnConsultar().addActionListener(e -> consultar());
        vista.getBtnPDF().addActionListener(e -> generarPDF());
    }

    private void cargarCategorias() {

        List<Categoria> categorias =
                servicio.listarCategorias();

        vista.cargarCategorias(categorias);
    }

    private void consultar() {

        Categoria categoria =
                vista.getCategoriaSeleccionada();

        if (categoria == null) {

            vista.mostrarMensaje(
                    "Debe seleccionar una categoría."
            );

            return;
        }

        LocalDate fecha =
                vista.getFechaSeleccionada();

        List<Recurso> recursos =
                servicio.listarRecursosPorCategoria(
                        categoria.getId()
                );

        if (recursos.isEmpty()) {

            vista.mostrarMensaje(
                    "No existen recursos registrados para esta categoría."
            );

            limpiarTabla();

            return;
        }

        construirTabla(
                recursos,
                fecha
        );
    }

    private void construirTabla(
            List<Recurso> recursos,
            LocalDate fecha) {

        DefaultTableModel modelo =
                vista.getModelTabla();

        modelo.setRowCount(0);

        String[] columnas =
                new String[recursos.size() + 1];

        columnas[0] = "Hora";

        for (int i = 0;
             i < recursos.size();
             i++) {

            columnas[i + 1] =
                    recursos.get(i)
                            .getDescripcion();
        }

        modelo.setColumnIdentifiers(
                columnas
        );

        List<LocalTime> horas =
                servicio.obtenerHorasDelDia();

        for (LocalTime hora : horas) {

            Object[] fila =
                    new Object[recursos.size() + 1];

            fila[0] =
                    hora.toString();

            for (int i = 0;
                 i < recursos.size();
                 i++) {

                Recurso recurso =
                        recursos.get(i);

                Reserva reserva =
                        servicio.obtenerReservaDelRecurso(
                                recurso,
                                fecha,
                                hora
                        );

                if (reserva != null) {

                    fila[i + 1] =
                            reserva.getActividad();

                } else {

                    fila[i + 1] =
                            "";
                }
            }

            modelo.addRow(fila);
        }

        configurarTabla();
    }

    private void configurarTabla() {

        JTable tabla =
                vista.getTablaCalendario();

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {

                    @Override
                    public Component
                    getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column) {

                        Component componente =
                                super.getTableCellRendererComponent(
                                        table,
                                        value,
                                        isSelected,
                                        hasFocus,
                                        row,
                                        column
                                );

                        setHorizontalAlignment(
                                SwingConstants.CENTER
                        );

                        if (column > 0 &&
                                value != null &&
                                !value.toString()
                                        .isEmpty()) {

                            componente.setBackground(
                                    new Color(
                                            217,
                                            237,
                                            247
                                    )
                            );

                            componente.setFont(
                                    componente.getFont()
                                            .deriveFont(
                                                    Font.BOLD
                                            )
                            );

                            setToolTipText(
                                    value.toString()
                            );

                        } else {

                            componente.setBackground(
                                    Color.WHITE
                            );

                            componente.setFont(
                                    componente.getFont()
                            );

                            setToolTipText(null);
                        }

                        if (isSelected) {

                            componente.setBackground(
                                    table.getSelectionBackground()
                            );
                        }

                        return componente;
                    }
                };

        for (int i = 1;
             i < tabla.getColumnCount();
             i++) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(renderer);
        }
    }

    private void limpiarTabla() {

        DefaultTableModel modelo =
                vista.getModelTabla();

        modelo.setRowCount(0);

        modelo.setColumnIdentifiers(
                new String[]{"Hora"}
        );
    }

    private void generarPDF() {

        Categoria categoria =
                vista.getCategoriaSeleccionada();

        if (categoria == null) {

            vista.mostrarMensaje(
                    "Debe seleccionar una categoría."
            );

            return;
        }

        LocalDate fecha =
                vista.getFechaSeleccionada();

        List<Recurso> recursos =
                servicio.listarRecursosPorCategoria(
                        categoria.getId()
                );

        if (recursos.isEmpty()) {

            vista.mostrarMensaje(
                    "No existen recursos registrados para esta categoría."
            );

            return;
        }

        try {

            Map<String, Reserva> reservasPorRecurso =
                    new HashMap<>();

            for (Recurso recurso : recursos) {

                for (LocalTime hora :
                        servicio.obtenerHorasDelDia()) {

                    Reserva reserva =
                            servicio.obtenerReservaDelRecurso(
                                    recurso,
                                    fecha,
                                    hora
                            );

                    if (reserva != null) {

                        reservasPorRecurso.put(
                                recurso.getId(),
                                reserva
                        );

                        break;
                    }
                }
            }

            File archivo =
                    ReportePDFService.generarCalendarioRecursos(
                            categoria,
                            fecha,
                            recursos,
                            reservasPorRecurso
                    );

            int opcion =
                    JOptionPane.showConfirmDialog(
                            vista,
                            "PDF generado correctamente.\n"
                                    + archivo.getAbsolutePath()
                                    + "\n\n¿Desea abrirlo?",
                            "Reporte PDF",
                            JOptionPane.YES_NO_OPTION
                    );

            if (opcion == JOptionPane.YES_OPTION) {

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