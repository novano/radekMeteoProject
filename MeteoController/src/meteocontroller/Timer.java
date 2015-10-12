package meteocontroller;

import java.util.Date;

/**
 *
 * @author Jan Novak <novano@mail.muni.cz>
 */
public class Timer {

    private Date nextActivation = new Date(0L);
    private long period = 500L;

    public Timer(long period) {
        this.period = period;
    }

    public boolean activate() {
        Date now = new Date();
        if (nextActivation.before(now)) {
            nextActivation = new Date(now.getTime() + period);
            return true;
        }
        return false;
    }

}
