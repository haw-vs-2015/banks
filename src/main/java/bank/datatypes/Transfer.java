package bank.datatypes;

public class Transfer {

    private String to;
    private String from;
    private int amount;
    private String reason;
    private String event;

    public Transfer() {}

    public String getTo() {
        return to;
    }

    public synchronized void setTo(String to) {
        this.to = to;
    }

    public  String getFrom() {
        return from;
    }

    public synchronized void setFrom(String from) {
        this.from = from;
    }

    public String getReason() {
        return reason;
    }

    public synchronized void setReason(String reason) {
        this.reason = reason;
    }

    public String getEvent() {
        return event;
    }

    public synchronized void setEvent(String event) {
        this.event = event;
    }

    public int getAmount() {
        return amount;
    }

    public synchronized void setAmount(int amount) {
        this.amount = amount;
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

        Transfer transfer = (Transfer) obj;

        return transfer.to.equals(to) && transfer.from.equals(from) && transfer.reason.equals(reason) && transfer.event.equals(event);
    }

    @Override
    public int hashCode() {
        return to.hashCode() + from.hashCode() + reason.hashCode() + event.hashCode();
    }

}
