package at.svgsch.simplechat;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {

    private final String REMOVE_USER = "remove_user";
    private final String ADD_USER = "add_user";
    private final String CHAT_MESSAGE = "chat_message";

    private ArrayList<ChatMessage> chatMessages;
    private UserDatabase userDb;
    private Client client;
    private RetainedFragment retainedFragment;


    private String username;
    private String myId;

    private LinearLayout ll_chat;
    private EditText et_message;
    private ScrollView sv_chat;
    private Toolbar toolbar;
    private Button btn_send;

    private int myColor = Color.parseColor("#DBFFBA");
    private int otherColor = Color.parseColor("#cccccc");


    private static class ClientMessageHandler extends Handler {

        private WeakReference<ChatActivity> mActivity;

        public ClientMessageHandler(ChatActivity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Client.CLIENT_MESSAGE:
                    try {
                        mActivity.get().handleClientMessage((String) msg.obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Client.CLIENT_CONNECTED:
                    try {
                        mActivity.get().handleConnected((String)msg.obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Client.CLIENT_DISCONNECTED:
                    break;
                case Client.CLIENT_RETRY:
                    mActivity.get().displayInfoMessage("Kunne ikke koble til! Prøver igjen...");
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        username = getIntent().getStringExtra(LoginActivity.EXTRA_USERNAME);
        chatMessages = new ArrayList<>();
        initViews();
        init();
    }

    private void init() {
        FragmentManager fm = getFragmentManager();
        retainedFragment = (RetainedFragment)fm.findFragmentByTag("data");

        if (retainedFragment == null) {
            retainedFragment = new RetainedFragment();
            fm.beginTransaction().add(retainedFragment, "data").commit();
        }

        if (client == null)
            connect();
    }

    private void initViews() {


        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name) + " - " + username);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setEnabled(false);
        et_message = (EditText)findViewById(R.id.et_sent);
        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn_send.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sv_chat = (ScrollView)findViewById(R.id.sv_chat);
        ll_chat = (LinearLayout)findViewById(R.id.ll_chat);
        setSupportActionBar(toolbar);
    }

    private void connect() {

        client = setupClient();
        client.connect();

    }

    private Client setupClient() {
        Client c = new Client(new ClientMessageHandler(this));

        c.setSendClientDataHandler(new Client.SendClientDataHandler() {
            @Override
            public String generateClientData() {
                JSONObject cData = new JSONObject();

                try {
                    cData.put("username", username);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return cData.toString();
            }
        });

        return c;
    }



    private void handleConnected(String data) throws Exception {
        JSONObject obj = new JSONObject(data);
        myId = obj.getString("id");
        userDb = new UserDatabase(generateUserList(obj.getJSONArray("online_users")));
    }

    private void handleClientMessage(String msg) throws Exception {
        Log.i("Message", msg);
        JSONObject msgObj = new JSONObject(msg);
        String what = msgObj.getString("what");
        switch (what) {
            case CHAT_MESSAGE:
                User u = userDb.getUserById(msgObj.getString("source"));
                displayChatMessage(u.getUsername(), msgObj.getString("message"), otherColor, false);
                break;
            case ADD_USER:
                addUser(msgObj.getString("id"), msgObj.getString("username"));
                break;
            case REMOVE_USER:
                removeUser(msgObj.getString("id"));
                break;
            default:
                break;
        }

    }

    private void displayInfoMessage(String message) {
        TextView infoTv = new TextView(this);
        infoTv.setText(message);
        infoTv.setTextSize(17);
        infoTv.setTypeface(null, Typeface.ITALIC);
        infoTv.setTextColor(Color.LTGRAY);
        ll_chat.addView(infoTv);
        infoTv.setGravity(Gravity.CENTER);
        scrollToBottom();
    }

    private void displayChatMessage(String username, String message, int color, boolean isRight) {

        SimpleDateFormat date = new SimpleDateFormat("HH:mm");
        String info = date.format(Calendar.getInstance().getTime());
        chatMessages.add(new ChatMessage(this, ll_chat, username, message, color, info, isRight));
        writeToLog("["+info+"] " + username + ": " + message);
        scrollToBottom();
    }

    private void removeUser(String id) {
        User u = userDb.getUserById(id);
        displayInfoMessage(u.getUsername() + " koblet fra");
        userDb.removeUser(u);
    }

    private void addUser(String id, String name) {
        User u = new User(id, name);
        displayInfoMessage(u.getUsername() + " koblet til");
        userDb.addUser(u);
    }

    private ArrayList<User> generateUserList(JSONArray jsonArray) {

        ArrayList<User> list = new ArrayList<>();
        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userObject = jsonArray.getJSONObject(i);
                User u = new User(userObject.getString("id"),userObject.getString("username"));
                list.add(u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void scrollToBottom() {
        sv_chat.post(new Runnable() {
            @Override
            public void run() {
                sv_chat.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void onSentButtonPressed(View view) {
        String message = et_message.getText().toString();
        et_message.setText("");
        displayChatMessage(username,message, myColor, true);
        scrollToBottom();
        JSONObject msg = new JSONObject();
        try {
            msg.put("what",CHAT_MESSAGE);
            msg.put("message",message);
        }   catch (Exception e) {

        }

        client.send(msg.toString());
    }


    private void writeToLog(String text) {
        File file = new File(this.getFilesDir(), LogActivity.LOG_FILE);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(text);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i("ChatActivity","onDestroy");
        retainedFragment.saveData(client, userDb, username, myId);
        client.close();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.i("ChatActivity","onStop");
        super.onStop();
    }
}
