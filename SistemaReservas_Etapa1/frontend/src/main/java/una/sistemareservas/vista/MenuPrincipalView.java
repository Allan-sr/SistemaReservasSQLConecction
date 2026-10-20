package una.sistemareservas.vista;

import una.sistemareservas.controlador.CalendarioActividadesController;
import una.sistemareservas.vista.CalendarioActividadesView;

import una.sistemareservas.controlador.CalendarioRecursosController;
import una.sistemareservas.controlador.CategoriasController;
import una.sistemareservas.controlador.EstadisticasController;
import una.sistemareservas.controlador.FuncionariosController;
import una.sistemareservas.controlador.RecursosController;
import una.sistemareservas.controlador.ReservaController;
import una.sistemareservas.modelo.Rol;
import una.sistemareservas.modelo.Usuario;
import una.sistemareservas.util.Sesion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class MenuPrincipalView extends JFrame {

    private static final Color AZUL = new Color(28, 111, 232);
    private static final Color AZUL_OSCURO = new Color(22, 57, 112);
    private static final Color FONDO = new Color(244, 248, 253);
    private static final Color BORDE = new Color(220, 229, 241);
    private static final Color TEXTO = new Color(22, 39, 73);

    private JTabbedPane pestanas;

    private JButton btnCerrarSesion;
    private JButton btnCambiarClave;

    public MenuPrincipalView() {

        Usuario usuario = Sesion.getUsuarioActual();
        setTitle("Sistema de Reservas - " + usuario.getId());
        setSize(1220, 760);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configurarLookVisual();
        inicializarComponentes();
        configurarEventos();
    }

    private void configurarLookVisual() {

        getContentPane().setBackground(FONDO);
        UIManager.put("TabbedPane.selected", Color.WHITE);
        UIManager.put("Table.selectionBackground", new Color(214, 231, 252));
        UIManager.put("Table.selectionForeground", TEXTO);
        UIManager.put("Table.gridColor", BORDE);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", TEXTO);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("ComboBox.background", Color.WHITE);
    }

    private void inicializarComponentes() {
        Usuario usuario = Sesion.getUsuarioActual();
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FONDO);
        raiz.setBorder(new EmptyBorder(16, 18, 16, 18));
        raiz.add(construirEncabezado(usuario),
                BorderLayout.NORTH
        );

        pestanas =
                new JTabbedPane(
                        JTabbedPane.TOP
                );

        pestanas.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        pestanas.setBackground(Color.WHITE);

        pestanas.setForeground(
                AZUL_OSCURO
        );

        pestanas.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        0,
                        0,
                        0
                )
        );

        pestanas.setUI(
                new TabsModernosUI()
        );

        /*
         * FUNCIONARIO
         */
        if (usuario.getRol() == Rol.FUNCIONARIO) {
            agregarReservas();
            agregarCalendarioRecursos();
            agregarCalendarioActividades();
            agregarEstadisticas();
        }

        /*
         * ADMINISTRADOR
         */
        else {
            agregarFuncionarios();
            agregarCategorias();
            agregarRecursos();
            agregarCalendarioRecursos();
            agregarCalendarioActividades();
            agregarEstadisticas();
        }

        JPanel contenedorTabs =
                new JPanel(
                        new BorderLayout()
                );

        contenedorTabs.setBackground(
                Color.WHITE
        );

        contenedorTabs.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        new EmptyBorder(
                                0,
                                0,
                                0,
                                0
                        )
                )
        );

        contenedorTabs.add(
                pestanas,
                BorderLayout.CENTER
        );

        raiz.add(
                contenedorTabs,
                BorderLayout.CENTER
        );

        add(raiz);
    }

    private JPanel construirEncabezado(
            Usuario usuario) {

        JPanel encabezado =
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        encabezado.setBackground(
                Color.WHITE
        );

        encabezado.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        new EmptyBorder(
                                12,
                                16,
                                12,
                                16
                        )
                )
        );

        /*
         * MARCA
         */
        JPanel marca =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                12,
                                0
                        )
                );

        marca.setOpaque(false);

        JLabel icono =
                new JLabel(
                        "▣",
                        SwingConstants.CENTER
                );

        icono.setPreferredSize(
                new Dimension(
                        50,
                        50
                )
        );

        icono.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        27
                )
        );

        icono.setForeground(
                Color.WHITE
        );

        icono.setOpaque(true);

        icono.setBackground(
                AZUL
        );

        icono.setBorder(
                BorderFactory.createEmptyBorder(
                        4,
                        4,
                        4,
                        4
                )
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

        JLabel titulo =
                new JLabel(
                        "Sistema de Reservas"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        titulo.setForeground(
                AZUL_OSCURO
        );

        JLabel subtitulo =
                new JLabel(
                        "Organiza · Reserva · Disfruta"
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        subtitulo.setForeground(
                new Color(
                        76,
                        126,
                        190
                )
        );

        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(2)
        );

        textos.add(subtitulo);

        marca.add(icono);

        marca.add(textos);

        /*
         * USUARIO
         */
        JPanel usuarioPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        usuarioPanel.setOpaque(false);

        JLabel avatar =
                new JLabel(
                        "●",
                        SwingConstants.CENTER
                );

        avatar.setPreferredSize(
                new Dimension(
                        44,
                        44
                )
        );

        avatar.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        avatar.setForeground(
                AZUL
        );

        avatar.setOpaque(true);

        avatar.setBackground(
                new Color(
                        228,
                        239,
                        255
                )
        );

        JPanel datos =
                new JPanel();

        datos.setOpaque(false);

        datos.setLayout(
                new BoxLayout(
                        datos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel nombre =
                new JLabel(
                        usuario.getNombreCompleto()
                );

        nombre.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        nombre.setForeground(TEXTO);

        JLabel rol =
                new JLabel(
                        usuario.getRol().toString()
                );

        rol.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        rol.setForeground(
                new Color(
                        92,
                        115,
                        145
                )
        );

        datos.add(nombre);

        datos.add(rol);

        btnCambiarClave =
                crearBotonSecundario(
                        "Cambiar clave"
                );

        btnCerrarSesion =
                crearBotonRojo(
                        "Cerrar sesión"
                );

        usuarioPanel.add(avatar);

        usuarioPanel.add(datos);

        usuarioPanel.add(
                btnCambiarClave
        );

        usuarioPanel.add(
                btnCerrarSesion
        );

        encabezado.add(
                marca,
                BorderLayout.WEST
        );

        encabezado.add(
                usuarioPanel,
                BorderLayout.EAST
        );

        return encabezado;
    }

    /*
     * ============================================================
     * PESTAÑAS
     * ============================================================
     */

    private void agregarReservas() {

        ReservasView vista =
                new ReservasView();

        new ReservaController(vista);

        pestanas.addTab(
                "Reservas",
                iconoTab("+"),
                prepararVista(vista)
        );
    }

    private void agregarFuncionarios() {

        FuncionariosView vista =
                new FuncionariosView();

        new FuncionariosController(vista);

        pestanas.addTab(
                "Funcionarios",
                iconoTab("F"),
                prepararVista(vista)
        );
    }

    private void agregarCategorias() {

        CategoriasView vista =
                new CategoriasView();

        new CategoriasController(vista);

        pestanas.addTab(
                "Categorías",
                iconoTab("C"),
                prepararVista(vista)
        );
    }

    private void agregarRecursos() {

        RecursosView vista =
                new RecursosView();

        new RecursosController(vista);

        pestanas.addTab(
                "Recursos",
                iconoTab("R"),
                prepararVista(vista)
        );
    }

    private void agregarCalendarioRecursos() {

        CalendarioRecursosView vista =
                new CalendarioRecursosView();

        new CalendarioRecursosController(vista);

        pestanas.addTab(
                "Calendario",
                iconoTab("▦"),
                prepararVista(vista)
        );
    }

    private void agregarCalendarioActividades() {

        CalendarioActividadesView vista =
                new CalendarioActividadesView();

        new CalendarioActividadesController(vista);

        pestanas.addTab(
                "Actividades",
                iconoTab("▦"),
                prepararVista(vista)
        );
    }

    private void agregarEstadisticas() {

        EstadisticasView vista =
                new EstadisticasView();

        new EstadisticasController(vista);

        pestanas.addTab(
                "Estadísticas",
                iconoTab("▥"),
                prepararVista(vista)
        );
    }

    /*
     * Convierte el contenido de las ventanas
     * existentes en contenido de pestañas.
     *
     * Los Controllers NO se modifican.
     */
    private JPanel prepararVista(
            JFrame vista) {

        Container contenidoOriginal =
                vista.getContentPane();

        vista.setContentPane(
                new JPanel()
        );

        vista.setVisible(false);

        JPanel contenedor =
                new JPanel(
                        new BorderLayout()
                );

        contenedor.setBackground(
                Color.WHITE
        );

        contenedor.setBorder(
                new EmptyBorder(
                        14,
                        14,
                        14,
                        14
                )
        );

        contenedor.add(
                contenidoOriginal,
                BorderLayout.CENTER
        );

        aplicarEstiloRecursivo(
                contenidoOriginal
        );

        return contenedor;
    }

    /*
     * ============================================================
     * ESTILO
     * ============================================================
     */

    private void aplicarEstiloRecursivo(
            Component componente) {

        if (componente instanceof JPanel) {

            JPanel panel =
                    (JPanel) componente;

            panel.setBackground(
                    Color.WHITE
            );
        }

        if (componente instanceof JLabel) {

            JLabel label =
                    (JLabel) componente;

            label.setForeground(TEXTO);
        }

        if (componente instanceof JTextField) {

            JTextField campo =
                    (JTextField) componente;

            campo.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            14
                    )
            );

            campo.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    BORDE
                            ),
                            BorderFactory.createEmptyBorder(
                                    7,
                                    9,
                                    7,
                                    9
                            )
                    )
            );
        }

        if (componente instanceof JTable) {

            JTable tabla =
                    (JTable) componente;

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

            tabla.setRowHeight(30);

            tabla.setShowVerticalLines(
                    false
            );

            tabla.setShowHorizontalLines(
                    true
            );
        }

        if (componente instanceof JButton) {

            JButton boton =
                    (JButton) componente;

            boton.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            13
                    )
            );

            boton.setFocusPainted(false);

            boton.setCursor(
                    new Cursor(
                            Cursor.HAND_CURSOR
                    )
            );
        }

        if (componente instanceof Container) {

            Container contenedor =
                    (Container) componente;

            for (Component hijo :
                    contenedor.getComponents()) {

                aplicarEstiloRecursivo(
                        hijo
                );
            }
        }
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
                                8,
                                12,
                                8,
                                12
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
                                8,
                                12,
                                8,
                                12
                        )
                )
        );

        return boton;
    }

    private Icon iconoTab(String texto) {

        return new ImageIcon(
                new java.awt.image.BufferedImage(
                        1,
                        1,
                        java.awt.image.BufferedImage.TYPE_INT_ARGB
                )
        );
    }

    /*
     * ============================================================
     * EVENTOS
     * ============================================================
     */

    private void configurarEventos() {

        btnCambiarClave.addActionListener(
                e -> {

                    CambiarClaveView vista =
                            new CambiarClaveView(this);

                    vista.setVisible(true);
                }
        );

        btnCerrarSesion.addActionListener(
                e -> {

                    int respuesta =
                            JOptionPane.showConfirmDialog(
                                    this,
                                    "¿Desea cerrar la sesión actual?",
                                    "Cerrar sesión",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE
                            );

                    if (respuesta !=
                            JOptionPane.YES_OPTION) {

                        return;
                    }

                    Sesion.cerrar();

                    dispose();

                    LoginView login =
                            new LoginView();

                    new una.sistemareservas.controlador.LoginController(
                            login
                    );

                    login.setVisible(true);
                }
        );
    }

    /*
     * ============================================================
     * UI DE LAS PESTAÑAS
     * ============================================================
     */

    private static class TabsModernosUI
            extends BasicTabbedPaneUI {

        @Override
        protected void installDefaults() {

            super.installDefaults();

            tabInsets =
                    new Insets(
                            12,
                            18,
                            12,
                            18
                    );

            selectedTabPadInsets =
                    new Insets(
                            0,
                            0,
                            0,
                            0
                    );

            contentBorderInsets =
                    new Insets(
                            0,
                            0,
                            0,
                            0
                    );

            tabAreaInsets =
                    new Insets(
                            0,
                            0,
                            0,
                            0
                    );
        }

        @Override
        protected void paintTabBackground(
                Graphics g,
                int tabPlacement,
                int tabIndex,
                int x,
                int y,
                int w,
                int h,
                boolean seleccionado) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setColor(
                    seleccionado
                            ? Color.WHITE
                            : new Color(
                            247,
                            250,
                            254
                    )
            );

            g2.fillRoundRect(
                    x + 2,
                    y + 3,
                    w - 4,
                    h - 5,
                    12,
                    12
            );

            g2.dispose();
        }

        @Override
        protected void paintTabBorder(
                Graphics g,
                int tabPlacement,
                int tabIndex,
                int x,
                int y,
                int w,
                int h,
                boolean seleccionado) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setColor(
                    seleccionado
                            ? AZUL
                            : BORDE
            );

            g2.drawRoundRect(
                    x + 2,
                    y + 3,
                    w - 4,
                    h - 5,
                    12,
                    12
            );

            g2.dispose();
        }

        @Override
        protected void paintContentBorder(
                Graphics g,
                int tabPlacement,
                int selectedIndex) {

            // El contenedor exterior dibuja el borde.
        }
    }
}