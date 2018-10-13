package app.hablemos.model;

public class User {

    public String userID;
    public String username;
    public String email;
    public String equipo;
    public String remediosManiana;
    public String remediosTarde;
    public String remediosNoche;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userID,String username, String email, String equipo, String remediosManiana, String remediosTarde, String remediosNoche) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.equipo = equipo;
        this.remediosManiana = remediosManiana;
        this.remediosTarde = remediosTarde;
        this.remediosNoche = remediosNoche;
    }

    public User(String userID,String username, String email) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.equipo = "";
        this.remediosManiana = "";
        this.remediosTarde = "";
        this.remediosNoche = "";
    }
}