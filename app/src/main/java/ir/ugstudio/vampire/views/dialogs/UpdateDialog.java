package ir.ugstudio.vampire.views.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.listeners.OnCompleteListener;
import ir.ugstudio.vampire.views.custom.CustomButton;

public class UpdateDialog extends AlertDialog implements View.OnClickListener {
    private final static String URL = "http://cafebazaar.ir/app/%s/?l=fa";
    private CustomButton positiveButton;
    private CustomButton negativeButton;
    private boolean forceUpdate;
    private OnCompleteListener onCompleteListener;

    public UpdateDialog(Context context, boolean forceUpdate, OnCompleteListener onCompleteListener) {
        super(context);
        this.setCancelable(false);
        this.forceUpdate = forceUpdate;
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update);

        positiveButton = (CustomButton) findViewById(R.id.button_positive);
        negativeButton = (CustomButton) findViewById(R.id.button_negative);

        if (forceUpdate) {
            negativeButton.setText("خروج");
        }

        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_positive:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(String.format(URL, getContext().getPackageName())));
                getContext().startActivity(browserIntent);
                break;

            case R.id.button_negative:
                onCompleteListener.onComplete(0);
                break;
        }
    }

    private void exitApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

}
