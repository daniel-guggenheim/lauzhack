package ch.google.thomas.breakingwall.data;

/**
 * Created by charles on 20.11.16.
 */

public class LogedUser extends User {
    private String ourToken;
    private static LogedUser user;


    public static LogedUser getInstance(){
        if(user == null) {
            return new LogedUser();
        } else {
            return user;
        }
    }

    public LogedUser() {
        //check if


    }

    public boolean isLogged() {
        return ourToken != null;
    }

    public void login(String token) {

        ourToken = token;
    }
}
