
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SortedSetDijkstraAlgo implements ReachableAlgo {

    @Override
    public Set<Crossing> calculate(Collection<Crossing> crossings, Collection<Link> links, Crossing startCrossing, double time) {
        Set<Crossing> resultSet = new HashSet<Crossing>();

        // TreeSet sortiert sofort beim Einfügen, das kleinste Element ist immer "first"
        TreeSet<Crossing> openList = new TreeSet<Crossing>();

        // mit dem größt möglichen Wert initialisieren => unendliche Kosten
        for (Crossing crossing : crossings) {
            crossing.setCosts(Double.POSITIVE_INFINITY);
        }

        startCrossing.setCosts(0);

        openList.add(startCrossing);
        /*-------------------------------------------------*/


        while (!openList.isEmpty()) {
            // das erste Element holen
            Crossing currentCrossing = openList.pollFirst();
            resultSet.add(currentCrossing);   
            //openList.remove(currentCrossing);

            for (Link l : currentCrossing.outgoingLinks) {
                double newCosts = currentCrossing.getCosts() + l.getTime();
                // neue Kosten setzen wenn sie geringer sind als bisherige
                if (newCosts < l.getTargetCrossing().getCosts()) {
                    l.getTargetCrossing().setCosts(newCosts);
                }
                // nur wenn Kosten noch im Bereich sind zur OpenListe hinzufügen
                if (l.getTargetCrossing().getCosts() <= time) {
                    if (!l.getTargetCrossing().wasVisited()) {
                        l.getTargetCrossing().setVisited(true);
                        openList.add(l.getTargetCrossing());
                    }
                }
            }
        }

        return resultSet;
    }
}
