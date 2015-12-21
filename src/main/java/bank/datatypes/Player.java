package bank.datatypes;

public class Player {

    private String id;
    private String name;
    private String uri;
    private Place place;
    private int position;
    private boolean ready;

    public Player() {}

    public String getId() {
        return id;
    }

    public synchronized void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public synchronized void setUri(String uri) {
        this.uri = uri;
    }

    public int getPosition() {
        return position;
    }

    public synchronized void setPosition(int position) {
        this.position = position;
    }

    public Place getPlace() {
        return place;
    }

    public synchronized void setPlace(Place place) {
        this.place = place;
    }

    public boolean isReady() {
        return ready;
    }

    public synchronized void setReady(boolean ready) {
        this.ready = ready;
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

        Player player = (Player) obj;

        return player.id.equals(id) && player.name.equals(name);
    }

    @Override
    public int hashCode() {
        return id.hashCode() + name.hashCode();
    }

}
