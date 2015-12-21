package bank.datatypes;

public class Place {

    private String name;

    public Place() {}

    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
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

        Place place = (Place) obj;

        return place.name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
