package una.sistemareservas.servidor;

import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSocket {

    private final int puerto;

    public ServidorSocket(int puerto) {
        this.puerto = puerto;
    }

    public void iniciar() throws IOException {

        try (ServerSocket servidor = new ServerSocket(puerto)) {

            System.out.println("Servidor iniciado en el puerto " + puerto);

            while (true) {

                Socket cliente = servidor.accept();

                Thread hilo = new Thread(() -> atenderCliente(cliente));
                hilo.start();
            }
        }
    }

    private void atenderCliente(Socket cliente) {

        try (
                Socket conexion = cliente;
                ObjectOutputStream salida =
                        new ObjectOutputStream(conexion.getOutputStream());
                ObjectInputStream entrada =
                        new ObjectInputStream(conexion.getInputStream())
        ) {

            salida.flush();

            System.out.println(
                    "Cliente conectado: " + conexion.getInetAddress()
            );

            while (!conexion.isClosed()) {

                Object objeto = entrada.readObject();

                if (!(objeto instanceof Peticion)) {
                    salida.writeObject(
                            new Respuesta(false, null, "Petición inválida")
                    );
                    salida.flush();
                    continue;
                }

                Peticion peticion = (Peticion) objeto;

                System.out.println(
                        "Operación recibida: " + peticion.getOperacion()
                );

                Respuesta respuesta = procesarPeticion(peticion);

                salida.writeObject(respuesta);
                salida.flush();
                salida.reset();
            }

        } catch (EOFException e) {
            System.out.println("Cliente desconectado.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error con cliente: " + e.getMessage());
        }
    }

    private Respuesta procesarPeticion(Peticion peticion) {

        if ("PING".equals(peticion.getOperacion())) {

            return new Respuesta(
                    true,
                    "PONG",
                    "Comunicación exitosa"
            );
        }

        return new Respuesta(
                false,
                null,
                "Operación no reconocida: " + peticion.getOperacion()
        );
    }
}


