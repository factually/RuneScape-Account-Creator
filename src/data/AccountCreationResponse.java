package data;

public class AccountCreationResponse {
    private Action action;
    private String message;

    public AccountCreationResponse(final Action action, final String message) {
        this.action = action;
        this.message = message;
    }

    public enum Action {
        TRY_AGAIN,
        cProxy,
        SAVE_ACCOUNT
    }

    public Action getAction() {
        return action;
    }

    public String getMessage() {
        return message;
    }
}
