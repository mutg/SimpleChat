package at.svgsch.simplechat;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Mathias on 03.05.2016.
 */
public class RetainedFragment extends Fragment {

    private Client savedClient = null;
    private UserDatabase savedDatabase = null;
    private String savedUsername = null;
    private String savedId = null;

    public void saveData(Client client, UserDatabase userDb, String username, String id) {
        savedClient = client;
        savedDatabase = userDb;
        savedUsername = username;
        savedId = id;
    }


    public Client getSavedClient() {
        return savedClient;
    }

    public UserDatabase getSavedUserDatabase() {
        return savedDatabase;
    }

    public String getSavedUsername() {
        return savedUsername;
    }

    public String getSavedId() {
        return savedId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
