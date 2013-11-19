
import java.util.Locale;

import fu.geo.LatLongPosition;

public class AreaBox {

    private LatLongPosition posSmallValues;
    private LatLongPosition posLargeValues;

    public AreaBox(LatLongPosition pos1, LatLongPosition pos2) {

        double smallLong;
        double bigLong;
        double smallLat;
        double bigLat;

        if (pos1.getLongitude() < pos2.getLongitude()) {
            smallLong = pos1.getLongitude();
            bigLong = pos2.getLongitude();
        } else {
            bigLong = pos1.getLongitude();
            smallLong = pos2.getLongitude();
        }

        if (pos1.getLatitude() < pos2.getLatitude()) {
            smallLat = pos1.getLatitude();
            bigLat = pos2.getLatitude();
        } else {
            bigLat = pos1.getLatitude();
            smallLat = pos2.getLatitude();
        }

        this.posSmallValues = new LatLongPosition(smallLat, smallLong);
        this.posLargeValues = new LatLongPosition(bigLat, bigLong);
    }

    /**
     * @return the posSmallValues
     */
    public LatLongPosition getPosSmallValues() {
        return posSmallValues;
    }

    /**
     * @return the posLargeValues
     */
    public LatLongPosition getPosLargeValues() {
        return posLargeValues;
    }
    
    public String getSqlBetweenStatement(String long_name, String lat_name) {
        return String.format(Locale.ENGLISH," %s BETWEEN %.16f AND %.16f AND %s BETWEEN %.16f AND %.16f",
                long_name, posSmallValues.getLongitude(), posLargeValues.getLongitude(),
                lat_name, posSmallValues.getLatitude(), posLargeValues.getLatitude());
    }
}
