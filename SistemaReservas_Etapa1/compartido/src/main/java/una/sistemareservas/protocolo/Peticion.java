
package una.sistemareservas.protocolo;

import java.io.Serializable;

public class Peticion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String operacion;
    private Object datos;

    public Peticion(String operacion, Object datos) {
        this.operacion = operacion;
        this.datos = datos;
    }

    public String getOperacion() {
        return operacion;
    }

    public Object getDatos() {
        return datos;
    }
}
