
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class Algo {

    public static HashSet<Crossing> calculate(Collection<Crossing> crossings, Collection<Link> links, Crossing startCrossing, double time) {
        HashSet<Crossing> resultList = new HashSet<Crossing>();
        HashSet<Crossing> newOpenList;
        HashSet<Crossing> openList = new HashSet<Crossing>();

        startCrossing.setRemainingTime(time);
        startCrossing.setVisited(true);

        openList.add(startCrossing);
        while (!openList.isEmpty()) {
            newOpenList = new HashSet<Crossing>();
            for (Crossing openCrossing : openList) {
                for (Link link : openCrossing.outgoingLinks) {
                    if (!link.getTargetCrossing().wasVisited()) {
                        if (openCrossing.getRemainingTime() - link.getTime() < 0) {
                            resultList.add(openCrossing);
                        } else {
                            link.getTargetCrossing().setRemainingTime(openCrossing.getRemainingTime() - link.getTime());
                            link.getTargetCrossing().setVisited(true);
                            newOpenList.add(link.getTargetCrossing());
                            resultList.add(link.getTargetCrossing()); // punkte in der mitte werden auch addiert
                        }
                    } else {
                        if (link.getTargetCrossing().getRemainingTime() < openCrossing.getRemainingTime() - link.getTime()) {
                            //update
                            link.getTargetCrossing().setRemainingTime(openCrossing.getRemainingTime() - link.getTime());

                            //den durchgang nochmal neu machen, solange bis kein schnellerer weg mehr gefunden wird
                            
                            //rollback bei allen in diesem durchlauf besuchten
                            for (Crossing cro : newOpenList) {
                                cro.setVisited(false);
                            }
                            //der aktualisierte, bleibt aber besucht
                            link.getTargetCrossing().setVisited(true);
                            newOpenList = openList;
                            openList = null;
                            break;
                        }
                    }
                }
                if (openList == null) {
                    break;
                }
            }
            openList = newOpenList;
        }

        return resultList;
    }
}
