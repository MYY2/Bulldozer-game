/**
 * 2024s1-project1-mon-17-15-team-03
 * Jieyang Zhu
 * Yueyue Ma
 * RenJieYeo
 */
package ore;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGKeyListener;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;



public class Keyboard extends ControllingMachines implements GGKeyListener {


    private final Color borderColor = new Color(100, 100, 100);
    public boolean keyPressed(KeyEvent evt)
    {
        if (OreSim.isFinished())
            return true;

        Location next = null;
        switch (evt.getKeyCode())
        {
            case KeyEvent.VK_LEFT:
                next = OreSim.getPusher().get(OreSim.getPusherIndex()).getLocation().getNeighbourLocation(Location.WEST);
                OreSim.getPusher().get(OreSim.getPusherIndex()).setDirection(Location.WEST);
                break;
            case KeyEvent.VK_UP:
                next = OreSim.getPusher().get(OreSim.getPusherIndex()).getLocation().getNeighbourLocation(Location.NORTH);
                OreSim.getPusher().get(OreSim.getPusherIndex()).setDirection(Location.NORTH);
                break;
            case KeyEvent.VK_RIGHT:
                next = OreSim.getPusher().get(OreSim.getPusherIndex()).getLocation().getNeighbourLocation(Location.EAST);
                OreSim.getPusher().get(OreSim.getPusherIndex()).setDirection(Location.EAST);
                break;
            case KeyEvent.VK_DOWN:
                next = OreSim.getPusher().get(OreSim.getPusherIndex()).getLocation().getNeighbourLocation(Location.SOUTH);
                OreSim.getPusher().get(OreSim.getPusherIndex()).setDirection(Location.SOUTH);
                break;
        }

        Target curTarget = (Target) gameGrid.getOneActorAt(getLocation(), Target.class);
        if (curTarget != null){
            curTarget.show();
        }


        if (next != null && canMove(next))
        {
            OreSim.getPusher().get(OreSim.getPusherIndex()).setLocation(next);
            updateLogResult();
        }
        gameGrid.refresh();
        return true;
    }


    private boolean canMove(Location location)
    {
        // Test if try to move into border, rock or clay
        Color c = gameGrid.getBg().getColor(location);
        Rock rock = (Rock)gameGrid.getOneActorAt(location, Rock.class);
        Clay clay = (Clay)gameGrid.getOneActorAt(location, Clay.class);
        Bulldozer bulldozer = (Bulldozer)gameGrid.getOneActorAt(location,Bulldozer.class);
        Excavator excavator = (Excavator)gameGrid.getOneActorAt(location,Excavator.class);
        if (c.equals(borderColor) || rock != null || clay != null || bulldozer != null || excavator != null)
            return false;
        else // Test if there is an ore
        {
            Ore ore = (Ore)gameGrid.getOneActorAt(location, Ore.class);
            if (ore != null)
            {

                // Try to move the ore
                ore.setDirection(OreSim.getPusher().get(OreSim.getPusherIndex()).getDirection());
                if (moveOre(ore))
                    return true;
                else
                    return false;

            }
        }

        return true;
    }



    private boolean moveOre(Ore ore)
    {
        Location next = ore.getNextMoveLocation();
        // Test if try to move into border
        Color c = gameGrid.getBg().getColor(next);;
        Rock rock = (Rock)gameGrid.getOneActorAt(next, Rock.class);
        Clay clay = (Clay)gameGrid.getOneActorAt(next, Clay.class);


        if (c.equals(borderColor) || rock != null || clay != null)
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


    public boolean keyReleased(KeyEvent evt)
    {
        return true;
    }


    private void updateLogResult() {
    }


}
