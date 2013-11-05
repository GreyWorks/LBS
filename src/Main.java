
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
		LatLongPosition[] boundingBox = getAreaBox(new LatLongPosition(49.458167, 11.10222), 5, 20);
		for(LatLongPosition i:boundingBox) {
			System.out.println("lat: " + i.getLatitude() + " long: " + i.getLongitude());
		}
        
        String dbhost="geo.informatik.fh-nuernberg.de";
        int dbport=5432;
        String dbuser="dbuser";
        String dbpasswd="dbuser";
        String dbname="deproDB13";

        Database db = new Database(dbhost, dbport, dbuser, dbpasswd, dbname);
        db.openConnection();
        
        Map<Integer, Crossing> crossings = db.getCrossings(boundingBox[0], boundingBox[1]);
        
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
	 * @param speed	speed in kmh
	 * @return [0]=northWest [1]=southEast
	 */
	public static LatLongPosition[] getAreaBox(LatLongPosition position, int time, int speed) {
		double meters = (speed * 1000.0) * (time / 60.0);
		LatLongPosition[] positions = new LatLongPosition[2];
        
        double lat = position.getLatitude();
        double lon = position.getLongitude();
        
		positions[0] = new LatLongPosition(fu.geo.Spherical.latitudeNorthOf(lat, lon, meters), 
                fu.geo.Spherical.longitudeEastOf(lat, lon, -meters));
		positions[1] = new LatLongPosition(fu.geo.Spherical.latitudeNorthOf(lat, lon, -meters),
                fu.geo.Spherical.longitudeEastOf(lat, lon, meters));
		
		return positions;
	}
}
