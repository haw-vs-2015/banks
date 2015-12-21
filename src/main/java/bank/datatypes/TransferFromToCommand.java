package bank.datatypes;

import bank.BankService;

import java.io.IOException;
import java.util.ArrayList;

public class TransferFromToCommand implements Command {

    private String playerAId;
    private String playerBId;
    private int amount;
    private String reason;
    private String gameId;
    private String actionAId;
    private String actionBId;

    public TransferFromToCommand() {}

    public TransferFromToCommand(String playerAId, String playerBId, int amount, String reason, String gameId, String actionId) {
        this.playerAId = playerAId;
        this.playerBId = playerBId;
        this.amount = amount;
        this.reason = reason;
        this.gameId = gameId;
        this.actionAId = actionId;
        this.actionBId = new StringBuilder(actionId).reverse().toString();
    }

    public String getPlayerAId() {
        return playerAId;
    }

    public void setPlayerAId(String playerAId) {
        this.playerAId = playerAId;
    }

    public String getPlayerBId() {
        return playerBId;
    }

    public void setPlayerBId(String playerBId) {
        this.playerBId = playerBId;
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

    public String getActionAId() {
        return actionAId;
    }

    public String getActionBId() {
        return actionBId;
    }

    public void setActionId(String actionId) {
        this.actionAId = actionId;
        this.actionBId = new StringBuilder(actionId).reverse().toString();
    }

    @Override
    public ArrayList<Event> exec(BankService service) throws IOException {
        return service.transferFromTo(playerAId, playerBId, amount, reason, gameId, actionAId, actionBId);
    }

}
