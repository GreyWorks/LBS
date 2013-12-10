
import fu.geo.LatLongPosition;
import fu.util.ConcaveHullGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws IOException {


        // DEBUG test generating start box
        LatLongPosition startPosition = new LatLongPosition(49.481689, 11.115951);
        AreaBox areaBox = getAreaBox(startPosition, 10, 50);
        LatLongPosition small = areaBox.getPosSmallValues();
        LatLongPosition large = areaBox.getPosLargeValues();
        System.out.println("lat: " + small.getLatitude() + " long: " + small.getLongitude());
        System.out.println("lat: " + large.getLatitude() + " long: " + large.getLongitude());

        LSISpeed.init();

        String dbhost = "geo.informatik.fh-nuernberg.de";
        int dbport = 5432;
        String dbuser = "dbuser";
        String dbpasswd = "dbuser";
        String dbname = "deproDB13";

        Database db = new Database(dbhost, dbport, dbuser, dbpasswd, dbname);
        db.openConnection();

        Map<Long, Crossing> crossings = db.getCrossings(areaBox);
        Map<Long, Link> links = db.getLinks(areaBox);

        System.out.println("vorher");
        for (Link l : links.values()) {
            l.setCrossingReferences(crossings);
        }
        System.out.println("nachher");
        Crossing startCrossing = Main.findStartCrossing(startPosition, crossings);

        ReachableAlgo algo = new DijkstraAlgo();
        long startTime = System.nanoTime();
        System.out.println("startalgo");
        Set<Crossing> result = algo.calculate(crossings.values(), links.values(), startCrossing, 400);
        System.out.println("stopalgo, elapsed time (mikroseconds):" + (System.nanoTime() - startTime) / 1000);

        ArrayList<double[]> isochronePositions = Main.generateIsochrone(result);

        File testOutput = new File("/tmp/paint.txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(testOutput);
            writer.write("POLYGON col=0,255,0,100\n");
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*for (Crossing crossing : crossings.values()) {
         try {
         writer.write(String.format("%f,%f\n", crossing.getPosition().getLongitude(), crossing.getPosition().getLatitude()));
         } catch (IOException ex) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         }
         }*/
        for (double[] point : isochronePositions) {
            try {
                writer.write(String.format("%f,%f\n", point[1], point[0]));
            } catch(IOException ex) {
                System.out.println("write geht nicht");
            }
        }
        writer.close();

        File testOutput2 = new File("/tmp/paint_result.txt");
        FileWriter writer2 = null;
        try {
            writer2 = new FileWriter(testOutput2);
            writer2.write("POSITIONS rad=5 col=0,255,0,255\n");
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Crossing crossing : result) {
            try {
                writer2.write(String.format("%f,%f\n", crossing.getPosition().getLongitude(), crossing.getPosition().getLatitude()));
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        writer2.close();


        db.closeConnection();

    }

    /**
     * Calculates the bounding edge points for an area around a point
     *
     * @param position
     * @param time	time in minutes
     * @param speed	speed in km/h
     * @return rectangular areabox with the position as center
     */
    public static AreaBox getAreaBox(LatLongPosition position, int time, int speed) {
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
     * @param crossings	ArrayList of all crossings
     * @return	crossing
     */
    public static Crossing findStartCrossing(LatLongPosition position, Map<Long, Crossing> crossings) {
        Set<Long> keys = crossings.keySet();
        Iterator<Long> iter = keys.iterator();
        double dist = Double.POSITIVE_INFINITY;
        long shortest = -1;

        while (iter.hasNext()) {
            long key = iter.next();

            // Squared Euclidean distance
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
    public static ArrayList<double[]> generateIsochrone(Set<Crossing> crossings) {
        ArrayList<double[]> pointsList = new ArrayList<double[]>();
        for (Crossing c : crossings) {
            LatLongPosition pos = c.getPosition();
            pointsList.add(new double[]{pos.getLatitude(), pos.getLongitude()});
        }

        return ConcaveHullGenerator.concaveHull(pointsList, 100);
    }
}
