package bank.datatypes;

public class Event {

    private String type;
    private String name;
    private String reason;
    private String resource;
    private Player player;

    public Event() {}

    public String getType() {
        return type;
    }

    public synchronized void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public synchronized void setReason(String reason) {
        this.reason = reason;
    }

    public  String getResource() {
        return resource;
    }

    public synchronized void setResource(String resource) {
        this.resource = resource;
    }

    public Player getPlayer() {
        return player;
    }

    public synchronized void setPlayer(Player player) {
        this.player = player;
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

        Event event = (Event) obj;

        return event.type.equals(type) && event.name.equals(name) && event.reason.equals(reason) && event.resource.equals(resource) && event.player.equals(player);
    }

    @Override
    public int hashCode() {
        return type.hashCode() + name.hashCode() + reason.hashCode() + resource.hashCode() + player.hashCode();
    }

}
