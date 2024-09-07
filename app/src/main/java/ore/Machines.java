/**
 * 2024s1-project1-mon-17-15-team-03
 * Jieyang Zhu
 * Yueyue Ma
 * RenJieYeo
 */
package ore;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

import java.awt.*;

import java.util.List;


public abstract class Machines extends Actor {
    public final Color borderColor = new Color(100, 100, 100);
    private List<String> controls = null;

    public Machines(boolean b, String s) {
        super(b, s);
    }

    public void setUpMachine(boolean isAutoMode, List<String> controls) {
        this.controls = controls;
    }

    /**
     * Method to move machine automatically based on the instructions input from properties file
     */
    public abstract void autoMoveNext();

    /**
     * The method to decide the machine can move to next position or not
     * @param location
     * @return an integer that indicates there is an obstacle or not
     */
    public int canMove(Location location) {
        Color c = gameGrid.getBg().getColor(location);
        Rock rock = (Rock) gameGrid.getOneActorAt(location, Rock.class);
        Clay clay = (Clay) gameGrid.getOneActorAt(location, Clay.class);
        Bulldozer bulldozer = (Bulldozer) gameGrid.getOneActorAt(location, Bulldozer.class);
        Excavator excavator = (Excavator) gameGrid.getOneActorAt(location, Excavator.class);
        Pusher pusher = (Pusher) gameGrid.getOneActorAt(location, Pusher.class);
        Ore ore = (Ore) gameGrid.getOneActorAt(location, Ore.class);
        return canDo(c, rock, clay, bulldozer, excavator, pusher, ore);
    }

    /**
     * The helper method to help decide the canMove method in different machine
     * @param c rock clay bulldozer excavator pusher ore
     * @return an integer that indicates there is an obstacle or not
     */
    protected abstract int canDo(Color c, Rock rock, Clay clay, Bulldozer bulldozer, Excavator excavator, Pusher pusher, Ore ore);


    /**
     * To get the machine moved steps
     * @return machine moved steps
     */
    public abstract int getMoves();

    /**
     * To remove the obstacle
     * @return remove obstacle's number
     */
    public int getRemoved() {
        return 0;
    }


    /**
     * get the next location for moving and set machine's direction
     * @param move next
     * @return the location of the next position
     */
    public Location direction_method(String move,Location next){
        switch (move) {
            case "L":
                next = getLocation().getNeighbourLocation(Location.WEST);
                setDirection(Location.WEST);
                break;
            case "U":
                next = getLocation().getNeighbourLocation(Location.NORTH);
                setDirection(Location.NORTH);
                break;
            case "R":
                next = getLocation().getNeighbourLocation(Location.EAST);
                setDirection(Location.EAST);
                break;
            case "D":
                next = getLocation().getNeighbourLocation(Location.SOUTH);
                setDirection(Location.SOUTH);
                break;
        }
        return next;
    }

    public List<String> getControls() {
        return controls;
    }
}
