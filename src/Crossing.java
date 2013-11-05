
import fu.geo.LatLongPosition;
import java.util.ArrayList;

/**
 *
 * @author Simon Roehrl
 * 
 */
public class Crossing {
    ArrayList<Link> links;
    private LatLongPosition position;
        
    public Crossing(double latitude, double longtitude) {
        this.position = new LatLongPosition(latitude, longtitude);
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
