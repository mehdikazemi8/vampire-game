package ir.ugstudio.vampire.interfaces;

import ir.ugstudio.vampire.utils.introduction.BaseIntroductionMessages;

public interface MainActivityActions {
    public void openActionsFragment();

    public void openTowerOptionsFragment();

    public void openMissionOptionsFragment();

    public void openHintFragment(BaseIntroductionMessages introductionType);
}
