/**
 * 2024s1-project1-mon-17-15-team-03
 * Jieyang Zhu
 * Yueyue Ma
 * RenJieYeo
 */
package ore;


import ch.aplu.jgamegrid.Location;

import java.awt.*;
import java.util.List;


public class Excavator extends Machines {
    private int moves;
    private int removed;
    private int autoMovementIndex = 0;
    /**
     * To get the machine moved steps
     * @return machine moved steps
     */
    public int getMoves() {
        return moves;
    }
    /**
     * To get the obstacle removed number
     * @return remove obstacle's number
     */
    public int getRemoved() {
        return removed;
    }

    //private List<String> controls = null;


    public Excavator() {
        super(true, "sprites/excavator.png");
    }

    /**
     * Method to move excavator automatically based on the instructions input from properties file
     */
    public void autoMoveNext() {
        List<String> controls = super.getControls();
        if (controls != null && autoMovementIndex < controls.size()) {
            String[] currentMove = controls.get(autoMovementIndex).split("-");
            String machine = currentMove[0];
            String move = currentMove[1];
            autoMovementIndex++;
            if (machine.equals("E")) {
                if (OreSim.isFinished())
                    return;
                Location next = null;
                next = direction_method(move,next);
                //if the next is a rock, add the moves and number of rock removed
                if (next != null && canMove(next) == 1) {
                    removed += 1;
                    moves++;
                    setLocation(next);
                }
                //if the next is an empty place, add the moves
                if (next != null && canMove(next) == 2) {
                    moves++;
                    setLocation(next);
                }
                gameGrid.refresh();
            }
        }
    }

    /**
     * The helper method to help decide the canMove method in excavator
     * @param c rock clay bulldozer excavator pusher ore
     * @return an integer that indicates there is a rock or not
     */
    @Override
    protected int canDo(Color c, Rock rock, Clay clay, Bulldozer bulldozer, Excavator excavator, Pusher pusher, Ore ore) {
        if (c.equals(borderColor) || clay != null ||bulldozer != null || excavator != null || ore !=null||pusher!=null)
            return 0;
        else // Test if there is a rock
        {
            if (rock != null)
            {
                rock.removeSelf();
                return 1;
            }
        }

        return 2;
    }
}

