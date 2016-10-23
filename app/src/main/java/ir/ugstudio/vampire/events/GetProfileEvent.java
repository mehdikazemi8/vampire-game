package ir.ugstudio.vampire.events;

import ir.ugstudio.vampire.models.User;

public class GetProfileEvent {
    User user;
    boolean successfull;

    public GetProfileEvent(User user, boolean successfull) {
        this.user = user;
        this.successfull = successfull;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSuccessfull() {
        return successfull;
    }

    public void setSuccessfull(boolean successfull) {
        this.successfull = successfull;
    }
}
