package una.sistemareservas.vista;

import una.sistemareservas.modelo.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class CalendarioRecursosView extends JFrame {

    private JSpinner spinnerFecha;
    private JComboBox<Categoria> cmbCategoria;
    private JButton btnConsultar;
    private JButton btnPDF;
    private JTable tablaCalendario;
    private DefaultTableModel modelTabla;


    public CalendarioRecursosView() {

        setTitle("Calendarización de Recursos");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Consulta de Recursos"));
        panelFiltros.add(new JLabel("Fecha:"));
        spinnerFecha = crearSpinnerFecha(LocalDate.now());
        panelFiltros.add(spinnerFecha);
        panelFiltros.add(new JLabel("Categoría:"));
        cmbCategoria = new JComboBox<>();
        cmbCategoria.setPreferredSize(new Dimension(250, 25));

        panelFiltros.add(cmbCategoria);
        btnConsultar = new JButton("Consultar");
        panelFiltros.add(btnConsultar);
        btnPDF = new JButton("Generar PDF");
        panelFiltros.add(btnPDF);

        add(panelFiltros, BorderLayout.NORTH);
        String[] columnas = {"Hora"};

        modelTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCalendario = new JTable(modelTabla);

        tablaCalendario.setRowHeight(50);

        tablaCalendario.setFont(
                new Font("Arial", Font.PLAIN, 12)
        );

        tablaCalendario.getTableHeader()
                .setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(
                tablaCalendario
        );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 10, 10
                )
        );

        add(scrollPane, BorderLayout.CENTER);
    }

    private JSpinner crearSpinnerFecha(LocalDate fecha) {

        Date date = Date.from(
                fecha.atStartOfDay(
                        ZoneId.systemDefault()
                ).toInstant()
        );

        SpinnerDateModel model = new SpinnerDateModel(
                date,
                null,
                null,
                java.util.Calendar.DAY_OF_MONTH
        );

        JSpinner spinner = new JSpinner(model);

        JSpinner.DateEditor editor =
                new JSpinner.DateEditor(
                        spinner,
                        "dd/MM/yyyy"
                );

        spinner.setEditor(editor);

        return spinner;
    }

    public LocalDate getFechaSeleccionada() {
        Date date = (Date) spinnerFecha.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void cargarCategorias(List<Categoria> categorias) {
        cmbCategoria.removeAllItems();
        for (Categoria categoria : categorias) {
            cmbCategoria.addItem(categoria);
        }
    }

    public Categoria getCategoriaSeleccionada() {
        return (Categoria) cmbCategoria.getSelectedItem();
    }

    public JButton getBtnConsultar() {
        return btnConsultar;
    }

    public JButton getBtnPDF() {return btnPDF;}

    public JTable getTablaCalendario() {
        return tablaCalendario;
    }

    public DefaultTableModel getModelTabla() {
        return modelTabla;
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}