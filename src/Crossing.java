
import fu.geo.LatLongPosition;
import java.util.ArrayList;

public class Crossing implements Comparable<Crossing> {

    private LatLongPosition position;
    /**
     * bool used in algorithm to signal if crossing was already visited
     */
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
     * @return the position
     */
    public LatLongPosition getPosition() {
        return new LatLongPosition(position);
    }

    /**
     * @return the visited
     */
    public boolean wasVisited() {
        return visited;
    }

    /**
     * @param visited the visited to set
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * @return the costs
     */
    public double getCosts() {
        return costs;
    }

    /**
     * @param costs the costs to set
     */
    public void setCosts(double costs) {
        this.costs = costs;
    }

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
