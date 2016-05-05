package at.svgsch.simplechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class LogActivity extends AppCompatActivity {

    public static final String LOG_FILE = "log_file";

    private LinearLayout ll_logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ll_logView = (LinearLayout)findViewById(R.id.ll_logView);
        readLog();
    }

    private void readLog() {
        File file = new File(this.getFilesDir(), LOG_FILE);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    TextView logEntry = new TextView(this);
                    logEntry.setText(line);
                    ll_logView.addView(logEntry);
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
