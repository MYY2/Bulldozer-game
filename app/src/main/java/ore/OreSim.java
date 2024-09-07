/**
 * 2024s1-project1-mon-17-15-team-03
 * Jieyang Zhu
 * Yueyue Ma
 * RenJieYeo
 */
package ore;

import ch.aplu.jgamegrid.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Properties;

public class OreSim extends GameGrid implements GGKeyListener
{
  // ------------- Inner classes -------------
  public enum ElementType{
    OUTSIDE("OS"), EMPTY("ET"), BORDER("BD"),
    PUSHER("P"), BULLDOZER("B"), EXCAVATOR("E"), ORE("O"),
    ROCK("R"), CLAY("C"), TARGET("T");
    private final String shortType;

    ElementType(String shortType) {
      this.shortType = shortType;
    }

    public String getShortType() {
      return shortType;
    }

    public static ElementType getElementByShortType(String shortType) {
      ElementType[] types = ElementType.values();
      for (ElementType type: types) {
        if (type.getShortType().equals(shortType)) {
          return type;
        }
      }

      return ElementType.EMPTY;
    }
  }

  // ------------- End of inner classes ------
  //

  private final MapGrid grid;
  private final int nbHorzCells;
  private final int nbVertCells;
  private final Color borderColor = new Color(100, 100, 100);
  private final Ore[] ores;
  private final Target[] targets;
  private final Keyboard keyboard = new Keyboard();

  //Arraylist for pushers
  public static ArrayList<Machines> pusher = new ArrayList<>();
  public static ArrayList<Machines> getPusher() {
    return pusher;
  }

  //Arraylist for bulldozers
  public static ArrayList<Machines> bulldozer = new ArrayList<>();
  private int bulldozerIndex = 0, excavatorIndex = 0;

  //Arraylist for excavators
  public static ArrayList<Machines> excavator = new ArrayList<>();

  private static boolean isFinished = false;

  public static boolean isFinished() {
    return isFinished;
  }

  private static int pusherIndex = 0;

  public static int getPusherIndex() {
    return pusherIndex;
  }


  private Properties properties;
  private boolean isAutoMode;
  private double gameDuration;
  private List<String> controls;
  private int movementIndex;
  private StringBuilder logResult = new StringBuilder();
  public OreSim(Properties properties, MapGrid grid)
  {
    super(grid.getNbHorzCells(), grid.getNbVertCells(), 30, false);
    this.grid = grid;
    nbHorzCells = grid.getNbHorzCells();
    nbVertCells = grid.getNbVertCells();
    this.properties = properties;

    ores = new Ore[grid.getNbOres()];
    targets = new Target[grid.getNbOres()];

    isAutoMode = properties.getProperty("movement.mode").equals("auto");
    gameDuration = Integer.parseInt(properties.getProperty("duration"));
    setSimulationPeriod(Integer.parseInt(properties.getProperty("simulationPeriod")));
    controls = Arrays.asList(properties.getProperty("machines.movements").split(","));
  }

  /**
   * Check the number of ores that are collected
   * @return
   */

