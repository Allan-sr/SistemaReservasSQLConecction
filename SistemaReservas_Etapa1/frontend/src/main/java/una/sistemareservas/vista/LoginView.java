package una.sistemareservas.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginView extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtClave;

    private JButton btnIngresar;
    private JButton btnCerrar;

    private static final Color AZUL =
            new Color(28, 111, 232);

    private static final Color AZUL_OSCURO =
            new Color(22, 57, 112);

    private static final Color FONDO =
            new Color(244, 248, 253);

    private static final Color BORDE =
            new Color(218, 227, 240);

    private static final Color TEXTO =
            new Color(25, 43, 75);

    public LoginView() {

        setTitle("Sistema de Reservas");

        setSize(900, 560);

        setMinimumSize(
                new Dimension(800, 500)
        );

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        inicializarComponentes();
    }

    private void inicializarComponentes() {

        JPanel fondo =
                new JPanel(
                        new GridLayout(1, 2)
                );

        fondo.setBackground(FONDO);

        /*
         * ==========================================================
         * PANEL IZQUIERDO
         * ==========================================================
         */

        JPanel panelMarca =
                new JPanel(
                        new BorderLayout()
                );

        panelMarca.setBackground(AZUL);

        panelMarca.setBorder(
                new EmptyBorder(
                        45,
                        45,
                        45,
                        45
                )
        );

        JPanel contenidoMarca =
                new JPanel();

        contenidoMarca.setOpaque(false);

        contenidoMarca.setLayout(
                new BoxLayout(
                        contenidoMarca,
                        BoxLayout.Y_AXIS
                )
        );

        /*
         * ICONO
         */

        JLabel icono =
                new JLabel(
                        "▣",
                        SwingConstants.CENTER
                );

        icono.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        icono.setPreferredSize(
                new Dimension(
                        95,
                        95
                )
        );

        icono.setMaximumSize(
                new Dimension(
                        95,
                        95
                )
        );

        icono.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        50
                )
        );

        icono.setForeground(
                AZUL
        );

        icono.setOpaque(true);

        icono.setBackground(
                Color.WHITE
        );

        /*
         * TITULO
         */

        JLabel titulo =
                new JLabel(
                        "<html><div style='text-align:center;'>"
                                + "Sistema de<br>Reservas"
                                + "</div></html>",
                        SwingConstants.CENTER
                );

        titulo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        30
                )
        );

        titulo.setForeground(
                Color.WHITE
        );

        /*
         * SUBTITULO
         */

        JLabel subtitulo =
                new JLabel(
                        "Organiza · Reserva · Disfruta",
                        SwingConstants.CENTER
                );

        subtitulo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        15
                )
        );

        subtitulo.setForeground(
                new Color(
                        220,
                        235,
                        255
                )
        );

        /*
         * TEXTO INFORMATIVO
         */

        JLabel descripcion =
                new JLabel(
                        "<html><div style='text-align:center;'>"
                                + "Gestiona tus reservas de recursos<br>"
                                + "de forma rápida y sencilla."
                                + "</div></html>",
                        SwingConstants.CENTER
                );

        descripcion.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        descripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        descripcion.setForeground(
                new Color(
                        225,
                        237,
                        252
                )
        );

        contenidoMarca.add(
                Box.createVerticalGlue()
        );

        contenidoMarca.add(icono);

        contenidoMarca.add(
                Box.createVerticalStrut(28)
        );

        contenidoMarca.add(titulo);

        contenidoMarca.add(
                Box.createVerticalStrut(8)
        );

        contenidoMarca.add(subtitulo);

        contenidoMarca.add(
                Box.createVerticalStrut(28)
        );

        contenidoMarca.add(descripcion);

        contenidoMarca.add(
                Box.createVerticalGlue()
        );

        panelMarca.add(
                contenidoMarca,
                BorderLayout.CENTER
        );

        /*
         * ==========================================================
         * PANEL DERECHO
         * ==========================================================
         */

        JPanel panelLogin =
                new JPanel(
                        new GridBagLayout()
                );

        panelLogin.setBackground(
                Color.WHITE
        );

        panelLogin.setBorder(
                new EmptyBorder(
                        40,
                        55,
                        40,
                        55
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.insets =
                new Insets(
                        7,
                        0,
                        7,
                        0
                );

        gbc.weightx = 1;

        /*
         * ENCABEZADO
         */

        JLabel lblBienvenida =
                new JLabel(
                        "Bienvenido"
                );

        lblBienvenida.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        lblBienvenida.setForeground(
                AZUL_OSCURO
        );

        gbc.gridx = 0;
        gbc.gridy = 0;

        panelLogin.add(
                lblBienvenida,
                gbc
        );

        JLabel lblInstruccion =
                new JLabel(
                        "Ingresa tus credenciales para continuar"
                );

        lblInstruccion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        lblInstruccion.setForeground(
                new Color(
                        100,
                        120,
                        150
                )
        );

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        25,
                        0
                );

        panelLogin.add(
                lblInstruccion,
                gbc
        );

        /*
         * USUARIO
         */

        gbc.insets =
                new Insets(
                        6,
                        0,
                        5,
                        0
                );

        JLabel lblUsuario =
                crearEtiqueta(
                        "Usuario"
                );

        gbc.gridy++;

        panelLogin.add(
                lblUsuario,
                gbc
        );

        txtUsuario =
                new JTextField();

        configurarCampo(
                txtUsuario
        );

        gbc.gridy++;

        panelLogin.add(
                txtUsuario,
                gbc
        );

        /*
         * CONTRASEÑA
         */

        JLabel lblClave =
                crearEtiqueta(
                        "Contraseña"
                );

        gbc.gridy++;

        panelLogin.add(
                lblClave,
                gbc
        );

        txtClave =
                new JPasswordField();

        configurarCampo(
                txtClave
        );

        gbc.gridy++;

        panelLogin.add(
                txtClave,
                gbc
        );

        /*
         * BOTÓN INGRESAR
         */

        btnIngresar =
                crearBotonPrincipal(
                        "Iniciar sesión"
                );

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        22,
                        0,
                        8,
                        0
                );

        panelLogin.add(
                btnIngresar,
                gbc
        );

        /*
         * BOTÓN CERRAR
         */

        btnCerrar =
                crearBotonSecundario(
                        "Cerrar"
                );

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        5,
                        0,
                        5,
                        0
                );

        panelLogin.add(
                btnCerrar,
                gbc
        );

        /*
         * TEXTO INFERIOR
         */

        JLabel pie =
                new JLabel(
                        "Sistema de Reservas v2.0",
                        SwingConstants.CENTER
                );

        pie.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        pie.setForeground(
                new Color(
                        130,
                        145,
                        165
                )
        );

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        20,
                        0,
                        0,
                        0
                );

        panelLogin.add(
                pie,
                gbc
        );

        fondo.add(panelMarca);

        fondo.add(panelLogin);

        add(fondo);

        /*
         * Enter = iniciar sesión
         */

        getRootPane()
                .setDefaultButton(
                        btnIngresar
                );
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

    private void configurarCampo(
            JTextField campo) {

        campo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        15
                )
        );

        campo.setPreferredSize(
                new Dimension(
                        300,
                        45
                )
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                12,
                                8,
                                12
                        )
                )
        );

        campo.setBackground(
                Color.WHITE
        );

        campo.setForeground(
                TEXTO
        );
    }

    private JButton crearBotonPrincipal(
            String texto) {

        JButton boton =
                new JButton(texto) {

                    @Override
                    protected void paintComponent(
                            Graphics g) {

                        Graphics2D g2 =
                                (Graphics2D)
                                        g.create();

                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );

                        g2.setColor(
                                getModel().isPressed()
                                        ? new Color(
                                        18,
                                        88,
                                        190
                                )
                                        : AZUL
                        );

                        g2.fill(
                                new RoundRectangle2D.Double(
                                        0,
                                        0,
                                        getWidth(),
                                        getHeight(),
                                        12,
                                        12
                                )
                        );

                        g2.dispose();

                        super.paintComponent(g);
                    }
                };

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        boton.setForeground(
                Color.WHITE
        );

        boton.setBackground(
                AZUL
        );

        boton.setPreferredSize(
                new Dimension(
                        300,
                        45
                )
        );

        boton.setFocusPainted(false);

        boton.setBorder(
                BorderFactory.createEmptyBorder()
        );

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setOpaque(false);

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
                        13
                )
        );

        boton.setForeground(
                AZUL_OSCURO
        );

        boton.setBackground(
                new Color(
                        242,
                        247,
                        253
                )
        );

        boton.setPreferredSize(
                new Dimension(
                        300,
                        42
                )
        );

        boton.setFocusPainted(false);

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                12,
                                8,
                                12
                        )
                )
        );

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return boton;
    }

    public String getUsuario() {

        return txtUsuario
                .getText()
                .trim();
    }

    public String getClave() {

        return new String(
                txtClave.getPassword()
        );
    }

    public JButton getBtnIngresar() {

        return btnIngresar;
    }

    public JButton getBtnCerrar() {

        return btnCerrar;
    }

    public void mostrarMensaje(
            String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Sistema de Reservas",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void limpiarCampos() {

        txtUsuario.setText("");

        txtClave.setText("");

        txtUsuario.requestFocus();
    }
}