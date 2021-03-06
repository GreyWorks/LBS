
import java.util.HashMap;
import java.util.Map;

public class LSISpeed {

    static Map<Integer, Integer> lsiSpeedMap = new HashMap<Integer, Integer>();

    private LSISpeed() {
    }

    /**
     * setzt alle die Geschwindigkeiten für die verschiedenen Straßentypen
     */
    public static void init() {
        lsiSpeedMap.put(new Integer(34110000), new Integer(110)); // autobahn
        lsiSpeedMap.put(new Integer(34120000), new Integer(90)); // kraftfahrstrasse
        lsiSpeedMap.put(new Integer(34130000), new Integer(65)); // landstrasse unspezifiziert
        lsiSpeedMap.put(new Integer(34131000), new Integer(80)); // bundesstrasse
        lsiSpeedMap.put(new Integer(34132000), new Integer(60)); // landstrasse sekundaer
        lsiSpeedMap.put(new Integer(34133000), new Integer(70)); // landstrasse tertiaer
        lsiSpeedMap.put(new Integer(34134000), new Integer(70)); // landstrasse unklassifiziert
        lsiSpeedMap.put(new Integer(34141000), new Integer(40)); // innerortstrasse
        lsiSpeedMap.put(new Integer(34142000), new Integer(10)); // verkehrsberuhigter bereich
        lsiSpeedMap.put(new Integer(34143000), new Integer(20)); // erschliessungsweg
        lsiSpeedMap.put(new Integer(34143100), new Integer(20)); // zufahrt
        lsiSpeedMap.put(new Integer(34143200), new Integer(5)); // parkplatzweg
        lsiSpeedMap.put(new Integer(34150000), new Integer(10)); // feld- und waldweg
        lsiSpeedMap.put(new Integer(34160000), new Integer(10)); // feld- und waldweg (historisch)
        lsiSpeedMap.put(new Integer(34171000), new Integer(60)); // anschlussstelle autobahn
        lsiSpeedMap.put(new Integer(34172000), new Integer(60)); // Anschlussstelle (Kraftfahrstraße)
        lsiSpeedMap.put(new Integer(34173000), new Integer(50)); // Anschlussstelle (Bundesttrasse)
        lsiSpeedMap.put(new Integer(34174000), new Integer(30)); // Anschlussstelle (sekundaere)
        lsiSpeedMap.put(new Integer(34175000), new Integer(30)); // Anschlussstelle (tertiaer)
        lsiSpeedMap.put(new Integer(34176000), new Integer(20)); // kreisverkehr
    }

    /**
     * liefert die Geschwindigkeit anhand der LSIClass-ID
     *
     * @param id
     * @return
     */
    public static int getSpeedById(int id) {
        return LSISpeed.lsiSpeedMap.get(new Integer(id));
    }

    /**
     *
     * @return die Geschwindigkeit auf der schnellsten Straße
     */
    public static int getMaxSpeed() {
        int maxSpeed = 0;
        for (Integer speed : lsiSpeedMap.values()) {
            if (speed > maxSpeed) {
                maxSpeed = speed;
            }
        }
        return maxSpeed;
    }
}
