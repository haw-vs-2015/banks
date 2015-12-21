package bank.datatypes;

import bank.BankService;

public class CreateAccountCommand implements Command {

    private Account account;
    private String gameId;

    public CreateAccountCommand() {}

    public CreateAccountCommand(Account account, String gameId) {
        this.account = account;
        this.gameId = gameId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public Boolean exec(BankService service) throws Exception {
        return service.createAccount(account, gameId);
    }

}
