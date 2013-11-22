
import java.util.Map;

public class Link {

    private Crossing from;
    private Crossing to;
    private long from_id;
    private long to_id;
    // TODO length und speed muss nicht unbedingt gespeichert werden, time reicht aus
    /**
     * in meters
     */
    private int length;
    private int speed;
    /**
     * in seconds
     */
    private double time;

    public Link(long from_id, long to_id, int length, int lsiclass, int maxspeed) {
        this.from_id = from_id;
        this.to_id = to_id;
        this.length = length;
        int lsiSpeed = 0;
        try {
            lsiSpeed = LSISpeed.getSpeedById(lsiclass);
        } catch (Exception e) {
            System.out.println("LSIClass not in List: " + lsiclass);
            System.exit(1);
        }

        if (maxspeed != 0 && maxspeed < lsiSpeed) {
            this.speed = maxspeed;
        } else {
            this.speed = lsiSpeed;
        }

        this.time = (double) this.length / (double) (this.speed * 1000 / 60 / 60);
    }

    public void setCrossingReferences(Map<Long, Crossing> crossings) {
        this.from = crossings.get(new Long(from_id));
        this.to = crossings.get(new Long(to_id));
        this.from.outgoingLinks.add(this);
    }

    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /**
     * @return the to
     */
    public Crossing getTargetCrossing() {
        return to;
    }
}
