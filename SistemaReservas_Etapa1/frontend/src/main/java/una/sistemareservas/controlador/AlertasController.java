package una.sistemareservas.controlador;

import una.sistemareservas.cliente.ClienteSocket;
import una.sistemareservas.negocio.NotificacionReserva;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;
import una.sistemareservas.vista.AlertasView;

import javax.swing.JOptionPane;

public class AlertasController {

    private final AlertasView vista;
    private final ClienteSocket cliente;

    private boolean activo;

    public AlertasController(
            AlertasView vista,
            ClienteSocket cliente) {

        this.vista = vista;
        this.cliente = cliente;
    }

    private void recibirAlerta(NotificacionReserva notificacion) {
        if (!activo) {
            return;
        }

        vista.agregarAlerta(
                notificacion.getActividad(),
                notificacion.getFuncionarioNombre(),
                notificacion.getFecha(),
                notificacion.getHora(),
                () -> verAlerta(notificacion),
                this::descartarAlerta
        );
    }

    private void verAlerta(NotificacionReserva notificacion) {
        if (!activo) {
            return;
        }

        vista.mostrarDetalle(
                notificacion.getActividad(),
                notificacion.getFuncionarioNombre(),
                notificacion.getFecha(),
                notificacion.getHoraInicio(),
                notificacion.getHoraFin(),
                notificacion.getRecursos()
        );
    }

    private void descartarAlerta(Runnable quitarDeLaVista) {
        if (activo && vista.confirmarDescarte()) {
            quitarDeLaVista.run();
        }
    }

    public void iniciar() {
        activo = true;
        cliente.setReceptorAlertas(this::recibirAlerta);

        try {
            Respuesta respuesta = cliente.enviarPeticion(
                    new Peticion("ALERTAS_SUSCRIBIR", null)
            );

            if (!respuesta.isExito()) {
                throw new IllegalStateException(
                        respuesta.getMensaje()
                );
            }

        } catch (Exception e) {
            activo = false;
            cliente.setReceptorAlertas(null);

            JOptionPane.showMessageDialog(
                    vista,
                    "No se pudieron activar las alertas: "
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void detener() {
        activo = false;
        cliente.setReceptorAlertas(null);

        try {
            cliente.enviarPeticion(
                    new Peticion("ALERTAS_DESUSCRIBIR", null)
            );

        } catch (Exception e) {
            System.err.println(
                    "No se pudo cerrar la suscripción: "
                            + e.getMessage()
            );
        }
    }
}