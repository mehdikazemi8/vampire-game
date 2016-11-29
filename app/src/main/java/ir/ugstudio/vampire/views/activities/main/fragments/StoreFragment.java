package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.events.StartRealPurchase;
import ir.ugstudio.vampire.managers.SharedPrefManager;
import ir.ugstudio.vampire.models.StoreItemReal;
import ir.ugstudio.vampire.models.StoreItems;
import ir.ugstudio.vampire.views.activities.main.adapters.OnRealStoreItemClickListener;
import ir.ugstudio.vampire.views.activities.main.adapters.StoreItemAdapterReal;

public class StoreFragment extends Fragment implements View.OnClickListener {

    private Button startPurchase;
    private RecyclerView virtualItems;
    private RecyclerView realItems;
    private StoreItemAdapterReal realItemsAdapter;
    private List<StoreItemReal> realItemsList;

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
        realItems = (RecyclerView) view.findViewById(R.id.real_items);
        virtualItems = (RecyclerView) view.findViewById(R.id.virtual_items);
    }

    private void configure() {
        startPurchase.setOnClickListener(this);
        realItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        virtualItems.setLayoutManager(new LinearLayoutManager(getActivity()));

        configureRealItems();
    }

    private void configureRealItems() {
        StoreItems storeItems = SharedPrefManager.readStoreItems(getActivity());
        if (storeItems != null) {
            Log.d("TAG", "configureRealItems " + storeItems.getReals().size());
            realItemsList = storeItems.getReals();
            realItemsAdapter = new StoreItemAdapterReal(realItemsList, new OnRealStoreItemClickListener() {
                @Override
                public void onItemClick(StoreItemReal item) {
                    Toast.makeText(getActivity(), item.getItemSku(), Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new StartRealPurchase(item));
                }
            });
            realItems.setAdapter(realItemsAdapter);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_purchase:
//                EventBus.getDefault().post(new StartRealPurchase());
                break;
        }
    }
}
