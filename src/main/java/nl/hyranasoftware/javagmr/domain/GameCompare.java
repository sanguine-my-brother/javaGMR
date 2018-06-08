package nl.hyranasoftware.javagmr.domain;

import java.util.Comparator;

/**
 *
 * @author padoura
 */
public class GameCompare implements Comparator<Game>{

    /**
     * Used as a Game specific comparator with the following order of precedence:
     * 1) Games that expire and have the earliest expiration date
     * 2) Games that expire and have the latest expiration date
     * 3) Games that do not expire and have the earliest starting date
     * 4) Games that do not expire and have the latest starting date
     */
    @Override
    public int compare(Game g1, Game g2) {
        if (g1.getCurrentTurn().getExpires() == null) {
            if (g2.getCurrentTurn().getExpires() == null)
                return g1.getCurrentTurn().getStarted().toDate().compareTo(g2.getCurrentTurn().getStarted().toDate());
            else
                return 1;
        }else{
            if (g2.getCurrentTurn().getExpires() == null)
                return -1;
            else
                return g1.getCurrentTurn().getExpires().toDate().compareTo(g2.getCurrentTurn().getExpires().toDate());
        }
    }
}
