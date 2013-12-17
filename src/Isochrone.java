
import fu.geo.LatLongPosition;
import fu.util.ConcaveHullGenerator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Isochrone {

    private Database dataBase;
    Map<Long, Crossing> crossings;
    Map<Long, Link> links;

    public Isochrone(String dbhost, int dbport, String dbuser,
            String dbpasswd, String dbname) throws Exception {

        this.dataBase = new Database(dbhost, dbport, dbuser, dbpasswd, dbname);
        LSISpeed.init();
    }

    /**
     * Holt die Daten von der Datenbank und speichert sie in den Maps
     *
     * @param areaBox die Daten werden nur in diesem Bereich geholt
     */
    private void fetchData(AreaBox areaBox) {
        long startTime;
        System.out.println("Daten holen ...");
        System.out.println("Kreuzungen holen ...");
        startTime = System.nanoTime();
        this.crossings = this.dataBase.getCrossings(areaBox);
        System.out.println("done! elapsed time (milliseconds):" + (System.nanoTime() - startTime) / 1000 / 1000);

        System.out.println("Verbindungen holen ...");
        startTime = System.nanoTime();
        links = this.dataBase.getLinks(areaBox);
        System.out.println("done! elapsed time (milliseconds):" + (System.nanoTime() - startTime) / 1000 / 1000);
    }

    private void prepareData() {
        // Kreuzungen und Verbindungen verknüpfen
        for (Link l : links.values()) {
            l.setCrossingReferences(crossings);
        }
    }

    /**
     * berechnet die Isochrone
     *
     * @param latitude Latitude des Startpunktes
     * @param longitude Longtitude des Startpunktes
     * @param minutes die verfügbaren Minuten, für die der Umkreis berechnet
     * werden soll
     * @return alle erreichbaren Positionen
     * @throws Exception
     */
    public ArrayList<double[]> computeIsochrone(
            double latitude,
            double longitude,
            int minutes) throws Exception {
        long startTime;

        this.dataBase.openConnection();

        LatLongPosition startPosition = new LatLongPosition(latitude, longitude);
        AreaBox areaBox = getAreaBox(startPosition, minutes, LSISpeed.getMaxSpeed());

        // Daten aus der Datenbank holen
        this.fetchData(areaBox);
        // Kreuzungen und Verbindungen verknüpfen, vereinfacht den Algorithmus
        this.prepareData();

        Crossing startCrossing = this.findNearestCrossing(startPosition);

        IsochroneAlgo algo = new TreeSetDijkstraAlgo();

        startTime = System.nanoTime();
        System.out.println("berechnen ...");
        Set<Crossing> result = algo.calculate(crossings.values(), links.values(), startCrossing, minutes * 60);
        System.out.println("done! elapsed time (milliseconds):" + (System.nanoTime() - startTime) / 1000 / 1000);

        startTime = System.nanoTime();
        System.out.println("Isochrone berechnen ...");
        ArrayList<double[]> isochronePoints = this.generateIsochrone(result);
        System.out.println("done! elapsed time (milliseconds):" + (System.nanoTime() - startTime) / 1000 / 1000);

        return isochronePoints;
    }

    public void exit() {
        this.dataBase.closeConnection();
    }

    /**
     * Berechnet die Eckpunkte eines Rechtecks für eine Fläche um einen Punkt
     *
     * @param position
     * @param time	Zeit in Minuten
     * @param speed	Geschwindigkeit in km/h
     * @return rechteckige Box mit der position als Zentrum
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
     * findet die nächste Kreuzung zur angegebenen Position
     *
     * @param position
     * @return	die nächste Kreuzung zur angegebenen Position
     */
    private Crossing findNearestCrossing(LatLongPosition position) {
        Set<Long> keys = this.crossings.keySet();
        Iterator<Long> iter = keys.iterator();
        double dist = Double.POSITIVE_INFINITY;
        long shortest = -1;

        while (iter.hasNext()) {
            long key = iter.next();

            // quadratischer euklidischer Abstand, ist für kleine Entfernungen genau genug
            double dlat = position.getLatitude() - this.crossings.get(key).getPosition().getLatitude();
            double dlong = position.getLongitude() - this.crossings.get(key).getPosition().getLongitude();
            double newDist = dlat * dlat + dlong * dlong;

            if (newDist < dist) {
                dist = newDist;
                shortest = key;
            }
        }
        return this.crossings.get(shortest);
    }

    /**
     * erzeugt die Punkte für die Isochrone
     *
     * @param crossings die erreichbaren Kreuzungen
     * @return die Punkte auf der Isochronen
     */
    private ArrayList<double[]> generateIsochrone(Set<Crossing> crossings) {
        ArrayList<double[]> pointsList = new ArrayList<double[]>();
        for (Crossing c : crossings) {
            LatLongPosition pos = c.getPosition();
            pointsList.add(new double[]{pos.getLatitude(), pos.getLongitude()});
        }

        return ConcaveHullGenerator.concaveHull(pointsList, 0.005);
    }
}
