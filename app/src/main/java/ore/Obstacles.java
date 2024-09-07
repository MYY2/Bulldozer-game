/**
 * 2024s1-project1-mon-17-15-team-03
 * Jieyang Zhu
 * Yueyue Ma
 * RenJieYeo
 */
package ore;

import ch.aplu.jgamegrid.Actor;

public class Obstacles extends Actor {

    //for other Obstacles and targets
    public Obstacles(String s) {
        super(s);
    }

    //for Ores
    public Obstacles(String s,int n) {
        super(s,n);
    }
}
