package una.sistemareservas.controlador;

import una.sistemareservas.modelo.Usuario;
import una.sistemareservas.negocio.UsuarioService;
import una.sistemareservas.util.Sesion;
import una.sistemareservas.vista.LoginView;
import una.sistemareservas.vista.MenuPrincipalView;

public class LoginController {

    private final LoginView vista;
    private final UsuarioService servicio;

    public LoginController(LoginView vista) {

        this.vista = vista;
        this.servicio = new UsuarioService();

        iniciarEventos();
    }

    private void iniciarEventos() {

        vista.getBtnIngresar()
                .addActionListener(e -> iniciarSesion());

        vista.getBtnCerrar()
                .addActionListener(e -> cerrarPrograma());
    }

    private void iniciarSesion() {

        String id = vista.getUsuario();
        String clave = vista.getClave();

        if (id.isEmpty()) {

            vista.mostrarMensaje(
                    "Debe ingresar el usuario."
            );

            return;
        }

        if (clave.isEmpty()) {

            vista.mostrarMensaje(
                    "Debe ingresar la contraseña."
            );

            return;
        }

        Usuario usuario =
                servicio.autenticar(id, clave);

        if (usuario == null) {

            vista.mostrarMensaje(
                    "Usuario o contraseña incorrectos."
            );

            vista.limpiarCampos();

            return;
        }

        // Guardamos el usuario en la sesión
        Sesion.iniciar(usuario);

        vista.dispose();

        MenuPrincipalView menu =
                new MenuPrincipalView();

        menu.setVisible(true);
    }

    private void cerrarPrograma() {

        System.exit(0);
    }
}