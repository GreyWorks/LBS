
import fu.geo.LatLongPosition;
import fu.util.ConcaveHullGenerator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Isochrone {

    private Database dataBase;

    public Isochrone(String dbhost, int dbport, String dbuser,
            String dbpasswd, String dbname) throws Exception {

        this.dataBase = new Database(dbhost, dbport, dbuser, dbpasswd, dbname);
        LSISpeed.init();
    }

    public ArrayList<double[]> computeIsochrone(
            double latitude,
            double longitude,
            int minutes) throws Exception {
        long startTime;

        this.dataBase.openConnection(); 

        LatLongPosition startPosition = new LatLongPosition(latitude, longitude);
        AreaBox areaBox = getAreaBox(startPosition, minutes, LSISpeed.getMaxSpeed());

        System.out.println("Daten holen ...");
        System.out.println("Kreuzungen holen ...");
        startTime = System.nanoTime();
        Map<Long, Crossing> crossings = this.dataBase.getCrossings(areaBox);
        System.out.println("done! elapsed time (milliseconds):" + (System.nanoTime() - startTime) / 1000 / 1000);

        System.out.println("Verbindungen holen ...");
        startTime = System.nanoTime();
        Map<Long, Link> links = this.dataBase.getLinks(areaBox);
        System.out.println("done! elapsed time (milliseconds):" + (System.nanoTime() - startTime) / 1000 / 1000);

        for (Link l : links.values()) {
            l.setCrossingReferences(crossings);
        }

        Crossing startCrossing = this.findStartCrossing(startPosition, crossings);

        ReachableAlgo algo = new DijkstraAlgo();


        System.out.println("berechnen ...");
        Set<Crossing> result = algo.calculate(crossings.values(), links.values(), startCrossing, minutes * 60);
        System.out.println("done! elapsed time (milliseconds):" + (System.nanoTime() - startTime) / 1000 / 1000);

        ArrayList<double[]> isochronePositions = this.generateIsochrone(result);

        return new ArrayList<double[]>(isochronePositions);
    }

    public void exit() {
        this.dataBase.closeConnection();
    }

    /**
     * Calculates the bounding edge points for an area around a point
     *
     * @param position
     * @param time	time in minutes
     * @param speed	speed in km/h
     * @return rectangular areabox with the position as center
     */
    private AreaBox getAreaBox(LatLongPosition position, int time, int speed) {
        double meters = (speed * 1000.0) * (time / 60.0);

        double lat = position.getLatitude();
        double lon = position.getLongitude();

        LatLongPosition pos1 = new LatLongPosition(fu.geo.Spherical.latitudeNorthOf(lat, lon, meters),
                fu.geo.Spherical.longitudeEastOf(lat, lon, -meters));
        LatLongPosition pos2 = new LatLongPosition(fu.geo.Spherical.latitudeNorthOf(lat, lon, -meters),
                fu.geo.Spherical.longitudeEastOf(lat, lon, meters));

        return new AreaBox(pos1, pos2);
    }

    /**
     * Finds nearest crossing id for a given position
     *
     * @param position	start position for the search
     * @param crossings	ArrayList of the crossings to search through
     * @return	the crossing which is nearest to given position
     */
    private static Crossing findStartCrossing(LatLongPosition position, Map<Long, Crossing> crossings) {
        Set<Long> keys = crossings.keySet();
        Iterator<Long> iter = keys.iterator();
        double dist = Double.POSITIVE_INFINITY;
        long shortest = -1;

        while (iter.hasNext()) {
            long key = iter.next();

            // Squared Euclidean distance, suits for small distances
            double dlat = position.getLatitude() - crossings.get(key).getPosition().getLatitude();
            double dlong = position.getLongitude() - crossings.get(key).getPosition().getLongitude();
            double newDist = dlat * dlat + dlong * dlong;

            if (newDist < dist) {
                dist = newDist;
                shortest = key;
            }
        }
        return crossings.get(shortest);
    }

    /**
     * generates the isochrone
     *
     * @param the crossings inside the isochrone
     * @return the isochrone line
     */
    private static ArrayList<double[]> generateIsochrone(Set<Crossing> crossings) {
        ArrayList<double[]> pointsList = new ArrayList<double[]>();
        for (Crossing c : crossings) {
            LatLongPosition pos = c.getPosition();
            pointsList.add(new double[]{pos.getLatitude(), pos.getLongitude()});
        }

        return ConcaveHullGenerator.concaveHull(pointsList, 0.005);
    }
}
