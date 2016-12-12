package ir.ugstudio.vampire.events;

public class ShowTabEvent {
    private int tabIndex;

    public ShowTabEvent(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }
}
