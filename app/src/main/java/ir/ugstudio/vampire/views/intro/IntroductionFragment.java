package ir.ugstudio.vampire.views.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;

import org.greenrobot.eventbus.EventBus;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.events.CloseIntroductionFragment;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.custom.CustomButton;

public class IntroductionFragment extends BaseFragment {

    private ViewPager viewPager;
    private CustomButton nextItem;
    private CirclePageIndicator indicator;
    private IntroPagerAdapter pagerAdapter;

    public static IntroductionFragment getInstance() {
        return new IntroductionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_introduction, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find(view);
        configure();
    }

    private void find(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.intro_items);
        nextItem = (CustomButton) view.findViewById(R.id.next_item);
        indicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
    }

    private void configure() {
        pagerAdapter = new IntroPagerAdapter(getContext());
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);

        nextItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem == pagerAdapter.getCount() - 1) {
                    EventBus.getDefault().post(new CloseIntroductionFragment());
                } else {
                    viewPager.setCurrentItem(currentItem + 1);
                }
            }
        });
    }
}
