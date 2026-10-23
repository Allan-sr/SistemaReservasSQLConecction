
package una.sistemareservas.servidor;
import una.sistemareservas.util.DatosIniciales;

import java.rmi.server.ExportException;

public class MainServidor {
    public static void main(String[] args) {
        DatosIniciales.inicializar();

        ServidorSocket servidor = new ServidorSocket(5000);
        try{
            servidor.iniciar();
        }catch(Exception e){
            System.err.println("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }
}
