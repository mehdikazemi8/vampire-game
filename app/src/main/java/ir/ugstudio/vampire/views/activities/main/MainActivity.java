package ir.ugstudio.vampire.views.activities.main;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.async.GetQuotes;
import ir.ugstudio.vampire.managers.CacheManager;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.FontHelper;
import ir.ugstudio.vampire.views.activities.main.adapters.MainFragmentsPagerAdapter;
import ir.ugstudio.vampire.views.activities.main.fragments.MapFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.NotificationsFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.RanklistFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.SettingsFragment;
import ir.ugstudio.vampire.views.activities.main.fragments.ShopFragment;

public class MainActivity extends FragmentActivity {

    public static final int NUMBER_OF_FRAGMENTS = 5;
    private static Fragment[] fragments = new Fragment[NUMBER_OF_FRAGMENTS];
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
    }

    private void setFragments() {
        fragments[0] = SettingsFragment.getInstance();
        fragments[1] = ShopFragment.getInstance();
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
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        EventBus.getDefault().register(this);
    }
}