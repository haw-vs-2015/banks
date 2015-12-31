package bank.datatypes;

public class Bank {

    private String players;
    private String transfers;

    public Bank() {}

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }

    public String getTransfers() {
        return transfers;
    }

    public void setTransfers(String transfers) {
        this.transfers = transfers;
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

        Bank bank = (Bank) obj;

        return bank.players.equals(players) && bank.transfers.equals(transfers);
    }

    @Override
    public int hashCode() {
        return players.hashCode() + transfers.hashCode();
    }

}
