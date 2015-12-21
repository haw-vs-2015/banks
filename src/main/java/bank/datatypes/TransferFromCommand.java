package bank.datatypes;

import bank.BankService;

import java.io.IOException;
import java.util.ArrayList;

public class TransferFromCommand implements Command {

    private String playerId;
    private int amount;
    private String reason;
    private String gameId;
    private String actionId;

    public TransferFromCommand() {}

    public TransferFromCommand(String playerId, int amount, String reason, String gameId, String actionId) {
        this.playerId = playerId;
        this.amount = amount;
        this.reason = reason;
        this.gameId = gameId;
        this.actionId = actionId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    @Override
    public ArrayList<Event> exec(BankService service) throws IOException {
        return service.transferFrom(playerId, amount, reason, gameId, actionId);
    }

}
