
package una.sistemareservas.servidor;

public class MainServidor {
    public static void main(String[] args) {
        ServidorSocket servidor = new ServidorSocket(5000);
        try {
            servidor.iniciar();
        } catch (Exception e) {
            System.err.println("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }
}
