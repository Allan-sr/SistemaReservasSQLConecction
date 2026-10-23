package una.sistemareservas.app;

import una.sistemareservas.cliente.ClienteSocket;
import una.sistemareservas.controlador.LoginController;
import una.sistemareservas.proxy.Servicios;
import una.sistemareservas.vista.LoginView;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        // Por defecto el servidor está en esta misma máquina.
        // Para otra máquina: pasar la IP como primer argumento.
        String host = args.length > 0 ? args[0] : "localhost";
        int puerto = 5000;

        ClienteSocket cliente = new ClienteSocket(host, puerto);

        try {
            cliente.conectar();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo conectar con el servidor en " + host + ":" + puerto,
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Servicios.iniciar(cliente);

        SwingUtilities.invokeLater(() -> {
            LoginView login = new LoginView();
            new LoginController(login);
            login.setVisible(true);
        });
    }
}