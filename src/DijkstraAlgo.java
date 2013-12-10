
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

        for (Crossing crossing : crossings) {
            crossing.setRemainingTime(Double.POSITIVE_INFINITY);
        }

        startCrossing.setRemainingTime(0);

        openList.add(startCrossing);
        /*-------------------------------------------------*/


        while (!openList.isEmpty()) {
            Crossing currentCrossing = openList.get(0);
            resultSet.add(currentCrossing);
            openList.remove(0);

            for (Link l : currentCrossing.outgoingLinks) {
                double newRemainingTime = currentCrossing.getRemainingTime() + l.getTime();
                if (newRemainingTime < l.getTargetCrossing().getRemainingTime()) {
                    l.getTargetCrossing().setRemainingTime(newRemainingTime);
                }
                if (l.getTargetCrossing().getRemainingTime() <= time) {
                    openList.add(l.getTargetCrossing());
                    Collections.sort(openList);
                }
            }
        }

        return resultSet;
    }
}
