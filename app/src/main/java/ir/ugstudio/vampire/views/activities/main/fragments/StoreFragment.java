package ir.ugstudio.vampire.views.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetProfile;
import ir.ugstudio.vampire.events.FinishHealMode;
import ir.ugstudio.vampire.events.StartRealPurchase;
import ir.ugstudio.vampire.listeners.OnCompleteListener;
import ir.ugstudio.vampire.managers.SharedPrefHandler;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.StoreItemReal;
import ir.ugstudio.vampire.models.StoreItemVirtual;
import ir.ugstudio.vampire.models.StoreItems;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.Utility;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.activities.main.adapters.OnRealStoreItemClickListener;
import ir.ugstudio.vampire.views.activities.main.adapters.OnVirtualStoreItemClickListener;
import ir.ugstudio.vampire.views.activities.main.adapters.StoreItemAdapterReal;
import ir.ugstudio.vampire.views.activities.main.adapters.StoreItemAdapterVirtual;
import ir.ugstudio.vampire.views.dialogs.IntroduceVirtualItemDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreFragment extends BaseFragment implements View.OnClickListener {

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
        realItems = (RecyclerView) view.findViewById(R.id.real_items);
        virtualItems = (RecyclerView) view.findViewById(R.id.virtual_items);
    }

    private void configForHorizontal(RecyclerView itemsList) {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        itemsList.setLayoutManager(layoutManager);
    }

    private void configure() {
        configForHorizontal(realItems);
        configForHorizontal(virtualItems);

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
                    showIntroduceVirtualItemDialog(item);
                }
            });
            virtualItems.setAdapter(virtualItemsAdapter);
        }
    }

    private void showIntroduceVirtualItemDialog(final StoreItemVirtual item) {

        IntroduceVirtualItemDialog dialog = new IntroduceVirtualItemDialog(getActivity(), item, new OnCompleteListener() {
            @Override
            public void onComplete(Integer state) {
                if (state.equals(1)) {
                    startVirtualPurchase(item);
                }
            }
        });
        dialog.show();

        try {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (NullPointerException e) {
            e.printStackTrace();
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
                if (response.isSuccessful()) {
                    String result = Utility.extractResult(response.body());

                    Log.d("TAG", "ddbb " + result + " " + item.getItemId());

                    switch (result) {
                        case Consts.RESULT_OK:
                            if (item.getItemId().equals(Consts.VIRTUAL_STORE_HEAL)) {
                                EventBus.getDefault().post(new FinishHealMode());
                            }
                            Utility.makeToast(getActivity(), getString(R.string.toast_virtual_purchase_ok), Toast.LENGTH_LONG);
                            GetProfile.run(getActivity());
                            break;

                        case Consts.RESULT_NOT_ENOUGH_MONEY:
                            Utility.makeToast(getActivity(), getString(R.string.toast_virtual_purchase_not_enough_money), Toast.LENGTH_LONG);
                            break;

                        case Consts.RESULT_NOT_NEEDED:
                            if (item.getItemId().equals("heal")) {
                                Utility.makeToast(getActivity(), getString(R.string.toast_heal_not_needed), Toast.LENGTH_LONG);
                            } else {
                                Utility.makeToast(getActivity(), getString(R.string.toast_general_not_needed), Toast.LENGTH_LONG);
                            }
                            break;
                    }
                } else {
                    Utility.makeToast(getActivity(), getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utility.makeToast(getActivity(), getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT);
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
                    Utility.makeToast(getActivity(), getString(R.string.toast_real_purchase_start), Toast.LENGTH_SHORT);
                    EventBus.getDefault().post(new StartRealPurchase(item));
                }
            });
            realItems.setAdapter(realItemsAdapter);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
