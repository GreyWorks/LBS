
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private static final String filename = "isochrone.txt";

    public static void main(String[] args) throws IOException {
        int dbport = 5432;
        String dbuser = "dbuser";
        String dbpasswd = "dbuser";
        ArrayList<double[]> isochronePositions= null;
        Isochrone iso = null;
        
        if (args.length < 5) {
            System.out.println("zu wenig Parameter");
            System.exit(1);
        }

        try {
        iso = new Isochrone(args[0], dbport, dbuser, dbpasswd, args[1]);
        
        isochronePositions = iso.computeIsochrone(Double.valueOf(args[2]),
                Double.valueOf(args[3]), 
                Integer.valueOf(args[4]));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        try {
            DorendaOutput out = new DorendaOutput(filename);
            out.write(isochronePositions);
            out.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }     

        iso.exit();
    }
}
