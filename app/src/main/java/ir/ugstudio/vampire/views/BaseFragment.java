package ir.ugstudio.vampire.views;

import android.support.v4.app.Fragment;
import android.util.Log;

import ir.ugstudio.vampire.VampireApp;

public abstract class BaseFragment extends Fragment {
    public void onBringToFront() {
        try {
            String screen = getClass().getName();
            if (screen.contains(".")) {
                screen = screen.substring(screen.lastIndexOf('.') + 1);
            }
            Log.d("TAG", "getClass " + screen);
            VampireApp.firebaseAnalytics.setCurrentScreen(getActivity(), screen, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
