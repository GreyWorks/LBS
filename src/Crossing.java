
import fu.geo.LatLongPosition;
import java.util.ArrayList;

public class Crossing implements Comparable<Crossing> {

    private LatLongPosition position;
    private boolean visited;
    private double costs;
    public ArrayList<Link> outgoingLinks;

    public Crossing(double latitude, double longtitude) {
        this.position = new LatLongPosition(latitude, longtitude);
        this.outgoingLinks = new ArrayList<Link>();
        this.visited = false;
    }

    public Crossing(LatLongPosition position) {
        this.position = new LatLongPosition(position);
    }

    /**
     * @return die Position der Kreuzung
     */
    public LatLongPosition getPosition() {
        return new LatLongPosition(position);
    }

    /**
     * @return wird vom Algorithmus verwendet: gibt an ob die Kreuzung bereits "besucht" 
     */
    public boolean wasVisited() {
        return visited;
    }

    /**
     * @param wird vom Algorithmus verwendet: setz die Kreuzung als "besucht"
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * @return die Kosten, die benötigt werden um vom Startpunkt zur aktuellen
     * Kreuzung zu gelangen
     */
    public double getCosts() {
        return costs;
    }

    /**
     * setzt die Kosten, die benötigt werden um vom Startpunkt zur aktuellen Kreuzung zu gelangen
     * @param costs
     */
    public void setCosts(double costs) {
        this.costs = costs;
    }

    /**
     * vergleicht zwei Kreuzungen anhand der Kosten
     * @param o
     * @return 
     */
    @Override
    public int compareTo(Crossing o) {
        if (this.getCosts() < o.getCosts()) {
            return -1;
        } else if (this.getCosts() > o.getCosts()) {
            return 1;
        } else {
            return 0;
        }
    }
}
