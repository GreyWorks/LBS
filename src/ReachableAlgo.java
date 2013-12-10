
import java.util.Collection;
import java.util.Set;


public interface ReachableAlgo {
    public Set<Crossing> calculate(Collection<Crossing> crossings, Collection<Link> links, Crossing startCrossing, double time);
    
}
