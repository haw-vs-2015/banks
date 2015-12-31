package bank.datatypes;

import java.util.ArrayList;

public class Accounts {

    private ArrayList<String> accounts = new ArrayList<>();

    public Accounts() {}

    public ArrayList<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<String> accounts) {
        this.accounts = accounts;
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

        Accounts accs = (Accounts) obj;

        if (accs.accounts.size() != accounts.size()) {
            return false;
        }

        for (int i = 0; i < accounts.size(); i++) {
            if (!accs.accounts.get(i).equals(accounts.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;

        for (String a : accounts) {
            hashCode += a.hashCode();
        }

        return hashCode;
    }

}
