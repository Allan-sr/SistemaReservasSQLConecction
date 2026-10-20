package una.sistemareservas.util;

import una.sistemareservas.modelo.Usuario;

public class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {
    }

    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static boolean hayUsuarioActivo() {
        return usuarioActual != null;
    }

    public static void cerrar() {
        usuarioActual = null;
    }
}