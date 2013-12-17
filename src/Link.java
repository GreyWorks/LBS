
import java.util.Map;

public class Link {

    private Crossing from;
    private Crossing to;
    private long from_id;
    private long to_id;
    private int length;
    private int speed;
    private double time;

    /**
     *
     * @param from_id Id der Startkreuzung
     * @param to_id Id der Zielkreuzung
     * @param length geometrische Länge in Metern
     * @param lsiclass die LSIClass
     * @param maxspeed Geschwindigkeitsbegrenzung, 0 falls keine vorhanden
     */
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

    /**
     * sucht die angrenzenden Kreuzungen (Start und Ziel) in crossings und
     * speichert die Referenzen
     *
     * @param crossings
     */
    public void setCrossingReferences(Map<Long, Crossing> crossings) {
        this.from = crossings.get(new Long(from_id));
        this.to = crossings.get(new Long(to_id));
        this.from.outgoingLinks.add(this);
    }

    /**
     * @return die für diese Verbindung benötigte Zeit
     */
    public double getTime() {
        return time;
    }

    /**
     * @return die Zielkreuzung der Verbindung
     */
    public Crossing getTargetCrossing() {
        return to;
    }
}
