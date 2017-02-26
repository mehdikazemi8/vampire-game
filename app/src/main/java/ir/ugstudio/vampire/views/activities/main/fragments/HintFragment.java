package ir.ugstudio.vampire.views.activities.main.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.introduction.BaseIntroductionMessages;
import ir.ugstudio.vampire.utils.introduction.IntroductionMessagesHandler;
import ir.ugstudio.vampire.views.BaseFragment;
import ir.ugstudio.vampire.views.custom.CustomTextView;

public class HintFragment extends BaseFragment {

    @BindView(R.id.hint_message)
    CustomTextView hintMessage;

    @BindView(R.id.introduction_avatar)
    ImageView introductionAvatar;

    private Queue<String> messagesQueue = new LinkedList<>();
    private Unbinder unbinder;
    private boolean zereshkiBackground = false;

    public static HintFragment getInstance() {
        return new HintFragment();
    }

    private void readArguments() {
        BaseIntroductionMessages introType = (BaseIntroductionMessages) getArguments().getSerializable(Consts.BASE_INTRODUCTION_MESSAGES_OBJECT);
        zereshkiBackground = getArguments().getBoolean(Consts.INTRODUCTION_MESSAGE_BACKGROUND_ZERESHKI, false);
        for (String message : IntroductionMessagesHandler.getMessages(getActivity(), introType)) {
            Log.d("TAG", "BaseIntroductionMessages " + message);
            messagesQueue.add(message);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        readArguments();

        View view = inflater.inflate(R.layout.fragment_hint, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showNextMessage();

        if(CacheHandler.getUser() != null) {
            if (CacheHandler.getUser().getRole().equals(Consts.ROLE_HUNTER)) {
                Picasso.with(getActivity()).load(R.drawable.hunt2000).into(introductionAvatar);
            } else {
                Picasso.with(getActivity()).load(R.drawable.vamp1001).into(introductionAvatar);
            }
        }

        if (zereshkiBackground) {
            hintMessage.setBackgroundColor(Color.parseColor(getString(R.string.color_zereshki)));
            hintMessage.setTextColor(Color.parseColor(getString(R.string.color_gray)));
        } else {

        }
    }

    private void showNextMessage() {
        Log.d("TAG", "showNextMessage " + messagesQueue.size());

        if (messagesQueue.isEmpty()) {
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            hintMessage.setText(messagesQueue.remove());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.hint_message)
    public void hintMessageOnClick() {
        showNextMessage();
    }
}
