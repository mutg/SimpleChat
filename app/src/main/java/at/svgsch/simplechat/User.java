package at.svgsch.simplechat;

/**
 * Created by Mathias on 30.04.2016.
 */
public class User {

    public String id;
    private String username;

    public String getId() {return id;}

    public String getUsername() {return username;}

    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }

}
