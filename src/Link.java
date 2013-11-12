
import java.util.Map;

/**
 *
 * @author Simon Roehrl
 */
public class Link {
    private Crossing from;
    private Crossing to;
    
    private long from_id;
    private long to_id;
    
    /**
     * in meters
     */
    private int length;
    private int lsiclass;
    private int maxspeed;
        
    public Link(long from_id, long to_id, int length, int lsiclass, int maxspeed) {
        this.from_id = from_id;
        this.to_id = to_id;
        this.length = length;
        this.lsiclass = lsiclass;
        this.maxspeed = maxspeed;
        
    }
    
    public void setCrossingReferences(Map<Long,Crossing> crossings) {
        this.from = crossings.get(new Long(from_id));
        this.to = crossings.get(new Long(to_id));
        this.from.outgoingLinks.add(this);
    }
             
}
