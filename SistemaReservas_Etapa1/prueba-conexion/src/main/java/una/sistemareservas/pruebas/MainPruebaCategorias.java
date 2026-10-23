
package una.sistemareservas.pruebas;

import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class MainPruebaCategorias {

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", 5000)) {

            ObjectOutputStream salida =
                    new ObjectOutputStream(socket.getOutputStream());

            salida.flush();

            ObjectInputStream entrada =
                    new ObjectInputStream(socket.getInputStream());

            Peticion peticion =
                    new Peticion(Operaciones.CATEGORIA_LISTAR, null);

            salida.writeObject(peticion);
            salida.flush();

            Respuesta respuesta =
                    (Respuesta) entrada.readObject();

            System.out.println("Exito: " + respuesta.isExito());
            System.out.println("Mensaje: " + respuesta.getMensaje());

            if (respuesta.isExito()) {

                Object datos = respuesta.getDatos();

                if (datos instanceof List<?> categorias) {

                    System.out.println("Cantidad: " + categorias.size());

                    for (Object objeto : categorias) {

                        if (objeto instanceof Categoria categoria) {

                            System.out.println(
                                    categoria.getId() + " - "
                                            + categoria.getDescripcion()
                            );
                        }
                    }

                } else {
                    System.out.println("Respuesta sin lista de categorias.");
                }

            } else {
                System.out.println(
                        "Error del servidor: " + respuesta.getMensaje()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

