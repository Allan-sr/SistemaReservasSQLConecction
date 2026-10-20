package una.sistemareservas.vista;

import una.sistemareservas.modelo.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoriasView extends JFrame {

    private JTextField txtBuscarDescripcion;
    private JButton btnBuscar;
    private JButton btnImprimir;

    private JTextField txtId;
    private JTextField txtDescripcion;

    private JButton btnGuardar;
    private JButton btnBorrar;
    private JButton btnLimpiar;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public CategoriasView() {

        setTitle("Categorías de recursos");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarComponentes();
    }

    private void inicializarComponentes() {

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panelPrincipal.add(construirPanelBusqueda(), BorderLayout.NORTH);
        panelPrincipal.add(construirPanelFormulario(), BorderLayout.CENTER);
        panelPrincipal.add(construirPanelListado(), BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private JPanel construirPanelBusqueda() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Búsqueda"));

        panel.add(new JLabel("Descripción:"));
        txtBuscarDescripcion = new JTextField(18);
        panel.add(txtBuscarDescripcion);

        btnBuscar = new JButton("Buscar");
        panel.add(btnBuscar);

        btnImprimir = new JButton("Imprimir");
        panel.add(btnImprimir);

        return panel;
    }

    private JPanel construirPanelFormulario() {

        JPanel panelExterno = new JPanel(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Categoría"));

        panel.add(new JLabel("Id:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panel.add(txtId);

        panel.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField();
        panel.add(txtDescripcion);

        panelExterno.add(panel, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        btnGuardar = new JButton("Guardar");
        btnBorrar = new JButton("Borrar");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnBorrar);
        panelBotones.add(btnLimpiar);

        panelExterno.add(panelBotones, BorderLayout.SOUTH);

        return panelExterno;
    }

    private JPanel construirPanelListado() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Listado"));

        modeloTabla = new DefaultTableModel(
                new Object[]{"Id", "Descripción"}, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(22);
        tabla.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(550, 200));

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    public String getBuscarDescripcion() {
        return txtBuscarDescripcion.getText().trim();
    }

    public String getId() {
        return txtId.getText().trim();
    }

    public String getDescripcion() {
        return txtDescripcion.getText().trim();
    }

    public void setId(String id) {
        txtId.setText(id);
    }

    public void setDescripcion(String descripcion) {
        txtDescripcion.setText(descripcion);
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    public JButton getBtnImprimir() {
        return btnImprimir;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnBorrar() {
        return btnBorrar;
    }

    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }

    public JTable getTabla() {
        return tabla;
    }

    public void limpiarFormulario() {

        txtId.setText("");
        txtDescripcion.setText("");

        tabla.clearSelection();
    }

    public void cargarTabla(List<Categoria> categorias) {

        modeloTabla.setRowCount(0);

        for (Categoria categoria : categorias) {

            modeloTabla.addRow(new Object[]{
                    categoria.getId(),
                    categoria.getDescripcion()
            });
        }
    }

    public int getFilaSeleccionada() {
        return tabla.getSelectedRow();
    }

    public String getValorCelda(int fila, int columna) {
        return (String) modeloTabla.getValueAt(fila, columna);
    }

    public void mostrarMensaje(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Categorías",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public boolean confirmar(String mensaje) {

        int opcion = JOptionPane.showConfirmDialog(
                this,
                mensaje,
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        return opcion == JOptionPane.YES_OPTION;
    }
}