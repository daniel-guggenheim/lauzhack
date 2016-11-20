package ch.google.thomas.breakingwall.data;

/**
 * Created by charles on 20.11.16.
 */

public class Message {
    private User to;
    private String content;

    public Message(String username, String msg) {
        to = new User("", username);
        this.content = msg;
    }

    String getReceiverUsername() {
        return to.getUserName();
    }

    String getContent() {
        return content;
    }

}
