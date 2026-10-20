package una.sistemareservas.vista;

import una.sistemareservas.controlador.CambiarClaveController;

import javax.swing.*;
import java.awt.*;

public class CambiarClaveView extends JDialog {

    private JPasswordField txtClaveActual;
    private JPasswordField txtNuevaClave;
    private JPasswordField txtConfirmarClave;

    private JButton btnCambiar;
    private JButton btnCancelar;

    public CambiarClaveView(JFrame padre) {

        super(
                padre,
                "Cambiar contraseña",
                true
        );

        setSize(400, 300);

        setLocationRelativeTo(padre);

        inicializarComponentes();

        new CambiarClaveController(this);
    }

    private void inicializarComponentes() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                4,
                                2,
                                10,
                                10
                        )
                );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        panel.add(
                new JLabel("Contraseña actual:")
        );

        txtClaveActual =
                new JPasswordField();

        panel.add(txtClaveActual);

        panel.add(
                new JLabel("Nueva contraseña:")
        );

        txtNuevaClave =
                new JPasswordField();

        panel.add(txtNuevaClave);

        panel.add(
                new JLabel("Confirmar contraseña:")
        );

        txtConfirmarClave =
                new JPasswordField();

        panel.add(txtConfirmarClave);

        btnCambiar =
                new JButton("Cambiar");

        btnCancelar =
                new JButton("Cancelar");

        panel.add(btnCambiar);
        panel.add(btnCancelar);

        add(panel);
    }

    public String getClaveActual() {

        return new String(
                txtClaveActual.getPassword()
        );
    }

    public String getNuevaClave() {

        return new String(
                txtNuevaClave.getPassword()
        );
    }

    public String getConfirmarClave() {

        return new String(
                txtConfirmarClave.getPassword()
        );
    }

    public JButton getBtnCambiar() {
        return btnCambiar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

    public void mostrarMensaje(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Cambiar contraseña",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}