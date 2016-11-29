package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.events.StartPurchase;

public class StoreFragment extends Fragment implements View.OnClickListener {

    private Button startPurchase;

    public static StoreFragment getInstance() {
        return new StoreFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find(view);
        configure();
    }

    private void find(View view) {
        startPurchase = (Button) view.findViewById(R.id.start_purchase);
    }

    private void configure() {
        startPurchase.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_purchase:
                EventBus.getDefault().post(new StartPurchase());
                break;
        }
    }
}
