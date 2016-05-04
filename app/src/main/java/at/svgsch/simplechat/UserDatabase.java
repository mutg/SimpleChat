package at.svgsch.simplechat;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Mathias on 30.04.2016.
 */
public class UserDatabase {

    private ArrayList<User> userList;

    public UserDatabase(ArrayList<User> existingUsers) {
        Log.i("New userdb","Created new userdb with " + existingUsers.size() + " users");
        userList = existingUsers;
        printUsers();
    }

    public void addUser(User u) {
        Log.i("Added user","id: " + u.getId() + ", name: " + u.getUsername());
        userList.add(u);
    }

    public void removeUser(User u) {
        Log.i("Removed user","id: " + u.getId() + ", name: " + u.getUsername());
        userList.remove(u);
    }


    private void printUsers() {
        for (User user : userList) {
            Log.i("userlist",user.getUsername() + ":" + user.getId());
        }
    }

    public User getUserById(String id) {
        Log.i("Finding user","id: " + id);
        for (User x : userList) {
            if (x.getId().equals(id)) {
                Log.i("found","it");
                return x;
            }
        }
        return null;
    }

    public User get(int i) {
        return userList.get(i);
    }

    public int size() {
        return userList.size();
    }

}
