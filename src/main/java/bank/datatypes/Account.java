package bank.datatypes;

import bank.exceptions.InsufficientFondsException;

import java.util.concurrent.locks.ReentrantLock;

public class Account {

    private Player player;
    private int saldo;

    private transient int tempSaldo = 0;
    private transient final ReentrantLock lock = new ReentrantLock();

    public Account() {}

    public Player getPlayer() {
        return player;
    }

    public synchronized void setPlayer(Player player) {
        this.player = player;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) throws InsufficientFondsException {
        if (lock.isHeldByCurrentThread()) {
            if (saldo < 0) {
                throw new InsufficientFondsException();
            } else {
                tempSaldo = saldo;
            }
        }
    }

    public void addSaldo(int amount) {
        if (lock.isHeldByCurrentThread()) {
            tempSaldo += amount;
        }
    }

    public void subSaldo(int amount) throws InsufficientFondsException {
        if (lock.isHeldByCurrentThread()) {
            if (tempSaldo - amount < 0) {
                throw new InsufficientFondsException();
            } else {
                tempSaldo -= amount;
            }
        }
    }

    public void beginTransaction() {
        lock.lock();

        tempSaldo = saldo;
    }

    public void commit() {
        saldo = tempSaldo;

        lock.unlock();
    }

    public void rollback() {
        if (lock.isHeldByCurrentThread()) {
            tempSaldo = saldo;
        }
    }

    public boolean isValid() {
        return player != null;
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

        Account account = (Account) obj;

        return account.player.equals(player);
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }

}
