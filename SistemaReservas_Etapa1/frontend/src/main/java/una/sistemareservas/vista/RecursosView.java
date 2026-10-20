package una.sistemareservas.vista;

import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Recurso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RecursosView extends JFrame {

    private JComboBox<Categoria> cmbFiltroCategoria;
    private JTextField txtFiltroDescripcion;
    private JButton btnBuscar;
    private JButton btnImprimir;

    private JTextField txtId;
    private JComboBox<Categoria> cmbCategoria;
    private JTextField txtDescripcion;

    private JButton btnGuardar;
    private JButton btnBorrar;
    private JButton btnLimpiar;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public RecursosView() {

        setTitle("Recursos");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarComponentes();
    }

    private void inicializarComponentes() {

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panelPrincipal.add(construirPanelFiltro(), BorderLayout.NORTH);
        panelPrincipal.add(construirPanelFormulario(), BorderLayout.CENTER);
        panelPrincipal.add(construirPanelListado(), BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private JPanel construirPanelFiltro() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Filtro"));

        panel.add(new JLabel("Categoría:"));
        cmbFiltroCategoria = new JComboBox<>();
        cmbFiltroCategoria.setPreferredSize(new Dimension(180, 25));
        panel.add(cmbFiltroCategoria);

        panel.add(new JLabel("Descripción:"));
        txtFiltroDescripcion = new JTextField(14);
        panel.add(txtFiltroDescripcion);

        btnBuscar = new JButton("Buscar");
        panel.add(btnBuscar);

        btnImprimir = new JButton("Imprimir");
        panel.add(btnImprimir);

        return panel;
    }

    private JPanel construirPanelFormulario() {

        JPanel panelExterno = new JPanel(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Recurso"));

        panel.add(new JLabel("Id (número de activo):"));
        txtId = new JTextField();
        panel.add(txtId);

        panel.add(new JLabel("Categoría:"));
        cmbCategoria = new JComboBox<>();
        panel.add(cmbCategoria);

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
                new Object[]{"Id", "Categoría", "Descripción"}, 0) {

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
        scroll.setPreferredSize(new Dimension(650, 200));

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Llena ambos combos (filtro y formulario) con las categorías disponibles.
     * Se antepone una opción vacía en el combo de filtro para representar "todas".
     */
    public void cargarCategorias(List<Categoria> categorias) {

        cmbFiltroCategoria.removeAllItems();
        cmbFiltroCategoria.addItem(null);
        for (Categoria categoria : categorias) {
            cmbFiltroCategoria.addItem(categoria);
        }

        cmbCategoria.removeAllItems();
        for (Categoria categoria : categorias) {
            cmbCategoria.addItem(categoria);
        }
    }

    public String getFiltroCategoriaId() {

        Categoria seleccionada = (Categoria) cmbFiltroCategoria.getSelectedItem();

        return (seleccionada == null) ? "" : seleccionada.getId();
    }

    public String getFiltroDescripcion() {
        return txtFiltroDescripcion.getText().trim();
    }

    public String getId() {
        return txtId.getText().trim();
    }

    public String getCategoriaId() {

        Categoria seleccionada = (Categoria) cmbCategoria.getSelectedItem();

        return (seleccionada == null) ? "" : seleccionada.getId();
    }

    public String getDescripcion() {
        return txtDescripcion.getText().trim();
    }

    public void setId(String id) {
        txtId.setText(id);
    }

    public void setCategoriaSeleccionada(String categoriaId) {

        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {

            Categoria categoria = cmbCategoria.getItemAt(i);

            if (categoria != null && categoria.getId().equalsIgnoreCase(categoriaId)) {
                cmbCategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    public void setDescripcion(String descripcion) {
        txtDescripcion.setText(descripcion);
    }

    public void deshabilitarId() {
        txtId.setEditable(false);
    }

    public void habilitarId() {
        txtId.setEditable(true);
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

        if (cmbCategoria.getItemCount() > 0) {
            cmbCategoria.setSelectedIndex(0);
        }

        habilitarId();

        tabla.clearSelection();
    }

    public void cargarTabla(List<Recurso> recursos) {

        modeloTabla.setRowCount(0);

        for (Recurso recurso : recursos) {

            String nombreCategoria =
                    (recurso.getCategoria() != null)
                            ? recurso.getCategoria().getDescripcion()
                            : recurso.getCategoriaId();

            modeloTabla.addRow(new Object[]{
                    recurso.getId(),
                    nombreCategoria,
                    recurso.getDescripcion()
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
                "Recursos",
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