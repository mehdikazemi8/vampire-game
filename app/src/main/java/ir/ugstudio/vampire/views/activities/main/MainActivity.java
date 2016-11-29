package ir.ugstudio.vampire.views.activities.main;

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

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.async.SendFCMIdToServer;
import ir.ugstudio.vampire.cafeutil.IabHelper;
import ir.ugstudio.vampire.cafeutil.IabResult;
import ir.ugstudio.vampire.cafeutil.Inventory;
import ir.ugstudio.vampire.cafeutil.Purchase;
import ir.ugstudio.vampire.events.ConsumePurchase;
import ir.ugstudio.vampire.events.StartPurchase;
import ir.ugstudio.vampire.utils.FontHelper;
import ir.ugstudio.vampire.views.activities.main.adapters.MainFragmentsPagerAdapter;
import ir.ugstudio.vampire.views.activities.main.fragments.MapFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.NotificationsFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.RanklistFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.SettingsFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.StoreFragment;

public class MainActivity extends FragmentActivity {

    public static final int NUMBER_OF_FRAGMENTS = 5;
    // Debug tag, for logging
    static final String TAG = "";
    // SKUs for our products: the premium upgrade (non-consumable)
    static final String SKU_PREMIUM = "coin-500";
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 1367;
    private static Fragment[] fragments = new Fragment[NUMBER_OF_FRAGMENTS];
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
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            } else if (purchase.getSku().equals(SKU_PREMIUM)) {
                mHelper.consumeAsync(purchase, onConsumeFinishedListener);
                // give user access to premium content and update the UI
            }
        }
    };

    IabHelper.OnConsumeFinishedListener onConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d("TAG", "now consume: " + result.getMessage());
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
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(4);
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
    public void onEvent(StartPurchase event) {
        if (mHelper != null)
            mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST, mPurchaseFinishedListener, "payload-string11");
        else
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onEvent(ConsumePurchase event) {
        Toast.makeText(this, "consume purchase start", Toast.LENGTH_SHORT).show();

        if (mHelper != null) {
//            mHelper.consumeAsync();
        } else {
            Toast.makeText(this, "consume purchase null", Toast.LENGTH_SHORT).show();
        }
    }
}