
import fu.geo.LatLongPosition;
import java.util.ArrayList;

public class Crossing {
    private LatLongPosition position;
    
    /**
     * bool used in algorithm to signal if crossing was already visited
     */
    private boolean visited;
    
    private double remainingTime;
    
    public ArrayList<Link> outgoingLinks;
        
    public Crossing(double latitude, double longtitude) {
        this.position = new LatLongPosition(latitude, longtitude);
        this.outgoingLinks = new ArrayList<Link>();
        this.visited = false;
    }
    
    public Crossing (LatLongPosition position) {
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
     * @return the remainingTime
     */
    public double getRemainingTime() {
        return remainingTime;
    }

    /**
     * @param remainingTime the remainingTime to set
     */
    public void setRemainingTime(double remainingTime) {
        this.remainingTime = remainingTime;
    }

}
