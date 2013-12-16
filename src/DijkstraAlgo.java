
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DijkstraAlgo implements ReachableAlgo {

    @Override
    public Set<Crossing> calculate(Collection<Crossing> crossings, Collection<Link> links, Crossing startCrossing, double time) {
        Set<Crossing> resultSet = new HashSet<Crossing>();

        List<Crossing> openList = new ArrayList<Crossing>();

        // mit dem größt möglichen Wert initialisieren => unendliche Kosten
        for (Crossing crossing : crossings) {
            crossing.setCosts(Double.POSITIVE_INFINITY);
        }

        startCrossing.setCosts(0);

        openList.add(startCrossing);
        /*-------------------------------------------------*/


        while (!openList.isEmpty()) {
            // das erste Element in der OpenListe hat die geringsten Kosten
            Crossing currentCrossing = openList.remove(0);
            resultSet.add(currentCrossing);     

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
                        // liste nach Kosten sortieren
                        Collections.sort(openList);
                    }
                }
            }
        }

        return resultSet;
    }
}