  private int checkOresDone() {
    int nbTarget = 0;
    for (int i = 0; i < grid.getNbOres(); i++)
    {
      if (ores[i].getIdVisible() == 1)
        nbTarget++;
    }

    return nbTarget;
  }
  /**
   * The main method to run the game
   * @param isDisplayingUI
   * @return
   */
  public String runApp(boolean isDisplayingUI) {
    isFinished = false;
    GGBackground bg = getBg();
    drawBoard(bg);
    drawActors();
    addKeyListener(this);
    if (isDisplayingUI) {
      show();
    }

    if (isAutoMode) {
        doRun();
    }

    int oresDone = checkOresDone();
    double ONE_SECOND = 1000.0;
    while(oresDone < grid.getNbOres() && gameDuration >= 0) {
      try {
        Thread.sleep(simulationPeriod);
        double minusDuration = (simulationPeriod / ONE_SECOND);
        gameDuration -= minusDuration;
        String title = String.format("# Ores at Target: %d. Time left: %.2f seconds", oresDone, gameDuration);
        setTitle(title);
        if (isAutoMode) {

          //check if there is a pusher
          if(!pusher.isEmpty()){
            //auto move each pusher
            for(int i = 0; i <= pusherIndex;i++){
              pusher.get(pusherIndex).autoMoveNext();
            }
          }
          //check if there is a bulldozer
          if(!bulldozer.isEmpty()){
            //auto move each bulldozer
            for(int i = 0;i <= bulldozerIndex;i++) {
              bulldozer.get(bulldozerIndex).autoMoveNext();
            }
          }
          //check if there is an excavator
          if(!excavator.isEmpty()){
            //auto move each pusher excavator
            for(int i = 0; i <= excavatorIndex; i++) {
              excavator.get(excavatorIndex).autoMoveNext();
            }
          }
          updateLogResult();
        }

        oresDone = checkOresDone();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    doPause();

    if (oresDone == grid.getNbOres()) {
      setTitle("Mission Complete. Well done!");
    } else if (gameDuration < 0) {
      setTitle("Mission Failed. You ran out of time");
    }

    updateStatistics();
    isFinished = true;
    return logResult.toString();
  }

  /**
   * Transform the list of actors to a string of location for a specific kind of actor.
   * @param actors
   * @return
   */
  private String actorLocations(List<Actor> actors) {
    StringBuilder stringBuilder = new StringBuilder();
    boolean hasAddedColon = false;
    boolean hasAddedLastComma = false;
    for (int i = 0; i < actors.size(); i++) {
      Actor actor = actors.get(i);
      if (actor.isVisible()) {
        if (!hasAddedColon) {
          stringBuilder.append(":");
          hasAddedColon = true;
        }
        stringBuilder.append(actor.getX() + "-" + actor.getY());
        stringBuilder.append(",");
        hasAddedLastComma = true;
      }
    }

    if (hasAddedLastComma) {
      stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");
    }

    return stringBuilder.toString();
  }


  /**
   * Students need to modify this method so it can write an actual statistics into the statistics file. It currently
   *  only writes the sample data.
   */
  private void updateStatistics() {
    File statisticsFile = new File("statistics.txt");
    FileWriter fileWriter = null;
    try {
      fileWriter = new FileWriter(statisticsFile);
      //check if there is a pusher
      if(!pusher.isEmpty()){
        //write each pusher statistics
        for(int i = 0;i <= pusherIndex;i++){
          fileWriter.write("Pusher-"+(pusherIndex+1)+" Moves: "+ pusher.get(pusherIndex).getMoves()+"\n");
        }
      }
      //check if there is an excavator
      if(!excavator.isEmpty()){
        //write each excavator statistics
        for(int i = 0;i<=excavatorIndex;i++){
          fileWriter.write("Excavator-"+(excavatorIndex+1)+" Moves: "+ excavator.get(excavatorIndex).getMoves()+"\n");
          fileWriter.write("Excavator-"+(excavatorIndex+1)+" Rock removed: "+excavator.get(excavatorIndex).getRemoved()+"\n");
        }
      }
      //check if there is a bulldozer
      if(!bulldozer.isEmpty()){
        //write each bulldozer statistics
        for(int i = 0;i<=bulldozerIndex;i++){
          fileWriter.write("Bulldozer-"+(bulldozerIndex+1)+" Moves: "+bulldozer.get(bulldozerIndex).getMoves()+"\n");
          fileWriter.write("Bulldozer-"+(bulldozerIndex+1)+" Clay removed: "+bulldozer.get(bulldozerIndex).getRemoved()+"\n");
        }
      }
    } catch (IOException e) {
      System.out.println("Cannot write to file - e: " + e.getLocalizedMessage());
    } finally {
      try {
        fileWriter.close();
      } catch (IOException e) {
        System.out.println("Cannot close file - e: " + e.getLocalizedMessage());
      }
    }
  }

  /**
   * Draw all different actors on the board: pusher, ore, target, rock, clay, bulldozer, excavator
   */
  private void drawActors()
  {
    //reset variables each time draw actors
    excavator.clear();
    bulldozer.clear();
    pusher.clear();
    int pusherIndex = 0;
    int oreIndex = 0;
    int targetIndex = 0;
    int bulldozerIndex = 0;
    int excavatorIndex = 0;

    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        ElementType a = grid.getCell(location);

        if (a == ElementType.PUSHER)
        {
          //draw and setup each pusher and count numbers
          pusher.add(pusherIndex,new Pusher());

          addActor(pusher.get(pusherIndex), location);
          pusher.get(pusherIndex).setUpMachine(isAutoMode, controls);
          pusherIndex++;
        }
        if (a == ElementType.ORE)
        {
          ores[oreIndex] = new Ore();
          addActor(ores[oreIndex], location);
          oreIndex++;
        }
        if (a == ElementType.TARGET)
        {
          targets[targetIndex] = new Target();
          addActor(targets[targetIndex], location);
          targetIndex++;
        }

        if (a == ElementType.ROCK)
        {
          addActor(new Rock(), location);
        }

        if (a == ElementType.CLAY)
        {
          addActor(new Clay(), location);
        }

        if (a == ElementType.BULLDOZER)
        {
          //draw and setup each bulldozer and count numbers
          bulldozer.add(bulldozerIndex, new Bulldozer());
          addActor(bulldozer.get(bulldozerIndex), location);
          bulldozer.get(bulldozerIndex).setUpMachine(isAutoMode, controls);
          bulldozerIndex++;
        }
        if (a == ElementType.EXCAVATOR)
        {
          //draw and setup each excavator and count numbers
          excavator.add(excavatorIndex, new Excavator());

          addActor(excavator.get(excavatorIndex), location);


          excavator.get(excavatorIndex).setUpMachine(isAutoMode, controls);

          excavatorIndex++;
        }
      }
    }
    System.out.println("ores = " + Arrays.asList(ores));
    setPaintOrder(Target.class);
  }

