
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Algo {

    public static List<Crossing> calculate(Collection<Crossing> crossings, Collection<Link> links, Crossing startCrossing, double time) {
        ArrayList<Crossing> resultList = new ArrayList<Crossing>();
        HashSet<Crossing> newOpenList;
        HashSet<Crossing> openList = new HashSet<Crossing>();

        startCrossing.setRemainingTime(time);
        startCrossing.setVisited(true);

        openList.add(startCrossing);
        while (!openList.isEmpty()) {
            newOpenList = new HashSet<Crossing>();
            for (Crossing openCrossing : openList) {
                System.out.println(openCrossing.getRemainingTime());
                for (Link link : openCrossing.outgoingLinks) {
                    if (!link.getTargetCrossing().wasVisited()) {
                        if (openCrossing.getRemainingTime() - link.getTime() < 0) {
                            resultList.add(openCrossing);
                        } else {
                            link.getTargetCrossing().setRemainingTime(openCrossing.getRemainingTime() - link.getTime());
                            link.getTargetCrossing().setVisited(true);
                            newOpenList.add(link.getTargetCrossing());
                            //resultList.add(link.getTargetCrossing());
                        }
                    } else {
                        // if ersetzen mit updateTimeIfGreater //größere Restzeit setzen
                        if (link.getTargetCrossing().getRemainingTime() < openCrossing.getRemainingTime() - link.getTime()) {
                            //update
                            link.getTargetCrossing().setRemainingTime(openCrossing.getRemainingTime() - link.getTime());

                            //den durchgang nochmal neu machen, solange 
                            //newOpenList.clear();
                            for (Crossing cro : newOpenList) {
                                cro.setVisited(false);
                            }
                            newOpenList = openList;
                            openList = null;
                            break;
                        }
                    }
                }
                if (openList == null)
                    break;
            }
            openList = newOpenList;
            //delete newOpenList hier, oder vorher werte kopieren, dann kann immer dieselbe liste verwedet werden
        }

        return resultList;
    }
}
