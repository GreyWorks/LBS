
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class DorendaOutput {

    File file;
    FileWriter writer;

    public DorendaOutput(String filename) throws IOException {
        this.file = new File(filename);
        this.writer = new FileWriter(this.file);
        this.writer.write("POLYGON col=0,255,0,100\n");
    }

    /**
     * schreibt die gegebenen Punkte im Dorenda-Format in die Datei
     *
     * @param points
     * @throws IOException
     */
    public void write(ArrayList<double[]> points) throws IOException {
        for (double[] point : points) {
            writer.write(String.format(Locale.ENGLISH, "%f,%f\n", point[1], point[0]));
        }
    }

    public void close() throws IOException {
        this.writer.close();
    }
}
