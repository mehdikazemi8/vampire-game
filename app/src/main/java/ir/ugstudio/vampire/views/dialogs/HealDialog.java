package ir.ugstudio.vampire.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;

import ir.ugstudio.vampire.R;

public class HealDialog extends Dialog {
    private TextView textView;
    private Button button;
    private Button goToHospital;
    private Button buyHealer;

    Queue<String> queue = new LinkedList<>();

    public HealDialog(Context context) {
        super(context);

        queue.add(context.getResources().getString(R.string.heal_you_are_dead));
        queue.add(context.getResources().getString(R.string.heal_you_have_two_options));
        queue.add(context.getResources().getString(R.string.heal_you_can_go_to_hospital));
        queue.add(context.getResources().getString(R.string.heal_you_can_buy_healer));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.heal_alert);
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();

        getWindow().setLayout((int) (metrics.widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);

        button = (Button) findViewById(R.id.button_confirm);
        goToHospital = (Button) findViewById(R.id.button_go_to_hospital);
        buyHealer = (Button) findViewById(R.id.button_buy_healer);
        textView = (TextView) findViewById(R.id.textView_caption);

        textView.setText(queue.remove());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(queue.isEmpty()) {
                    dismiss();
                } else {
                    textView.setText(queue.remove());
                    if(queue.isEmpty()) {
                        button.setVisibility(View.GONE);
                        buyHealer.setVisibility(View.VISIBLE);
                        goToHospital.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        buyHealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        goToHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
