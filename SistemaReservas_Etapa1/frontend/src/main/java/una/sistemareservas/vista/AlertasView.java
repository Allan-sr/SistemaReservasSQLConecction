
package una.sistemareservas.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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
                              String fecha, String hora) {

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

        btnVer.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Actividad: " + actividad +
                        "\nFuncionario: " + funcionario +
                        "\nFecha: " + fecha +
                        "\nHora: " + hora,
                "Detalle de reserva",
                JOptionPane.INFORMATION_MESSAGE
        ));

        btnDescartar.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea descartar esta alerta?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                listaAlertas.remove(tarjeta);
                cantidadAlertas--;
                lblCantidad.setText(String.valueOf(cantidadAlertas));
                listaAlertas.revalidate();
                listaAlertas.repaint();
            }
        });

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
}
