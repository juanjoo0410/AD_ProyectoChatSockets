package models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Ejecuta los comandos de un protocolo de chat room simple recibidos de un
 * socket.
 */
public class ChatService implements Runnable {

    private String userName;
    private Socket socket;
    private ChatRoom chatRoom;
    private PrintWriter out;
    private BufferedReader in;
    private boolean loggedIn;
    private String address;

    /**
     * Construye un objeto del servicio que procesa comandos de un socket para
     * un chat room.
     *
     * @param socket el socket
     * @param chatRoom el chat room
     */
    public ChatService(Socket socket, ChatRoom chatRoom) {
        this.socket = socket;
        this.chatRoom = chatRoom;
        this.chatRoom.add(this);
        this.loggedIn = false;
    }

    @Override
    public void run() {
        principal();
    }

    /**
     * Ejecuta los comandos hasta recibir un LOGOUT o el fin de los datos de
     * entrada.
     */
    private void principal() {
        try {
            InputStream inStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(inStream));
            out = new PrintWriter(outStream);

            while (true) {
                if (!in.ready()) {
                    continue;
                }
                String line = in.readLine();
                int commandDelimiterPos = line.indexOf(' ');
                if (commandDelimiterPos < 0) {
                    commandDelimiterPos = line.length();
                }
                String command = line.substring(0, commandDelimiterPos);
                line = line.substring(commandDelimiterPos);

                String response = executeCommand(command, line);
                putMessage(response);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Envía el mensaje a través del socket.
     *
     * @param msg el mensaje a ser enviado
     */
    public void putMessage(String msg) {
        if (out != null) {
            out.println(msg);
            out.flush();
        }
    }

    /**
     * Ejecuta un comando.
     *
     * @param command el comando
     * @param line el resto de la linea del comando
     * @return la respuesta a ser enviada al cliente
     */
    private String executeCommand(String command, String line) throws IOException {
        if (command.equals("LOGIN")) {
            userName = line;
            chatRoom.register(userName);
            chatRoom.broadcast(userName, "LOGIN", this);
            loggedIn = true;
            return "Administrador del chat room: Hola, " + userName + ".";
        } else if (!loggedIn) {
            return "Administrador del chat room: Usted debe hacer LOGIN primero";
        } else if (command.equals("CHAT")) {
            String message = line;
            chatRoom.broadcast(userName, message, this);
            return userName + ": " + message;
        } else if (!command.equals("LOGOUT")) {
            return "Administrador del chat room: Comando inválido";
        }

        chatRoom.broadcast(userName, "LOGOUT", this);
        chatRoom.leave(userName, this);
        return "Adios " + userName + "!";
    }

    /**
     * Retorna el nombre del usuario de este servicio.
     *
     * @return el nombre del usuario de este servicio
     */
    public String getUserName() {
        return userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    
}
