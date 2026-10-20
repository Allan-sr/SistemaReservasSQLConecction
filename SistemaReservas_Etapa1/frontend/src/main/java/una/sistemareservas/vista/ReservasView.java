package una.sistemareservas.vista;

import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.modelo.Reserva;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ReservasView extends JFrame {

    private JTextField txtFrase;
    private JButton btnExtraerIA;

    private JTextField txtActividad;
    private JSpinner spnFecha;
    private JSpinner spnHoraInicio;
    private JSpinner spnHoraFin;

    private JList<Categoria> listCategorias;
    private DefaultListModel<Categoria> modeloListaCategorias;

    private JButton btnReservar;
    private JButton btnCancelar;
    private JButton btnLimpiar;

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnImprimir;

    public ReservasView() {

        setTitle("Reservas");

        setSize(1100, 700);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        inicializarComponentes();
    }

    private static final Color AZUL =
            new Color(28, 111, 232);

    private static final Color AZUL_OSCURO =
            new Color(22, 57, 112);

    private static final Color FONDO =
            new Color(244, 248, 253);

    private static final Color BORDE =
            new Color(220, 229, 241);

    private static final Color TEXTO =
            new Color(22, 39, 73);

    private JPanel tarjeta(
            String titulo,
            String subtitulo) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                20,
                                18,
                                20
                        )
                )
        );

        JPanel encabezado =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        encabezado.setOpaque(false);

        JLabel circulo =
                new JLabel(
                        "+",
                        SwingConstants.CENTER
                );

        circulo.setPreferredSize(
                new Dimension(
                        42,
                        42
                )
        );

        circulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        25
                )
        );

        circulo.setForeground(
                Color.WHITE
        );

        circulo.setOpaque(true);

        circulo.setBackground(
                AZUL
        );

        JPanel textos =
                new JPanel();

        textos.setOpaque(false);

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel lblTitulo =
                new JLabel(titulo);

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        lblTitulo.setForeground(
                AZUL_OSCURO
        );

        JLabel lblSubtitulo =
                new JLabel(subtitulo);

        lblSubtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        lblSubtitulo.setForeground(
                new Color(
                        95,
                        119,
                        153
                )
        );

        textos.add(lblTitulo);

        textos.add(
                Box.createVerticalStrut(3)
        );

        textos.add(lblSubtitulo);

        encabezado.add(
                circulo,
                BorderLayout.WEST
        );

        encabezado.add(
                textos,
                BorderLayout.CENTER
        );

        tarjeta.add(
                encabezado,
                BorderLayout.NORTH
        );

        return tarjeta;
    }

    private void inicializarComponentes() {

        JPanel fondo =
                new JPanel(
                        new BorderLayout(
                                0,
                                14
                        )
                );

        fondo.setBackground(FONDO);

        fondo.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        5,
                        5,
                        5
                )
        );

        /*
         * ============================================================
         * NUEVA RESERVA
         * ============================================================
         */

        JPanel tarjetaNueva =
                tarjeta(
                        "Nueva reserva",
                        "Completa la información para reservar un recurso"
                );

        JPanel formulario =
                new JPanel(
                        new GridBagLayout()
                );

        formulario.setOpaque(false);

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        6,
                        6,
                        6,
                        6
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        /*
         * FRASE
         */
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        JLabel lblFrase =
                crearEtiqueta(
                        "Frase"
                );

        formulario.add(
                lblFrase,
                gbc
        );

        txtFrase =
                new JTextField();

        estilizarCampo(
                txtFrase
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(
                txtFrase,
                gbc
        );

        btnExtraerIA =
                crearBotonAzul(
                        "Extraer"
                );

        gbc.gridx = 2;
        gbc.weightx = 0;

        formulario.add(
                btnExtraerIA,
                gbc
        );

        /*
         * ACTIVIDAD
         */
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        formulario.add(
                crearEtiqueta("Actividad"),
                gbc
        );

        txtActividad =
                new JTextField();

        estilizarCampo(
                txtActividad
        );

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        formulario.add(
                txtActividad,
                gbc
        );

        gbc.gridwidth = 1;

        /*
         * FECHA
         */
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        formulario.add(
                crearEtiqueta("Fecha"),
                gbc
        );

        spnFecha =
                new JSpinner(
                        new SpinnerDateModel()
                );

        spnFecha.setEditor(
                new JSpinner.DateEditor(
                        spnFecha,
                        "dd/MM/yyyy"
                )
        );

        estilizarSpinner(
                spnFecha
        );

        gbc.gridx = 1;
        gbc.weightx = 0.5;

        formulario.add(
                spnFecha,
                gbc
        );

        /*
         * HORA INICIO
         */
        gbc.gridx = 2;
        gbc.weightx = 0;

        formulario.add(
                crearEtiqueta("Hora inicio"),
                gbc
        );

        spnHoraInicio =
                new JSpinner(
                        new SpinnerDateModel()
                );

        spnHoraInicio.setEditor(
                new JSpinner.DateEditor(
                        spnHoraInicio,
                        "HH:mm"
                )
        );

        estilizarSpinner(
                spnHoraInicio
        );

        gbc.gridx = 3;
        gbc.weightx = 0.4;

        formulario.add(
                spnHoraInicio,
                gbc
        );

        /*
         * HORA FIN
         */
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 0;

        formulario.add(
                crearEtiqueta("Hora fin"),
                gbc
        );

        spnHoraFin =
                new JSpinner(
                        new SpinnerDateModel()
                );

        spnHoraFin.setEditor(
                new JSpinner.DateEditor(
                        spnHoraFin,
                        "HH:mm"
                )
        );

        estilizarSpinner(
                spnHoraFin
        );

        gbc.gridx = 3;
        gbc.weightx = 0.4;

        formulario.add(
                spnHoraFin,
                gbc
        );

        /*
         * CATEGORÍAS
         */
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;

        formulario.add(
                crearEtiqueta(
                        "Categorías requeridas"
                ),
                gbc
        );

        modeloListaCategorias =
                new DefaultListModel<>();

        listCategorias =
                new JList<>(
                        modeloListaCategorias
                );

        listCategorias.setSelectionMode(
                ListSelectionModel
                        .MULTIPLE_INTERVAL_SELECTION
        );

        listCategorias.setVisibleRowCount(
                3
        );

        listCategorias.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        listCategorias.setFixedCellHeight(
                34
        );

        listCategorias.setBackground(
                Color.WHITE
        );

        listCategorias.setSelectionBackground(
                new Color(
                        218,
                        234,
                        255
                )
        );

        listCategorias.setSelectionForeground(
                AZUL_OSCURO
        );

        listCategorias.setCellRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component
                    getListCellRendererComponent(
                            JList<?> lista,
                            Object valor,
                            int indice,
                            boolean seleccionado,
                            boolean enfocado) {

                        JLabel label =
                                (JLabel)
                                        super.getListCellRendererComponent(
                                                lista,
                                                valor,
                                                indice,
                                                seleccionado,
                                                enfocado
                                        );

                        label.setBorder(
                                BorderFactory.createEmptyBorder(
                                        5,
                                        8,
                                        5,
                                        8
                                )
                        );

                        if (seleccionado) {

                            label.setFont(
                                    new Font(
                                            "Segoe UI",
                                            Font.BOLD,
                                            13
                                    )
                            );
                        }

                        return label;
                    }
                }
        );

        JScrollPane scrollCategorias =
                new JScrollPane(
                        listCategorias
                );

        scrollCategorias.setBorder(
                BorderFactory.createLineBorder(
                        BORDE
                )
        );

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;

        formulario.add(
                scrollCategorias,
                gbc
        );

        /*
         * BOTONES
         */
        JPanel botones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                4
                        )
                );

        botones.setOpaque(false);

        btnReservar =
                crearBotonAzul(
                        "Reservar"
                );

        btnCancelar =
                crearBotonSecundario(
                        "Cancelar reserva seleccionada"
                );

        btnLimpiar =
                crearBotonRojo(
                        "Limpiar"
                );

        botones.add(
                btnReservar
        );

        botones.add(
                btnCancelar
        );

        botones.add(
                btnLimpiar
        );

        JPanel contenidoNueva =
                new JPanel(
                        new BorderLayout(
                                0,
                                10
                        )
                );

        contenidoNueva.setOpaque(false);

        contenidoNueva.add(
                formulario,
                BorderLayout.CENTER
        );

        contenidoNueva.add(
                botones,
                BorderLayout.SOUTH
        );

        tarjetaNueva.add(
                contenidoNueva,
                BorderLayout.CENTER
        );

        /*
         * ============================================================
         * MIS RESERVAS
         * ============================================================
         */

        JPanel tarjetaReservas =
                tarjeta(
                        "Mis reservas",
                        "Consulta, revisa o imprime tus reservas"
                );

        modeloTabla =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Actividad",
                                "Fecha",
                                "Horario",
                                "Recursos",
                                "Estado"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int fila,
                            int columna) {

                        return false;
                    }
                };

        tabla =
                new JTable(
                        modeloTabla
                );

        tabla.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        tabla.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                13
                        )
                );

        tabla.getTableHeader()
                .setBackground(
                        new Color(
                                239,
                                246,
                                255
                        )
                );

        tabla.getTableHeader()
                .setForeground(
                        AZUL_OSCURO
                );

        tabla.setRowHeight(32);

        tabla.setSelectionMode(
                ListSelectionModel
                        .SINGLE_SELECTION
        );

        tabla.setSelectionBackground(
                new Color(
                        218,
                        234,
                        255
                )
        );

        tabla.setShowVerticalLines(
                false
        );

        tabla.setGridColor(
                BORDE
        );

        JScrollPane scroll =
                new JScrollPane(
                        tabla
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        BORDE
                )
        );

        tarjetaReservas.add(
                scroll,
                BorderLayout.CENTER
        );

        JPanel botonImprimir =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                0,
                                8
                        )
                );

        botonImprimir.setOpaque(false);

        btnImprimir =
                crearBotonSecundario(
                        "Imprimir"
                );

        botonImprimir.add(
                btnImprimir
        );

        tarjetaReservas.add(
                botonImprimir,
                BorderLayout.SOUTH
        );

        /*
         * ============================================================
         * PANTALLA FINAL
         * ============================================================
         */

        JPanel contenido =
                new JPanel(
                        new BorderLayout(
                                0,
                                14
                        )
                );

        contenido.setOpaque(false);

        contenido.add(
                tarjetaNueva,
                BorderLayout.NORTH
        );

        contenido.add(
                tarjetaReservas,
                BorderLayout.CENTER
        );

        fondo.add(
                contenido,
                BorderLayout.CENTER
        );

        add(fondo);
    }

    private JLabel crearEtiqueta(
            String texto) {

        JLabel label =
                new JLabel(texto);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        label.setForeground(
                TEXTO
        );

        return label;
    }

    private void estilizarCampo(
            JTextField campo) {

        campo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        campo.setPreferredSize(
                new Dimension(
                        250,
                        38
                )
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                6,
                                10,
                                6,
                                10
                        )
                )
        );
    }

    private void estilizarSpinner(
            JSpinner spinner) {

        spinner.setPreferredSize(
                new Dimension(
                        180,
                        38
                )
        );

        JComponent editor =
                spinner.getEditor();

        if (editor instanceof JSpinner.DefaultEditor) {

            JTextField campo =
                    ((JSpinner.DefaultEditor)
                            editor)
                            .getTextField();

            campo.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            14
                    )
            );

            campo.setBorder(
                    BorderFactory.createEmptyBorder(
                            5,
                            8,
                            5,
                            8
                    )
            );
        }

        spinner.setBorder(
                BorderFactory.createLineBorder(
                        BORDE
                )
        );
    }

    private JButton crearBotonAzul(
            String texto) {

        JButton boton =
                new JButton(texto);

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        boton.setForeground(
                Color.WHITE
        );

        boton.setBackground(
                AZUL
        );

        boton.setFocusPainted(false);

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        22,
                        10,
                        22
                )
        );

        return boton;
    }

    private JButton crearBotonSecundario(
            String texto) {

        JButton boton =
                new JButton(texto);

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        boton.setForeground(
                AZUL_OSCURO
        );

        boton.setBackground(
                new Color(
                        239,
                        246,
                        255
                )
        );

        boton.setFocusPainted(false);

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        190,
                                        213,
                                        244
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                14,
                                9,
                                14
                        )
                )
        );

        return boton;
    }

    private JButton crearBotonRojo(
            String texto) {

        JButton boton =
                new JButton(texto);

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        boton.setForeground(
                new Color(
                        190,
                        35,
                        35
                )
        );

        boton.setBackground(
                new Color(
                        255,
                        244,
                        244
                )
        );

        boton.setFocusPainted(false);

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        245,
                                        170,
                                        170
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                14,
                                9,
                                14
                        )
                )
        );

        return boton;
    }


    public void cargarCategorias(List<Categoria> categorias) {
        DefaultListModel<Categoria> model = new DefaultListModel<>();
        if (categorias != null) {
            for (Categoria cat : categorias) {
                model.addElement(cat);
            }
        }
        this.listCategorias.setModel(model); // Cambia 'listCategorias' por el nombre de tu JList
    }

    public void cargarTabla(List<Reserva> reservas) {

        modeloTabla.setRowCount(0);

        for (Reserva reserva : reservas) {

            String horario = reserva.getHoraInicio() + " - " + reserva.getHoraFin();

            StringBuilder recursos = new StringBuilder();
            for (Recurso recurso : reserva.getRecursos()) {
                if (recursos.length() > 0) {
                    recursos.append(", ");
                }
                recursos.append(recurso.getId());
            }

            modeloTabla.addRow(new Object[]{
                    reserva.getId(),
                    reserva.getActividad(),
                    reserva.getFecha(),
                    horario,
                    recursos.toString(),
                    reserva.getEstado()
            });
        }
    }

    public String getFrase() {
        return txtFrase.getText().trim();
    }

    public void setFrase(String frase) {
        txtFrase.setText(frase);
    }

    public String getActividad() {
        return txtActividad.getText().trim();
    }

    public void setActividad(String actividad) {
        txtActividad.setText(actividad);
    }

    public LocalDate getFecha() {
        Date fecha = (Date) spnFecha.getValue();
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void setFecha(LocalDate fecha) {
        Date valor = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
        spnFecha.setValue(valor);
    }

    public LocalTime getHoraInicio() {
        return convertirAHora((Date) spnHoraInicio.getValue());
    }

    public void setHoraInicio(LocalTime hora) {
        spnHoraInicio.setValue(convertirADate(hora));
    }

    public LocalTime getHoraFin() {
        return convertirAHora((Date) spnHoraFin.getValue());
    }

    public void setHoraFin(LocalTime hora) {
        spnHoraFin.setValue(convertirADate(hora));
    }

    private LocalTime convertirAHora(Date fecha) {
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
                .withSecond(0).withNano(0);
    }

    private Date convertirADate(LocalTime hora) {
        return Date.from(
                LocalDate.now().atTime(hora).atZone(ZoneId.systemDefault()).toInstant()
        );
    }

    /**
     * Retorna los ids de las categorías seleccionadas en la lista de selección múltiple.
     */
    public List<String> getCategoriaIdsSeleccionadas() {
        return listCategorias.getSelectedValuesList()
                .stream()
                .map(Categoria::getId)
                .toList();
    }

    public int getFilaSeleccionada() {
        return tabla.getSelectedRow();
    }

    public String getValorCelda(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        return (valor == null) ? "" : valor.toString();
    }

    public JButton getBtnReservar() {
        return btnReservar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }

    public JButton getBtnImprimir() {
        return btnImprimir;
    }

    public JTable getTabla() {
        return tabla;
    }

    /**
     * Limpia el formulario de nueva reserva (no toca el listado de "Mis reservas").
     */
    public void limpiarFormulario() {

        txtFrase.setText("");
        txtActividad.setText("");

        spnFecha.setValue(new Date());
        setHoraInicio(LocalTime.of(8, 0));
        setHoraFin(LocalTime.of(9, 0));

        listCategorias.clearSelection();
    }

    public void mostrarMensaje(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Reservas",
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

    public JTextField getTxtFrase() {
        return txtFrase;
    }

    public JButton getBtnExtraerIA() {
        return btnExtraerIA;
    }

    // Métodos para llenar los campos del formulario con el resultado de la IA
    public void setTxtActividad(String actividad) {
        txtActividad.setText(actividad); // Cambia txtActividad según el nombre exacto de tu campo
    }

    public void seleccionarCategoriasPorNombre(List<String> nombresCategorias) {
        if (nombresCategorias == null || listCategorias == null) return;

        ListModel<Categoria> model = listCategorias.getModel();
        int[] indicesToSelect = new int[model.getSize()];
        int count = 0;

        for (int i = 0; i < model.getSize(); i++) {
            Categoria cat = model.getElementAt(i);
            for (String nombreBuscado : nombresCategorias) {
                // Comparación que ignora mayúsculas y espacios
                if (cat.getDescripcion().trim().equalsIgnoreCase(nombreBuscado.trim())) {
                    indicesToSelect[count++] = i;
                    break;
                }
            }
        }
        listCategorias.setSelectedIndices(java.util.Arrays.copyOf(indicesToSelect, count));
    }

    public JTable getTablaReservas() {
        return tabla;
    }
}
