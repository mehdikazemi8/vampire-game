package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetProfile;
import ir.ugstudio.vampire.events.FinishHealMode;
import ir.ugstudio.vampire.events.StartRealPurchase;
import ir.ugstudio.vampire.managers.SharedPrefHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.StoreItemReal;
import ir.ugstudio.vampire.models.StoreItemVirtual;
import ir.ugstudio.vampire.models.StoreItems;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.activities.main.adapters.OnRealStoreItemClickListener;
import ir.ugstudio.vampire.views.activities.main.adapters.OnVirtualStoreItemClickListener;
import ir.ugstudio.vampire.views.activities.main.adapters.StoreItemAdapterReal;
import ir.ugstudio.vampire.views.activities.main.adapters.StoreItemAdapterVirtual;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreFragment extends BaseFragment implements View.OnClickListener {

    private Button startPurchase;
    private RecyclerView virtualItems;
    private RecyclerView realItems;
    private StoreItemAdapterReal realItemsAdapter;
    private StoreItemAdapterVirtual virtualItemsAdapter;
    private List<StoreItemReal> realItemsList;
    private List<StoreItemVirtual> virtualItemsList;

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
        configureVirtualItems();
    }

    private void configureVirtualItems() {
        StoreItems storeItems = SharedPrefHandler.readStoreItems(getActivity());
        if (storeItems != null && storeItems.getVirtuals() != null) {
            virtualItemsList = storeItems.getVirtuals();
            virtualItemsAdapter = new StoreItemAdapterVirtual(virtualItemsList, new OnVirtualStoreItemClickListener() {
                @Override
                public void onItemClick(StoreItemVirtual item) {
                    startVirtualPurchase(item);
                }
            });
            virtualItems.setAdapter(virtualItemsAdapter);
        }
    }

    private void startVirtualPurchase(final StoreItemVirtual item) {
        String token = UserHandler.readToken(getActivity());
        Call<ResponseBody> call = VampireApp.createUserApi().virtualPurchase(
                token, item.getItemId()
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String result = "IOEXception";
                try {
                    result = response.body().string();
                    Log.d("TAG", "xxx " + result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    if (result.equals("not_enough_money")) {
                        Toast.makeText(getContext(), "به نظر سکه‌ی کافی نداری :(", Toast.LENGTH_LONG).show();
                    } else {
                        if(item.getItemId().equals(Consts.VIRTUAL_STORE_HEAL)) {
                            EventBus.getDefault().post(new FinishHealMode());
                        }
                        Toast.makeText(getContext(), "انجام شد :) می تونی ادامه بدی", Toast.LENGTH_LONG).show();
                        GetProfile.run(getActivity());
                    }
                } else {
                    Toast.makeText(getContext(), "مشکلی پیش اومده لطفا دوباره امتحان کن", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void configureRealItems() {
        StoreItems storeItems = SharedPrefHandler.readStoreItems(getActivity());
        if (storeItems != null && storeItems.getReals() != null) {
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

    @Override
    public void onBringToFront() {
        super.onBringToFront();
        Log.d("TAG", "onBringToFront StoreFragment");

        configureRealItems();
        configureVirtualItems();
    }
}
