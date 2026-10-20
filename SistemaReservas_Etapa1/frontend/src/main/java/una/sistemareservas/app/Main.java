package una.sistemareservas.app;

import una.sistemareservas.controlador.LoginController;
import una.sistemareservas.util.DatosIniciales;
import una.sistemareservas.vista.LoginView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        DatosIniciales.inicializar();
        SwingUtilities.invokeLater(() -> {
            LoginView login = new LoginView();
            new LoginController(login);
            login.setVisible(true);
        });
    }
}
