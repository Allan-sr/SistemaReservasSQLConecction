package una.sistemareservas.controlador;

import una.sistemareservas.modelo.Usuario;
import una.sistemareservas.util.Sesion;
import una.sistemareservas.vista.LoginView;
import una.sistemareservas.vista.MenuPrincipalView;
import una.sistemareservas.contratos.IUsuarioService;
import una.sistemareservas.proxy.Servicios;

public class LoginController {

    private final LoginView vista;
    private final IUsuarioService servicio;

    public LoginController(LoginView vista) {

        this.vista = vista;
        this.servicio = Servicios.usuarios();

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

        Usuario usuario;
        try {
            usuario= servicio.autenticar(id, clave);
        } catch (RuntimeException e){
            vista.mostrarMensaje("No se pudo comunicar con el servidor: " + e.getMessage());
            return;
        }

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
                new MenuPrincipalView(Servicios.getCliente());

        menu.setVisible(true);

    }

    private void cerrarPrograma() {

        System.exit(0);
    }
}