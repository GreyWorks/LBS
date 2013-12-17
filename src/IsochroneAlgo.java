
import java.util.Collection;
import java.util.Set;


public interface IsochroneAlgo {
    /**
     * berechnet alle erreichbaren Kreuzungen in der Zeit time vom Startpunkt startCrossing
     * @param crossings alle Kreuzungen die potentiell erreicht werden könnten
     * @param links alle Verbindungen die potentiell erreicht werden können
     * @param startCrossing die Startkreuzung
     * @param time die verfügbare Zeit in Sekunden
     * @return 
     */
    public Set<Crossing> calculate(Collection<Crossing> crossings, Collection<Link> links, Crossing startCrossing, double time);
    
}
