package ir.ugstudio.vampire.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.listeners.OnCompleteListener;
import ir.ugstudio.vampire.utils.FontHelper;

public class ConnectionLostDialog extends Dialog {

    private OnCompleteListener onCompleteListener;

    private TextView textViewMessage;
    private Button cancelButton;
    private Button buttonEnableWifi;
    private Button buttonEnableData;

    public ConnectionLostDialog(Context context, OnCompleteListener listener) {
        super(context);
        this.onCompleteListener = listener;
    }

    public ConnectionLostDialog(Context context) {
        super(context);
    }

    public OnCompleteListener getOnCompleteListener() {
        return onCompleteListener;
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_connection_lost);
        configureLayoutSize();

        textViewMessage = (TextView) findViewById(R.id.textView_message);
        cancelButton = (Button) findViewById(R.id.cancel_connection_lost_dialog);
        buttonEnableWifi = (Button) findViewById(R.id.button_enable_wifi);
        buttonEnableData = (Button) findViewById(R.id.button_enable_data);

        FontHelper.setKoodakFor(getContext(), textViewMessage, cancelButton, buttonEnableWifi, buttonEnableData);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCompleteListener.onComplete(0);
            }
        });
        buttonEnableWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCompleteListener.onComplete(1);
            }
        });
        buttonEnableData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCompleteListener.onComplete(2);
            }
        });
    }

    private void configureLayoutSize() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int w = Math.min(1000, (int) (0.9 * displayMetrics.widthPixels));
        getWindow().setLayout(w, -2/* means WRAP_CONTENT */);
    }
}
