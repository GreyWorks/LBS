
import java.util.ArrayList;

/**
 *
 * @author Simon Roehrl
 * 
 */
public class Crossing {
    ArrayList<Link> links;
    private double lat;
    private double lon;
    
    public Crossing(double latitude, double longtitude) {
        this.lat = latitude;
        this.lon = longtitude;
    }

    /**
     * @return the lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * @return the lon
     */
    public double getLon() {
        return lon;
    }

    /**
     * @param lon the lon to set
     */
    public void setLon(double lon) {
        this.lon = lon;
    }
}
