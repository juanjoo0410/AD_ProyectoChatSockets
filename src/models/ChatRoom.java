package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import server.ChatServer;

/**
 * Un chat room el cual consiste de múltiples chatters.
 */
public class ChatRoom {

    private List activeService;
    private int capacity;
    private Map chatterHash;

    /**
     * Construye un chat room con una capacidad determinada.
     *
     * @param capacity la capacidad del cuarto
     */
    public ChatRoom(int capacity) {
        this.capacity = capacity;
        this.chatterHash = Collections.synchronizedMap(new HashMap(this.capacity));
        this.activeService = Collections.synchronizedList(new ArrayList(this.capacity));
    }

    /**
     * Registrar un chatter en el cuarto.
     *
     * @param aName el nombre a registrar
     */
    public void register(String aName) {
        chatterHash.put(aName, new Chatter(aName));
        updateUserList();
    }

    /**
     * De-registrar un chatter del cuarto.
     *
     * @param aName el nombre a de-registrar
     * @param service el nombre del servicio que se está de-registrando
     */
    public synchronized void leave(String aName, ChatService service) {
        chatterHash.remove(aName);
        activeService.remove(activeService.indexOf(service));
        updateUserList();
    }

    public void add(ChatService cs) {
        activeService.add(cs);
    }

    /**
     * Enviar un mensaje a todos en este chat room.
     *
     * @param requestor el nombre de quien envía el mensaje
     * @param msg el mensaje a enviar
     * @param chatService el lugar de donde se escribe el mensaje (el servicio
     * asociado al cliente que envió el mensaje)
     */
    public synchronized void broadcast(String requestor, String msg, ChatService chatService) {
        for (int i = 0; i < activeService.size(); i++) {
            ChatService cs = (ChatService) activeService.get(i);
            if (cs != chatService && cs.getUserName() != null) {
                cs.putMessage(requestor + ": " + msg);
            }
        }
    }

    public List<String> getChatterNames() {
        return new ArrayList<>(chatterHash.keySet());
    }
    
     private void updateUserList() {
         SwingUtilities.invokeLater(() -> ChatServer.updateUserList(getChatterNames()));
    }
}
