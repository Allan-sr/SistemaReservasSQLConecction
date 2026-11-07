
package una.sistemareservas.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class AlertasView extends JPanel {

    private static final Color AZUL = new Color(28, 111, 232);
    private static final Color FONDO = new Color(244, 248, 253);
    private static final Color BORDE = new Color(220, 229, 241);

    private final JPanel listaAlertas;
    private int cantidadAlertas = 0;
    private final JLabel lblCantidad;

    public AlertasView() {
        setLayout(new BorderLayout(0, 12));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(285, 0));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(15, 12, 15, 12)
        ));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);

        JLabel titulo = new JLabel("Alertas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(AZUL);

        lblCantidad = new JLabel("0");
        lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCantidad.setForeground(AZUL);

        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(lblCantidad, BorderLayout.EAST);

        add(encabezado, BorderLayout.NORTH);

        listaAlertas = new JPanel();
        listaAlertas.setLayout(new BoxLayout(
                listaAlertas, BoxLayout.Y_AXIS
        ));
        listaAlertas.setBackground(FONDO);

        JScrollPane scroll = new JScrollPane(listaAlertas);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    public void agregarAlerta(String actividad, String funcionario,
            String fecha, String hora, Runnable alVer, Consumer<Runnable> alDescartar) {

        JPanel tarjeta = new JPanel(new BorderLayout(5, 8));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel nombre = new JLabel(actividad);
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel detalle = new JLabel(funcionario);
        detalle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel informacion = new JPanel();
        informacion.setOpaque(false);
        informacion.setLayout(new BoxLayout(
                informacion, BoxLayout.Y_AXIS
        ));

        informacion.add(nombre);
        informacion.add(detalle);

        JPanel botones = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 5, 0
        ));
        botones.setOpaque(false);

        JButton btnVer = new JButton("Ver");
        JButton btnDescartar = new JButton("X");

        btnVer.addActionListener(e -> alVer.run());

        btnDescartar.addActionListener(e ->
                alDescartar.accept(() -> quitarAlerta(tarjeta))
        );

        botones.add(btnVer);
        botones.add(btnDescartar);

        tarjeta.add(informacion, BorderLayout.CENTER);
        tarjeta.add(botones, BorderLayout.SOUTH);

        listaAlertas.add(tarjeta);
        listaAlertas.add(Box.createVerticalStrut(8));

        cantidadAlertas++;
        lblCantidad.setText(String.valueOf(cantidadAlertas));

        listaAlertas.revalidate();
        listaAlertas.repaint();
    }

    public void mostrarDetalle(String actividad, String encargado,
                               String fecha, String desde, String hasta,
                               java.util.List<String> recursos) {

        JPanel datos = new JPanel(new GridLayout(0, 2, 8, 6));
        datos.add(new JLabel("Encargado:"));
        datos.add(new JLabel(encargado));
        datos.add(new JLabel("Fecha:"));
        datos.add(new JLabel(fecha));
        datos.add(new JLabel("Desde:"));
        datos.add(new JLabel(desde));
        datos.add(new JLabel("Hasta:"));
        datos.add(new JLabel(hasta));

        JTextArea listaRecursos = new JTextArea(
                recursos == null || recursos.isEmpty()
                        ? "(sin recursos)"
                        : String.join("\n", recursos));
        listaRecursos.setEditable(false);
        listaRecursos.setRows(4);
        listaRecursos.setColumns(28);

        JPanel bloqueRecursos = new JPanel(new BorderLayout(0, 4));
        bloqueRecursos.add(new JLabel("Recursos:"), BorderLayout.NORTH);
        bloqueRecursos.add(new JScrollPane(listaRecursos), BorderLayout.CENTER);

        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.add(datos, BorderLayout.NORTH);
        contenido.add(bloqueRecursos, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(
                this,
                contenido,
                actividad,
                JOptionPane.PLAIN_MESSAGE
        );
    }

    public boolean confirmarDescarte() {
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea descartar esta alerta?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        return respuesta == JOptionPane.YES_OPTION;
    }

    private void quitarAlerta(JPanel tarjeta) {
        listaAlertas.remove(tarjeta);

        cantidadAlertas--;
        lblCantidad.setText(String.valueOf(cantidadAlertas));

        listaAlertas.revalidate();
        listaAlertas.repaint();
    }
}
