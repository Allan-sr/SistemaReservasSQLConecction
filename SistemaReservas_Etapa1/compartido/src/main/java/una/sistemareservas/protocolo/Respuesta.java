
package una.sistemareservas.protocolo;

import java.io.Serializable;

public class Respuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean exito;
    private Object datos;
    private String mensaje;

    public Respuesta(boolean exito, Object datos, String mensaje) {
        this.exito = exito;
        this.datos = datos;
        this.mensaje = mensaje;
    }

    public boolean isExito() {
        return exito;
    }

    public Object getDatos() {
        return datos;
    }

    public String getMensaje() {
        return mensaje;
    }
}
