package ch.google.thomas.breakingwall.data;

/**
 * Created by Thomas on 19.11.2016.
 */
public class User {
    private String ID;
    private String userName;

    public User() {

    }
    public User(String ID, String userName){
        this.ID = ID;
        this.userName = userName;
    }

    public String getID() {
        return ID;
    }

    public String getUserName() {
        return userName;
    }

}
