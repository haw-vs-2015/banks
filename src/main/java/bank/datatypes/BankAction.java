package bank.datatypes;

import java.util.ArrayList;

public class BankAction {

    private long lamportTimestamp;
    private String actionId;
    private Command command;
    private String gameId;

    private transient boolean confirmed = false;

    public BankAction() {}

    public BankAction(long lamportTimestamp, String actionId, String gameId, Command command) {
        this.lamportTimestamp = lamportTimestamp;
        this.actionId = actionId;
        this.command = command;
        this.gameId = gameId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public long getLamportTimestamp() {
        return lamportTimestamp;
    }

    public String getActionId() {
        return actionId;
    }

    public String getGameId() {
        return gameId;
    }

    public Command getCommand() {
        return command;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (obj.getClass() == getClass()) {
            return false;
        }

        BankAction action = (BankAction) obj;

        return action.lamportTimestamp == lamportTimestamp && action.actionId.equals(actionId);
    }

    @Override
    public int hashCode() {
        return actionId.hashCode();
    }

}
