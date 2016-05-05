package at.svgsch.simplechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText et_username;
    public static final String EXTRA_USERNAME = "at.svgsch.simplechat.EXTRA_USERNAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_username = (EditText)findViewById(R.id.et_username);
    }

    public void onLoginPressed(View v) {
        String username = et_username.getText().toString();
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra(EXTRA_USERNAME,username);
        startActivity(i);
    }

    public void onViewLogPressed(View v) {
        Intent i = new Intent(this, LogActivity.class);
        startActivity(i);
    }
}