  /**
   * Draw the basic board with outside color and border color
   * @param bg
   */

  private void drawBoard(GGBackground bg)
  {
    bg.clear(new Color(230, 230, 230));
    bg.setPaintColor(Color.darkGray);
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        ElementType a = grid.getCell(location);
        if (a != ElementType.OUTSIDE)
        {
          bg.fillCell(location, Color.lightGray);
        }
        if (a == ElementType.BORDER)  // Border
          bg.fillCell(location, borderColor);
      }
    }
  }

  /**
   * The method is automatically called by the framework when a key is pressed. Based on the pressed key, the pusher
   *  will change the direction and move
   * @param evt
   * @return
   */
  private static int index =0;

  public boolean keyPressed(KeyEvent evt)
  {
    if (isFinished)
      return true;

    Location next = null;
    switch (evt.getKeyCode())
    {
      //use to switch the pusher to be controlled
      case KeyEvent.VK_CAPS_LOCK:
        if(index<pusherIndex){
          index++;
        }else {
          index = 0;
        }
        break;
      //control the selected pusher
      case KeyEvent.VK_LEFT:
        next = pusher.get(index).getLocation().getNeighbourLocation(Location.WEST);
        pusher.get(index).setDirection(Location.WEST);
        break;
      case KeyEvent.VK_UP:
        next = pusher.get(index).getLocation().getNeighbourLocation(Location.NORTH);
        pusher.get(index).setDirection(Location.NORTH);
        break;
      case KeyEvent.VK_RIGHT:
        next = pusher.get(index).getLocation().getNeighbourLocation(Location.EAST);
        pusher.get(index).setDirection(Location.EAST);
        break;
      case KeyEvent.VK_DOWN:
        next = pusher.get(index).getLocation().getNeighbourLocation(Location.SOUTH);
        pusher.get(index).setDirection(Location.SOUTH);
        break;
    }

    Target curTarget = (Target) getOneActorAt(pusher.get(index).getLocation(), Target.class);
    if (curTarget != null){
      curTarget.show();
    }


    if (next != null && pusher.get(index).canMove(next) == 1)
    {
      pusher.get(index).setLocation(next);
      updateLogResult();
    }
    refresh();
    return true;
  }



  public boolean keyReleased(KeyEvent evt)
  {
    return true;
  }

  /**
   * The method will generate a log result for all the movements of all actors
   * The log result will be tested against our expected output.
   * Your code will need to pass all the 3 test suites with 9 test cases.
   */
  private void updateLogResult() {
    movementIndex++;
    List<Actor> pushers = getActors(Pusher.class);
    List<Actor> ores = getActors(Ore.class);
    List<Actor> targets = getActors(Target.class);
    List<Actor> rocks = getActors(Rock.class);
    List<Actor> clays = getActors(Clay.class);
    List<Actor> bulldozers = getActors(Bulldozer.class);
    List<Actor> excavators = getActors(Excavator.class);

    logResult.append(movementIndex).append("#");
    logResult.append(ElementType.PUSHER.getShortType()).append(actorLocations(pushers)).append("#");
    logResult.append(ElementType.ORE.getShortType()).append(actorLocations(ores)).append("#");
    logResult.append(ElementType.TARGET.getShortType()).append(actorLocations(targets)).append("#");
    logResult.append(ElementType.ROCK.getShortType()).append(actorLocations(rocks)).append("#");
    logResult.append(ElementType.CLAY.getShortType()).append(actorLocations(clays)).append("#");
    logResult.append(ElementType.BULLDOZER.getShortType()).append(actorLocations(bulldozers)).append("#");
    logResult.append(ElementType.EXCAVATOR.getShortType()).append(actorLocations(excavators));

    logResult.append("\n");
  }

}
