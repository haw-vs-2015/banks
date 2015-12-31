package bank.datatypes;

import java.util.ArrayList;

public class Transfers {

    private ArrayList<String> transfers = new ArrayList<>();

    public Transfers() {}

    public ArrayList<String> getTransfers() {
        return transfers;
    }

    public void setTransfers(ArrayList<String> transfers) {
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

        Transfers transfs = (Transfers) obj;

        if (transfs.transfers.size() != transfers.size()) {
            return false;
        }

        for (int i = 0; i < transfers.size(); i++) {
            if (!transfs.transfers.get(i).equals(transfers.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;

        for (String a : transfers) {
            hashCode += a.hashCode();
        }

        return hashCode;
    }
}
