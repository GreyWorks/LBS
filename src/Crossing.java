
import fu.geo.LatLongPosition;
import java.util.ArrayList;

/**
 *
 * @author Simon Roehrl
 * 
 */
public class Crossing {
    private LatLongPosition position;
    
    public ArrayList<Link> outgoingLinks;
        
    public Crossing(double latitude, double longtitude) {
        this.position = new LatLongPosition(latitude, longtitude);
        this.outgoingLinks = new ArrayList<Link>();
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

}
