package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import models.ChatRoom;
import models.ChatService;

/**
 * Un servidor que ejecuta el ChatService. Puede aceptar múltiples conexiones de
 * múltiples clientes.
 */
public class ChatServer {
        private static final int ROOM_SIZE = 10;
        private static final int PORT = 8889;
        private static ChatRoom chatRoom;

    public static void main(String[] args) throws IOException {
        chatRoom = new ChatRoom(ROOM_SIZE);
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Esperando que se conecten clientes...");

            while (true) {
                Socket client = server.accept();
                System.out.println("Cliente conectado desde " + client.getInetAddress());

                // Crea una nueva instancia de ChatService
                ChatService servicio = new ChatService(client, chatRoom);
                servicio.setAddress(client.getInetAddress().toString());

                // Crea un nuevo hilo para el servicio y comienza la ejecución
                Thread hilo = new Thread(servicio);
                hilo.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
