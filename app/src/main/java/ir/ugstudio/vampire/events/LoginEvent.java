package ir.ugstudio.vampire.events;

import ir.ugstudio.vampire.models.User;

public class LoginEvent {
    User user;
    boolean successfull;

    public LoginEvent(User user, boolean successfull) {
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
