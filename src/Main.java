
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws IOException {
        int dbport = 5432;
        String dbuser = "dbuser";
        String dbpasswd = "dbuser";
        ArrayList<double[]> isochronePositions= null;
        Isochrone iso = null;

        try {
        iso = new Isochrone(args[0], dbport, dbuser, dbpasswd, args[1]);
        
        isochronePositions = iso.computeIsochrone(Double.valueOf(args[2]),
                Double.valueOf(args[3]), 
                Integer.valueOf(args[4]));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        File testOutput = new File("/tmp/paint.txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(testOutput);
            writer.write("POLYGON col=0,255,0,100\n");
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (double[] point : isochronePositions) {
            try {
                writer.write(String.format("%f,%f\n", point[1], point[0]));
            } catch (IOException ex) {
                System.out.println("write geht nicht");
            }
        }
        writer.close();


        iso.exit();
    }
}
