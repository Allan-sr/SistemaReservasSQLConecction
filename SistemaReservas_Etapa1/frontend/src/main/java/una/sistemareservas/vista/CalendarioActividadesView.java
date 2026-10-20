package una.sistemareservas.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CalendarioActividadesView extends JFrame {

    private JSpinner spinnerSemana;
    private JButton btnConsultar;
    private JButton btnPDF;

    private JTable tablaCalendario;
    private DefaultTableModel modelTabla;

    public CalendarioActividadesView() {

        setTitle("Calendarización de Actividades");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {

        setLayout(new BorderLayout(10, 10));

        JPanel panelFiltros =
                new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        panelFiltros.setBorder(
                BorderFactory.createTitledBorder(
                        "Consulta semanal"
                )
        );

        panelFiltros.add(
                new JLabel("Semana:")
        );

        spinnerSemana =
                crearSpinnerFecha(LocalDate.now());

        panelFiltros.add(spinnerSemana);

        btnConsultar =
                new JButton("Consultar");

        panelFiltros.add(btnConsultar);

        btnPDF =
                new JButton("Generar PDF");

        panelFiltros.add(btnPDF);

        add(
                panelFiltros,
                BorderLayout.NORTH
        );

        String[] columnas = {
                "Hora",
                "Lunes",
                "Martes",
                "Miércoles",
                "Jueves",
                "Viernes",
                "Sábado",
                "Domingo"
        };

        modelTabla =
                new DefaultTableModel(
                        columnas,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        tablaCalendario =
                new JTable(modelTabla);

        tablaCalendario.setRowHeight(55);

        tablaCalendario.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        tablaCalendario
                .getTableHeader()
                .setReorderingAllowed(false);

        JScrollPane scrollPane =
                new JScrollPane(tablaCalendario);

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );
    }

    private JSpinner crearSpinnerFecha(
            LocalDate fecha) {

        Date date =
                Date.from(
                        fecha.atStartOfDay(
                                ZoneId.systemDefault()
                        ).toInstant()
                );

        SpinnerDateModel model =
                new SpinnerDateModel(
                        date,
                        null,
                        null,
                        java.util.Calendar.DAY_OF_MONTH
                );

        JSpinner spinner =
                new JSpinner(model);

        JSpinner.DateEditor editor =
                new JSpinner.DateEditor(
                        spinner,
                        "dd/MM/yyyy"
                );

        spinner.setEditor(editor);

        return spinner;
    }

    public LocalDate getFechaSeleccionada() {

        Date date =
                (Date) spinnerSemana.getValue();

        return date.toInstant()
                .atZone(
                        ZoneId.systemDefault()
                )
                .toLocalDate();
    }

    public JButton getBtnConsultar() {
        return btnConsultar;
    }

    public JButton getBtnPDF() {
        return btnPDF;
    }

    public DefaultTableModel getModelTabla() {
        return modelTabla;
    }

    public JTable getTablaCalendario() {
        return tablaCalendario;
    }

    public void mostrarMensaje(
            String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje
        );
    }
}
