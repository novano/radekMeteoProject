/*
 * ===========================================================================
 *  IBA CZ Confidential
 *
 *  (c) Copyright IBA CZ 2012 ALL RIGHTS RESERVED
 *  The source code for this program is not published or otherwise
 *  divested of its trade secrets.
 *
 * ===========================================================================
 */
package meteocontroller;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan Novak <jan.novak@ibacz.eu>
 */
public class MeteoLogger {

    private static final Logger LOG = Logger.getLogger(MeteoLogger.class.getName());
    private static PrintWriter fileLog = null;
    private static long lastLogFileOpen = 0;

    public static void log(String msg) {
        LOG.log(Level.INFO, msg);
        try {
            ownLog(msg);
        } catch (IOException ex) {
            MeteoLogger.log(ex);
        }
    }

    public static void log(Throwable ex) {
        LOG.log(Level.WARNING, "", ex);
        try {
            ex.printStackTrace(getOwnLog());
        } catch (IOException ex1) {
            MeteoLogger.log(ex1);
        }
    }

    private static DateFormat dateFormatLogFile = new SimpleDateFormat("dd-MM-yyyy");
    private static DateFormat dateFormatLogEntry = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private static void ownLog(String msg) throws IOException {
        PrintWriter ownLog = getOwnLog();
        ownLog.write(dateFormatLogEntry.format(new Date()) + " > ");
        ownLog.write(msg);
        ownLog.print(System.lineSeparator());
        ownLog.flush();
    }

    private static PrintWriter getOwnLog() throws IOException {
        if (fileLog == null) {
            openLogFile();
        }

        //jeden soubor pro jeden den
        if (new Date(System.currentTimeMillis()).getDate() != new Date(lastLogFileOpen).getDate()) {
            openLogFile();
        }

        return fileLog;
    }

    private static void openLogFile() throws IOException {
        if (fileLog != null) {
            fileLog.close();
        }

        lastLogFileOpen = System.currentTimeMillis();
        fileLog = new PrintWriter(new FileWriter("meteo_log_" + dateFormatLogFile.format(lastLogFileOpen) + ".log"));
    }
}
