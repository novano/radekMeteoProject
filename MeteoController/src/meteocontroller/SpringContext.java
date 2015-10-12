package meteocontroller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import meteocontroller.photo.uploader.ThreadMonitor;

/**
 * Trida pro udrzeni instanci singletonu
 *
 * @author Jan Novak <novano@mail.muni.cz>
 */
public class SpringContext {

    private SpringContext() {
    }

    private static String propsFile = "settings.properties";

    private static Map<String, String> props = new HashMap<>();

    private static ThreadMonitor monitor = null;

    public static SpringContext getInstance() {
        MeteoLogger.log("Context init.");

        SpringContext springContext = new SpringContext();

        try (BufferedReader br = new BufferedReader(new FileReader(propsFile))) {
            String line = br.readLine();
            while (line != null) {
                if (line.isEmpty() || line.startsWith("//")) {
                    line = br.readLine();
                    continue;
                }

                String[] split = line.split("=");
                if (split.length != 2) {
                    Logger.getLogger(SpringContext.class.getName()).log(Level.SEVERE, null, "bacha");
                }
                springContext.props.put(split[0], split[1]);

                line = br.readLine();
            }
        } catch (IOException ex) {
            MeteoLogger.log(ex);
            MeteoLogger.log("Context init failed.");
        }

        monitor = new ThreadMonitor();
        new Thread(monitor).start();

        MeteoLogger.log("Context init successfull.");
        return springContext;
    }

    public String getProperty(String key) {
        return props.get(key);
    }

    public static ThreadMonitor getMonitor() {
        return monitor;
    }

}
