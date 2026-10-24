
package una.sistemareservas.servidor;

import una.sistemareservas.util.DatosIniciales;

public class MainServidor {

    public static void main(String[] args) {

        try {
            System.out.println("Inicializando datos del sistema...");

            DatosIniciales.inicializar();

            System.out.println("Iniciando servidor en puerto 5000...");

            ServidorSocket servidor = new ServidorSocket(5000);

            servidor.iniciar();

        } catch (Exception e) {
            System.err.println(
                    "No se pudo iniciar el servidor: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }
}

