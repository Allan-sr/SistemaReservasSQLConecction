package una.sistemareservas.vista;

import una.sistemareservas.modelo.Funcionario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FuncionariosView extends JFrame {

    private JTextField txtBuscarId;
    private JTextField txtBuscarNombre;
    private JButton btnBuscar;
    private JButton btnImprimir;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtTelefono;

    private JButton btnGuardar;
    private JButton btnBorrar;
    private JButton btnLimpiar;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public FuncionariosView() {

        setTitle("Funcionarios");
        setSize(650, 550);
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

        panel.add(new JLabel("Id:"));
        txtBuscarId = new JTextField(8);
        panel.add(txtBuscarId);

        panel.add(new JLabel("Nombre:"));
        txtBuscarNombre = new JTextField(12);
        panel.add(txtBuscarNombre);

        btnBuscar = new JButton("Buscar");
        panel.add(btnBuscar);

        btnImprimir = new JButton("Imprimir");
        panel.add(btnImprimir);

        return panel;
    }

    private JPanel construirPanelFormulario() {

        JPanel panelExterno = new JPanel(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Funcionario"));

        panel.add(new JLabel("Id:"));
        txtId = new JTextField();
        panel.add(txtId);

        panel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panel.add(txtNombre);

        panel.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        panel.add(txtTelefono);

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
                new Object[]{"Id", "Nombre", "Teléfono"}, 0) {

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
        scroll.setPreferredSize(new Dimension(600, 200));

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    public String getBuscarId() {
        return txtBuscarId.getText().trim();
    }

    public String getBuscarNombre() {
        return txtBuscarNombre.getText().trim();
    }

    public String getId() {
        return txtId.getText().trim();
    }

    public String getNombre() {
        return txtNombre.getText().trim();
    }

    public String getTelefono() {
        return txtTelefono.getText().trim();
    }

    public void setId(String id) {
        txtId.setText(id);
    }

    public void setNombre(String nombre) {
        txtNombre.setText(nombre);
    }

    public void setTelefono(String telefono) {
        txtTelefono.setText(telefono);
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

    public JButton getBtnImprimir() {return btnImprimir;}

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
        txtNombre.setText("");
        txtTelefono.setText("");

        habilitarId();

        tabla.clearSelection();
    }

    public void cargarTabla(List<Funcionario> funcionarios) {

        modeloTabla.setRowCount(0);

        for (Funcionario funcionario : funcionarios) {

            modeloTabla.addRow(new Object[]{
                    funcionario.getId(),
                    funcionario.getNombre(),
                    funcionario.getTelefono()
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
                "Funcionarios",
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
