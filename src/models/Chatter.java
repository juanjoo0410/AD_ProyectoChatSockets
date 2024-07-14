package models;

/**
   Una clase que representa a un usuario del chat room.
*/
public class Chatter {
    private String name;

    /**
      Construye un Chatter con un nombre dado.
   */
    public Chatter(String name) {
        this.name = name;
    }

    /**
      Retorna el nombre.
      @return el nombre
   */
    public String getName() {
        return name;
    }
}
