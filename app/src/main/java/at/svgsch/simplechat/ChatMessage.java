package at.svgsch.simplechat;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Mathias on 30.04.2016.
 */
public class ChatMessage {

    private LinearLayout layout;

    private View bg;
    private TextView tv_name;
    private GradientDrawable shape;
    private TextView tv_text;
    private TextView tv_time;

    public ChatMessage(Context context, ViewGroup root, String sender, String text, int color, String info, boolean isRight) {
        int layoutRes = isRight ? R.layout.chat_message_right : R.layout.chat_message_left;
        layout = (LinearLayout)LayoutInflater.from(context).inflate(layoutRes, root, false);

        bg = layout.findViewById(R.id.bg);
        shape = (GradientDrawable)bg.getBackground();
        tv_name = (TextView)layout.findViewById(R.id.tv_name);
        tv_text = (TextView)layout.findViewById(R.id.tv_text);
        tv_time = (TextView)layout.findViewById(R.id.tv_time);

        tv_name.setText(sender);
        tv_text.setText(text);
        tv_time.setText(info);

        setColor(color);
        root.addView(layout);
    }

    public void setColor(int newColor) {
        shape.setColor(newColor);
    }

}
