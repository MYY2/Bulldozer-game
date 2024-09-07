/**2024s1-project1-mon-17-15-team-03
 *
 */
package ore;

import ch.aplu.jgamegrid.Location;

import java.awt.*;
import java.util.List;

public class Bulldozer extends Machines {

    private int moves;
    private int removed;
    private int autoMovementIndex;

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

    public Bulldozer() {
        super(true, "sprites/bulldozer.png");  // Rotatable
    }


    /**
     * Method to move bulldozer automatically based on the instructions input from properties file
     */
    public void autoMoveNext() {
        List<String> controls = super.getControls();
        if (controls != null && autoMovementIndex < controls.size()) {
            String[] currentMove = controls.get(autoMovementIndex).split("-");
            String machine = currentMove[0];
            String move = currentMove[1];
            autoMovementIndex++;
            if (machine.equals("B")) {
                if (OreSim.isFinished())
                    return;
                Location next = null;
                next = direction_method(move,next);
                //if the next is a clay, add the moves and number of clay removed
                if (next != null && canMove(next) == 1) {
                    setLocation(next);
                    moves++;
                    removed += 1;
                }
                //if the next is an empty place, add the moves
                if (next != null && canMove(next) == 2) {
                    setLocation(next);
                    moves++;
                }
                gameGrid.refresh();
            }
        }
    }
    /**
     * The helper method to help decide the canMove method in bulldozer
     * @param c rock clay bulldozer excavator pusher ore
     * @return an integer that indicates there is an clay or not
     */
    protected int canDo(Color c, Rock rock, Clay clay, Bulldozer bulldozer, Excavator excavator, Pusher pusher, Ore ore){
        if (c.equals(borderColor) || rock != null ||bulldozer != null || excavator != null || ore !=null || pusher!=null)
            return 0;
        else // Test if there is a clay
        {
            if (clay != null)
            {
                clay.removeSelf();
                return 1;
            }
        }

        return 2;
    }
}
