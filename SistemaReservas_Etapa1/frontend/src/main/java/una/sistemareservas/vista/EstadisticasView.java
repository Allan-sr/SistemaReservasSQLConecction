package una.sistemareservas.vista;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class EstadisticasView extends JFrame {

    private JSpinner spinnerFechaInicio;
    private JSpinner spinnerFechaFin;
    private JButton btnGenerar;
    private JButton btnPDF;
    private JComboBox<String> comboTipoGrafico;
    private JPanel panelGrafico;

    public EstadisticasView() {
        setTitle("Estadísticas e Informes de Reservas");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel Superior: Controles y Filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));

        panelFiltros.add(new JLabel("Fecha Inicio:"));
        spinnerFechaInicio = crearSpinnerFecha(LocalDate.now().minusMonths(1));
        panelFiltros.add(spinnerFechaInicio);

        panelFiltros.add(new JLabel("Fecha Fin:"));
        spinnerFechaFin = crearSpinnerFecha(LocalDate.now().plusMonths(1));
        panelFiltros.add(spinnerFechaFin);

        panelFiltros.add(new JLabel("Tipo de Reporte:"));
        comboTipoGrafico = new JComboBox<>(new String[]{
                "Recursos por categoría",
                "Actividades por semana"
        });
        panelFiltros.add(comboTipoGrafico);

        btnGenerar = new JButton("Generar Gráfico");
        panelFiltros.add(btnGenerar);

        btnPDF = new JButton("Generar PDF");
        panelFiltros.add(btnPDF);

        add(panelFiltros, BorderLayout.NORTH);

        // Panel Central: Render del Gráfico
        panelGrafico = new JPanel(new BorderLayout());
        panelGrafico.setBorder(BorderFactory.createEtchedBorder());
        panelGrafico.add(new JLabel("Seleccione los filtros y haga clic en 'Generar Gráfico'", SwingConstants.CENTER), BorderLayout.CENTER);

        add(panelGrafico, BorderLayout.CENTER);
    }

    private JSpinner crearSpinnerFecha(LocalDate fechaInicial) {
        Date date = Date.from(fechaInicial.atStartOfDay(ZoneId.systemDefault()).toInstant());
        SpinnerDateModel model = new SpinnerDateModel(date, null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);
        return spinner;
    }

    public void mostrarGrafico(JFreeChart chart) {
        panelGrafico.removeAll();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        panelGrafico.add(chartPanel, BorderLayout.CENTER);
        panelGrafico.validate();
        panelGrafico.repaint();
    }

    public LocalDate getFechaInicio() {
        Date date = (Date) spinnerFechaInicio.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public LocalDate getFechaFin() {
        Date date = (Date) spinnerFechaFin.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public int getTipoGraficoSeleccionado() {
        return comboTipoGrafico.getSelectedIndex();
    }
    public JButton getBtnGenerar() {
        return btnGenerar;
    }
    public JButton getBtnPDF() {return btnPDF;}
    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}