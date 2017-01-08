package ir.ugstudio.vampire.views.activities.main.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import ir.ugstudio.vampire.views.activities.main.MainActivity;

public class MainFragmentsPagerAdapter extends FragmentPagerAdapter {
    // TODO must be filled by reading resources (e.g. icon.home, icon.settings)
//    private String tabTitles[] = new String[] { "E", "D", "C", "B", "A" };
    private String tabTitles[] = new String[] { "", "", "", "", "" };
    private Context context = null;

    public MainFragmentsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return MainActivity.NUMBER_OF_FRAGMENTS;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("TAG", "MainFragmentsPagerAdapter getItem " + position);
        return MainActivity.getFragment(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
