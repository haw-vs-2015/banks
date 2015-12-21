package bank.utils;

public class LamportClock {

    private long time = 0;

    public synchronized void tick() {
        time++;

        System.out.println("Tick: " + time);
    }

    public synchronized long getTime() {
        return time;
    }

    public synchronized void adjustTime(long time) {
        if (this.time <= time) {
            this.time = time + 1;

            System.out.println("Adjust time to: " + this.time);
        }
    }

}
