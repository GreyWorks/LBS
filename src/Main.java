
import fu.geo.LatLongPosition;
import java.util.Map;
/*import java.io.File;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.util.logging.Level;
 import java.util.logging.Logger;*/

public class Main {

    public static void main(String[] args) {


        // DEBUG test generating start box
        AreaBox areaBox = getAreaBox(new LatLongPosition(49.458167, 11.10222), 5, 20);
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


        /*File testOutput = new File("/tmp/paint.txt");
         FileWriter writer = null;
         try {
         writer = new FileWriter(testOutput);
         writer.write("POSITIONS rad=2 col=0,255,0,255\n");
         } catch (IOException ex) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         }
        
        
         for (Crossing crossing :crossings.values()){
         try {
         writer.write(String.format("%f,%f\n", crossing.getPosition().getLongitude(), crossing.getPosition().getLatitude()));
         } catch (IOException ex) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         }
         }*/



        db.closeConnection();


    }

    /**
     * Calculates the bounding edge points for an area around a point
     *
     * @param position
     * @param time	time in minutes
     * @param speed	speed in km/h
     * @return [0]=northWest [1]=southEast
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
}
