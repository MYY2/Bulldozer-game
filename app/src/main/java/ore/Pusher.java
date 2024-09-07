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

public class Pusher extends Machines {

    private int moves = 0;
    private int autoMovementIndex = 0;

    public Pusher() {
        super(true, "sprites/pusher.png");
    }


    /**
     * Method to move pusher automatically based on the instructions input from properties file
     */
    public void autoMoveNext() {
        List<String> controls = super.getControls();
        if (controls != null && autoMovementIndex < controls.size()) {
            String[] currentMove = controls.get(autoMovementIndex).split("-");
            String machine = currentMove[0];
            String move = currentMove[1];
            autoMovementIndex++;
            if (machine.equals("P")) {
                if (OreSim.isFinished())
                    return;
                Location next = null;
                next=direction_method(move,next);
                Target curTarget = (Target) gameGrid.getOneActorAt(getLocation(), Target.class);
                if (curTarget != null) {
                    curTarget.show();
                }
                if (next != null && canMove(next) == 1) {
                    setLocation(next);
                    moves++;
                }
                gameGrid.refresh();
            }
        }
    }
    /**
     * The helper method to help decide the canMove method in pusher
     * @param c rock clay bulldozer excavator pusher ore
     * @return an integer that indicates there is an ore or not
     */
    protected int canDo(Color c,Rock rock,Clay clay, Bulldozer bulldozer, Excavator excavator,Pusher pusher,Ore ore){

        if (c.equals(borderColor) || rock != null || clay != null || bulldozer != null || excavator != null || pusher!=null)
            return 0;
        else // Test if there is an ore
        {
            if (ore != null)
            {
                // Try to move the ore
                ore.setDirection(OreSim.getPusher().get(OreSim.getPusherIndex()).getDirection());
                if (moveOre(ore))
                    return 1;
                else
                    return 0;

            }
        }
        return 1;
    }

    /**
     * check the ore can be moved ot not
     * @param ore
     * @return ture if the ore can be moved，false if the ore can't be moved
     */
    private boolean moveOre(Ore ore)
    {
        Location next = ore.getNextMoveLocation();
        // Test if try to move into border or other machines
        Color c = gameGrid.getBg().getColor(next);
        Rock rock = (Rock)gameGrid.getOneActorAt(next, Rock.class);
        Clay clay = (Clay)gameGrid.getOneActorAt(next, Clay.class);
        Bulldozer bulldozer = (Bulldozer)gameGrid.getOneActorAt(next,Bulldozer.class);
        Excavator excavator = (Excavator)gameGrid.getOneActorAt(next,Excavator.class);
        Pusher pusher = (Pusher)gameGrid.getOneActorAt(next, Pusher.class);

        if (c.equals(borderColor) || rock != null || clay != null || bulldozer!=null||excavator!=null||pusher!=null)
            return false;

        // Test if there is another ore
        Ore neighbourOre =
                (Ore)gameGrid.getOneActorAt(next, Ore.class);
        if (neighbourOre != null)
            return false;

        // Reset the target if the ore is moved out of target
        Location currentLocation = ore.getLocation();
        List<Actor> actors = gameGrid.getActorsAt(currentLocation);
        if (actors != null) {
            for (Actor actor : actors) {
                if (actor instanceof Target) {
                    Target currentTarget = (Target) actor;
                    currentTarget.show();
                    ore.show(0);
                }
            }
        }

        // Move the ore
        ore.setLocation(next);

        // Check if we are at a target
        Target nextTarget = (Target) gameGrid.getOneActorAt(next, Target.class);
        if (nextTarget != null) {
            ore.show(1);
            nextTarget.hide();
        } else {
            ore.show(0);
        }

        return true;
    }

    public int getMoves() {
        return moves;
    }
}

