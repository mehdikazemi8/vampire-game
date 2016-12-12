package ir.ugstudio.vampire.views.activities.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.VampireApp;
import ir.ugstudio.vampire.async.GetProfile;
import ir.ugstudio.vampire.async.SendFCMIdToServer;
import ir.ugstudio.vampire.cafeutil.IabHelper;
import ir.ugstudio.vampire.cafeutil.IabResult;
import ir.ugstudio.vampire.cafeutil.Inventory;
import ir.ugstudio.vampire.cafeutil.Purchase;
import ir.ugstudio.vampire.events.OpenTowerWallFragment;
import ir.ugstudio.vampire.events.StartRealPurchase;
import ir.ugstudio.vampire.managers.UserHandler;
import ir.ugstudio.vampire.models.StoreItemReal;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.FontHelper;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.activities.main.adapters.MainFragmentsPagerAdapter;
import ir.ugstudio.vampire.views.activities.main.fragments.MapFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.NotificationsFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.RanklistFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.SettingsFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.StoreFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.TowerWallFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends FragmentActivity {

    public static final int NUMBER_OF_FRAGMENTS = 5;
    // Debug tag, for logging
    static final String TAG = "";
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 1367;
    // SKUs for our products: the premium upgrade (non-consumable)
    static String SKU_PREMIUM = "coin-500";
    private static BaseFragment[] fragments = new BaseFragment[NUMBER_OF_FRAGMENTS];
    // Does the user have the premium upgrade?
    boolean mIsPremium = false;
    // The helper object
    IabHelper mHelper;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                return;
            } else {
                Log.d(TAG, "Query inventory was successful.");
                // does the user have the premium upgrade?
                mIsPremium = inventory.hasPurchase(SKU_PREMIUM);

                // update UI accordingly

                // TODO must be here? because we have consumed it after buying
//                Purchase thePurchase = inventory.getPurchase(SKU_PREMIUM);
//                mHelper.consumeAsync(thePurchase, onConsumeFinishedListener);

                Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
            }

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };
    IabHelper.OnConsumeFinishedListener onConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d("TAG", "now consume: " + result.getMessage());
            Toast.makeText(MainActivity.this, "consume " + purchase.getSku(), Toast.LENGTH_SHORT).show();
            finalizeRealPurchase(purchase);
        }
    };
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            } else if (purchase.getSku().equals(SKU_PREMIUM)) {

                confirmRealPurchase(purchase);

                // give user access to premium content and update the UI
            }
        }
    };
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static Fragment getFragment(int position) {
        return fragments[position];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFragments();
        find();
        configure();

        SendFCMIdToServer.run(FirebaseInstanceId.getInstance().getToken());

        configureInAppPurchase();
    }

    private void setFragments() {
        fragments[0] = SettingsFragment.getInstance();
        fragments[1] = StoreFragment.getInstance();
        fragments[2] = NotificationsFragment.getInstance();
        fragments[3] = RanklistFragment.getInstance();
        fragments[4] = MapFragment.getInstance();
    }

    private void find() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MainFragmentsPagerAdapter(getSupportFragmentManager(), MainActivity.this));

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void configure() {
        configureTabLayoutFont();
        configureViewPager();
    }

    private void configureViewPager() {
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("TAG", "onPageSelected " + position);
                fragments[position].onBringToFront();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void configureTabLayoutFont() {
        ViewGroup viewGroup = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = viewGroup.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup viewGroupTab = (ViewGroup) viewGroup.getChildAt(j);
            int tabChildsCount = viewGroupTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = viewGroupTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(FontHelper.getIcons(this), Typeface.NORMAL);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    private void configureInAppPurchase() {
        // TODO
        String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDc/CHiRKlKgfAcz7F2SgSyYOnH7RTbLT8yD/99DMKMVP+dF8Tr2nuYSR+G/buK6UiR+ST+nQFE8KYGNMSEaCd5ONTIXwx6YKb8ORLFNdRmUxR6wf60BnRsfYSm7uP2VhIMtFyH2MAJXLFVA31T7WAjvufg5vvZR4iq7yih8RWvVtgfVY8QVh0hzPh25LqiXuS/Ysymfe1R+fsNuchXwd2ZPSVmR1FTVpyVbX8oN3sCAwEAAQ==";
        // You can find it in your Bazaar console, in the Dealers section.
        // It is recommended to add more security than just pasting it in your source code;
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Subscribe
    public void onEvent(StartRealPurchase event) {
        if (mHelper != null) {
            startRealPurchase(event.getItem());
        } else {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onEvent(OpenTowerWallFragment event) {
        TowerWallFragment fragment = TowerWallFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable("tower", event.getTower());
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_holder, fragment, Consts.FRG_WALL)
                .addToBackStack(null)
                .commit();
    }

    private void startRealPurchase(StoreItemReal item) {
        SKU_PREMIUM = item.getItemSku();

        String token = UserHandler.readToken(this);
        Call<ResponseBody> call = VampireApp.createUserApi().startRealPurchase(token, item.getItemSku());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    continueRealPurchase(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void continueRealPurchase(ResponseBody response) {
        String developerPayload = "IOEXception";
        try {
            developerPayload = response.string();
            mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST, mPurchaseFinishedListener, developerPayload);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void confirmRealPurchase(final Purchase purchase) {
        String token = UserHandler.readToken(this);
        Call<ResponseBody> call = VampireApp.createUserApi().confirmRealPurchase(
                token, purchase.getOrderId(), purchase.getDeveloperPayload()
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    mHelper.consumeAsync(purchase, onConsumeFinishedListener);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void finalizeRealPurchase(final Purchase purchase) {
        String token = UserHandler.readToken(this);
        Call<ResponseBody> call = VampireApp.createUserApi().finalizeRealPurchase(
                token, purchase.getDeveloperPayload()
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    GetProfile.run(MainActivity.this);
                    Toast.makeText(MainActivity.this, "finalize " + purchase.getSku(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        if( getSupportFragmentManager().findFragmentByTag(Consts.FRG_WALL) != null ) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        // todo, in case some other fragment is added to backstack in MainActivity
        confirmExit();
    }

    private void confirmExit() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("مطمئنی می خوای خارج بشی؟")
                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
        FontHelper.setKoodakFor(MainActivity.this, (TextView) dialog.findViewById(android.R.id.message));
    }
}