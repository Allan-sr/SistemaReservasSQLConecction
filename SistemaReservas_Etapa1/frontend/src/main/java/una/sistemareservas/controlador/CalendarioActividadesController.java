package una.sistemareservas.controlador;

import una.sistemareservas.modelo.Funcionario;
import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.negocio.CalendarioActividadesService;
import una.sistemareservas.negocio.ReportePDFService;
import una.sistemareservas.vista.CalendarioActividadesView;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CalendarioActividadesController {

    private final CalendarioActividadesView vista;
    private final CalendarioActividadesService servicio;

    private List<Reserva> reservasActuales;
    private LocalDate lunesActual;

    public CalendarioActividadesController(
            CalendarioActividadesView vista) {

        this.vista = vista;

        this.servicio =
                new CalendarioActividadesService();

        iniciarEventos();

        consultar();
    }

    private void iniciarEventos() {

        vista.getBtnConsultar()
                .addActionListener(
                        e -> consultar()
                );

        vista.getBtnPDF()
                .addActionListener(
                        e -> generarPDF()
                );
    }

    private void consultar() {

        LocalDate fecha =
                vista.getFechaSeleccionada();

        lunesActual =
                servicio.obtenerLunes(fecha);

        reservasActuales =
                servicio.listarReservasSemana(fecha);

        construirTabla();
    }

    private void construirTabla() {

        DefaultTableModel modelo =
                vista.getModelTabla();

        modelo.setRowCount(0);

        DateTimeFormatter formatoHora =
                DateTimeFormatter.ofPattern("HH:mm");

        for (int hora = 0; hora < 24; hora++) {

            LocalTime horaActual =
                    LocalTime.of(hora, 0);

            Object[] fila =
                    new Object[8];

            fila[0] =
                    horaActual.format(formatoHora);

            for (int dia = 0; dia < 7; dia++) {

                LocalDate fecha =
                        lunesActual.plusDays(dia);

                Reserva reserva =
                        buscarReserva(
                                fecha,
                                horaActual
                        );

                if (reserva != null) {

                    String funcionario =
                            obtenerNombreFuncionario(
                                    reserva
                            );

                    fila[dia + 1] =
                            "<html>"
                                    + reserva.getActividad()
                                    + "<br>Funcionario: "
                                    + funcionario
                                    + "</html>";

                } else {

                    fila[dia + 1] = "";
                }
            }

            modelo.addRow(fila);
        }

        configurarTabla();
    }

    private Reserva buscarReserva(
            LocalDate fecha,
            LocalTime hora) {

        for (Reserva reserva :
                reservasActuales) {

            if (!reserva.getFecha()
                    .equals(fecha)) {

                continue;
            }

            if (!hora.isBefore(
                    reserva.getHoraInicio()
            )
                    &&
                    hora.isBefore(
                            reserva.getHoraFin()
                    )) {

                return reserva;
            }
        }

        return null;
    }

    private String obtenerNombreFuncionario(
            Reserva reserva) {

        Funcionario funcionario =
                reserva.getFuncionario();

        if (funcionario != null) {

            return funcionario.getNombre();
        }

        if (reserva.getFuncionarioId() != null) {

            return reserva.getFuncionarioId();
        }

        return "N/A";
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

                        if (column > 0
                                && value != null
                                && !value.toString().isEmpty()) {

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

                        } else {

                            componente.setBackground(
                                    Color.WHITE
                            );
                        }

                        return componente;
                    }
                };

        tabla.setDefaultRenderer(
                Object.class,
                renderer
        );
    }

    private void generarPDF() {

        if (reservasActuales == null) {
            consultar();
        }

        try {

            File archivo = ReportePDFService.generarCalendarioActividades(lunesActual, reservasActuales);
            int opcion =
                    JOptionPane.showConfirmDialog(
                            null,
                            "PDF generado correctamente.\n"
                                    + archivo.getAbsolutePath()
                                    + "\n\n¿Desea abrirlo?",
                            "Reporte PDF",
                            JOptionPane.YES_NO_OPTION
                    );

            if (opcion ==
                    JOptionPane.YES_OPTION) {

                Desktop.getDesktop()
                        .open(archivo);
            }

        } catch (Exception ex) {

            vista.mostrarMensaje(
                    "Error al generar PDF: "
                            + ex.getMessage()
            );
        }
    }
}
