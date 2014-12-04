package edu.uchicago.cs.java.finalproject.controller;

import edu.uchicago.cs.java.finalproject.game.model.*;
import edu.uchicago.cs.java.finalproject.game.view.GamePanel;
import edu.uchicago.cs.java.finalproject.sounds.Sound;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.sound.sampled.Clip;

// ===============================================
// == This Game class is the CONTROLLER
// ===============================================

public class Game implements Runnable, KeyListener {

  // ===============================================
  // FIELDS
  // ===============================================

  public static final Dimension DIM = new Dimension(900, 600); //the dimension of the game.1100, 900
  private GamePanel gmpPanel;
  public static Random R = new Random();
  public final static int ANI_DELAY = 45; // milliseconds between screen
  // updates (animation)
  private Thread thrAnim;
  private static int nTick = 0;
  private ArrayList<Tuple> tupMarkForRemovals;
  private ArrayList<Tuple> tupMarkForAdds;
  private boolean bMuted = true;

  private final int PAUSE = 80, // p key
      QUIT = 81, // q key
      LEFT = 37, // rotate left; left arrow
      RIGHT = 39, // rotate right; right arrow
      UP = 38, // thrust; up arrow
      DOWN = 40,
      START = 83, // s key
      FIRE = 32, // space key
      MUTE = 77, // m-key mute
      SHIELD = 79,        // a key arrow
//FUTURE USE
  SPECIAL = 70;          // fire special weapon;  F key

  private Clip clpThrust;
  private Clip clpMusicBackground;

  private static final int SPAWN_NEW_SHIP_FLOATER = 1200;
  private static final int SPAWN_NEW_FALCON_ENEMY = 1200;

  private boolean pause;

  // ===============================================
  // ==CONSTRUCTOR
  // ===============================================

  public Game() {

    gmpPanel = new GamePanel(DIM);
    gmpPanel.addKeyListener(this);

    clpThrust = Sound.clipForLoopFactory("whitenoise.wav");
    clpMusicBackground = Sound.clipForLoopFactory("music-background.wav");
  }

  // ===============================================
  // ==METHODS
  // ===============================================

  public static void main(String args[]) {
    EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
      public void run() {
        try {
          Game game = new Game(); // construct itself
          game.fireUpAnimThread();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void fireUpAnimThread() { // called initially
    if (thrAnim == null) {
      thrAnim = new Thread(this); // pass the thread a runnable object (this)
      thrAnim.start();
    }
  }

  // implements runnable - must have run method
  public void run() {

    // lower this thread's priority; let the "main" aka 'Event Dispatch'
    // thread do what it needs to do first
    thrAnim.setPriority(Thread.MIN_PRIORITY);

    // and get the current time
    long lStartTime = System.currentTimeMillis();

    // this thread animates the scene
    while (Thread.currentThread() == thrAnim) {
      if (pause) {
        continue;
      }
      tick();
      addTailDebris();
      spawnNewShipFloater();
      spawnNewFalconEnemy();
      gmpPanel.update(gmpPanel.getGraphics()); // update takes the graphics context we must
      // surround the sleep() in a try/catch block
      // this simply controls delay time between
      // the frames of the animation
      //this might be a good place to check for collisions
      checkCollisions();
      //this might be a god place to check if the level is clear (no more foes)
      //if the level is clear then spawn some big asteroids -- the number of asteroids
      //should increase with the level.
      checkNewLevel();
      if (getNumAsteroids() < CommandCenter.getLevel() / 2) {
        spawnAsteroids(CommandCenter.getLevel() / 10 + Game.R.nextInt(2));
      }

      try {
        // The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update)
        // between frames takes longer than ANI_DELAY, then the difference between lStartTime -
        // System.currentTimeMillis() will be negative, then zero will be the sleep time
        lStartTime += ANI_DELAY;
        Thread.sleep(Math.max(0, lStartTime - System.currentTimeMillis()));
      } catch (InterruptedException e) {
        // just skip this frame -- no big deal
        continue;
      }
    } // end while
  } // end run

  private void addTailDebris() {
    for (Movable mv : CommandCenter.scheduledDebris) {
      CommandCenter.getMovDebris().add(mv);
    }
    CommandCenter.scheduledDebris.clear();
  }

  public void removeAsteroid() {
    for (Movable movFoe : CommandCenter.movFoes) {
      if (movFoe instanceof Asteroid || movFoe instanceof FalconEnemy) {
        if ((movFoe.getCenter().y >= Game.DIM.height && ((Sprite) movFoe).getDeltaY() > 0) ||
            (movFoe.getCenter().y <= 0 && ((Sprite) movFoe).getDeltaY() < 0) ||
            (movFoe.getCenter().x <= 0 && ((Sprite) movFoe).getDeltaX() < 0) ||
            (movFoe.getCenter().x >= Game.DIM.width && ((Sprite) movFoe).getDeltaX() > 0)) {
          tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
        }
      }
    }
    for (Movable floaters : CommandCenter.movFloaters) {
      if (floaters instanceof NewShipFloater) {
        if ((floaters.getCenter().y >= Game.DIM.height && ((Sprite) floaters).getDeltaY() > 0) ||
            (floaters.getCenter().y <= 0 && ((Sprite) floaters).getDeltaY() < 0) ||
            (floaters.getCenter().x <= 0 && ((Sprite) floaters).getDeltaX() < 0) ||
            (floaters.getCenter().x >= Game.DIM.width && ((Sprite) floaters).getDeltaX() > 0)) {
          tupMarkForRemovals.add(new Tuple(CommandCenter.movFloaters, floaters));
        }
      }
    }
    for (Movable friends : CommandCenter.movFriends) {
      if (friends instanceof Bullet) {
        if ((friends.getCenter().y >= Game.DIM.height && ((Sprite) friends).getDeltaY() > 0) ||
            (friends.getCenter().y <= 0 && ((Sprite) friends).getDeltaY() < 0) ||
            (friends.getCenter().x <= 0 && ((Sprite) friends).getDeltaX() < 0) ||
            (friends.getCenter().x >= Game.DIM.width && ((Sprite) friends).getDeltaX() > 0)) {
          tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, friends));
        }
      }
    }
    for (Movable friends : CommandCenter.movDebris) {
      if ((friends.getCenter().y >= Game.DIM.height && ((Sprite) friends).getDeltaY() > 0) ||
          (friends.getCenter().y <= 0 && ((Sprite) friends).getDeltaY() < 0) ||
          (friends.getCenter().x <= 0 && ((Sprite) friends).getDeltaX() < 0) ||
          (friends.getCenter().x >= Game.DIM.width && ((Sprite) friends).getDeltaX() > 0) ||
          ((Sprite) friends).getExpire() == 0) {
        tupMarkForRemovals.add(new Tuple(CommandCenter.movDebris, friends));
      }
    }
  }

  private void checkCollisions() {
    //life was killed
    //@formatter:off
    //for each friend in movFriends
    //for each foe in movFoes
    //if the distance between the two centers is less than the sum of their radii
    //mark it for removal

    //for each mark-for-removal
    //remove it
    //for each mark-for-add
    //add it
    //@formatter:on

    //we use this ArrayList to keep pairs of movMovables/movTarget for either
    //removal or insertion into our arrayLists later on
    tupMarkForRemovals = new ArrayList<Tuple>();
    tupMarkForAdds = new ArrayList<Tuple>();

    Point pntFriendCenter, pntFoeCenter;
    int nFriendRadiux, nFoeRadiux;

    Falcon falcon = CommandCenter.getFalcon();
    for (Movable movFriend : CommandCenter.movFriends) {
      for (Movable movFoe : CommandCenter.movFoes) {

        pntFriendCenter = movFriend.getCenter();
        pntFoeCenter = movFoe.getCenter();
        nFriendRadiux = movFriend.getRadius();
        nFoeRadiux = movFoe.getRadius();

        //detect collision
        if (pntFriendCenter.distance(pntFoeCenter) < (nFriendRadiux + nFoeRadiux)) {

          //falcon
          if ((movFriend instanceof Falcon)) {
            if (!falcon.getProtected() && falcon.getShield() == 0) {
              //when the shield is off
              // remove both friends and foes from the arraylist
              tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
              CommandCenter.spawnFalcon(false);
              killFoe(movFoe);//killing the enemies
            } else if (!falcon.getProtected() && falcon.getShield() > 0) {
              // when the shield is on, you can shoot. Foes removed from the list
              falcon.setShield(Math.max(falcon.getShield() - 25, 0));
              killFoe(movFoe);
            }
          }
          //not the falcon. ie if it's the bullet. Both bullets and foes removed
          else {
            tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
            killFoe(movFoe);
          }//end else

          //explode/remove foe

        }//end if
      }//end inner for
    }//end outer for

    //check for collisions between falcon and floaters
    if (falcon != null) {
      Point pntFalCenter = falcon.getCenter();
      int nFalRadiux = falcon.getRadius();
      Point pntFloaterCenter;
      int nFloaterRadiux;

      for (Movable movFloater : CommandCenter.movFloaters) {
        pntFloaterCenter = movFloater.getCenter();
        nFloaterRadiux = movFloater.getRadius();

        //detect collision
        if (pntFalCenter.distance(pntFloaterCenter) < (nFalRadiux + nFloaterRadiux)) {
          tupMarkForRemovals.add(new Tuple(CommandCenter.movFloaters, movFloater));
          // number of falcon increased by 1
          CommandCenter.setNumFalcons(CommandCenter.getNumFalcons() + 1);
          Sound.playSound("pacman_eatghost.wav");
        }//end if
      }//end inner for
    }//end if not null

    removeAsteroid();

    //remove these objects from their appropriate ArrayLists
    //this happens after the above iterations are done
    for (Tuple tup : tupMarkForRemovals) {
      tup.removeMovable();
    }

    //add these objects to their appropriate ArrayLists
    //this happens after the above iterations are done
    for (Tuple tup : tupMarkForAdds) {
      tup.addMovable();
    }

    //call garbage collection
    System.gc();
  }//end meth

  private void killFoe(Movable movFoe) {

    if (movFoe instanceof Asteroid) {

      //we know this is an Asteroid, so we can cast without threat of ClassCastException
      Asteroid astExploded = (Asteroid) movFoe;
      //big asteroid
      long score = CommandCenter.getScore();
      if (astExploded.getSize() == 0) {
        //spawn two medium randomn type Asteroids
        tupMarkForAdds.add(new Tuple(CommandCenter.movFoes, new Asteroid(astExploded, Util.generateAsteroidType())));
        tupMarkForAdds.add(new Tuple(CommandCenter.movFoes, new Asteroid(astExploded, Util.generateAsteroidType())));
        CommandCenter.setScore(astExploded.getType() == AsteroidType.FIRE ? score + 50 : score + 10);
      } else {
        tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
        CommandCenter.setScore(astExploded.getType() == AsteroidType.FIRE ? score + 100 : score + 2);
      }
      //set weapon boost type if it's not a normal asteroid
      if (astExploded.getType() != AsteroidType.NORMAL) {
        CommandCenter.getFalcon().setBoost(astExploded.getType());
      }
      //remove the original Foe
      tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
    }
    //not an asteroid
    else {
      //remove the original Foe
      tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
    }

    tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
  }

  //some methods for timing events in the game,
  //such as the appearance of UFOs, floaters (power-ups), etc.
  public void tick() {
    if (nTick == Integer.MAX_VALUE) {
      nTick = 0;
    } else {
      nTick++;
    }
  }

  public static int getTick() {
    return nTick;
  }

  private void spawnNewShipFloater() {
    //make the appearance of power-up dependent upon ticks and levels
    //the higher the level the more frequent the appearance
    if (nTick % (SPAWN_NEW_SHIP_FLOATER - CommandCenter.getLevel() * 7) == 0) {
      CommandCenter.movFloaters.add(new NewShipFloater());
    }
  }

 private void spawnNewFalconEnemy() {
        //make the appearance of power-up dependent upon ticks and levels
        //the higher the level the more frequent the appearance
       if (nTick % (SPAWN_NEW_FALCON_ENEMY - CommandCenter.getLevel() * 7) == 0) {
        CommandCenter.movFoes.add(new FalconEnemy(0));
        }
    }

  // Called when user presses 's'
  private void startGame() {
    CommandCenter.clearAll();
    CommandCenter.initGame();
    CommandCenter.setLevel(0);
    CommandCenter.setPlaying(true);
    CommandCenter.setPaused(false);
    if (!bMuted)
    clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
  }

  //this method spawns new asteroids
  private void spawnAsteroids(int nNum) {
    for (int nC = 0; nC < nNum; nC++) {
      //Asteroids with size of zero are big
      CommandCenter.movFoes.add(new Asteroid(0, Util.generateAsteroidType()));
    }
  }

  //there should always be asteroids on the screen
  private boolean isLevelClear() {
    //if there are no more Asteroids on the screen
    return getNumAsteroids() == 0;
  }

  private int getNumAsteroids() {
    int numAsteroids = 0;
    for (Movable movFoe : CommandCenter.movFoes) {
      if (movFoe instanceof Asteroid) {
        numAsteroids++;
      }
    }
    return numAsteroids;
  }

  private void checkNewLevel() {

    if (isLevelClear()) {
        //falcon is not protected in the beginning of every level

      spawnAsteroids(CommandCenter.getLevel() + 2);
      CommandCenter.setLevel(CommandCenter.getLevel() + 1);
    }
  }

  // Varargs for stopping looping-music-clips
  private static void stopLoopingSounds(Clip... clpClips) {
    for (Clip clp : clpClips) {
      clp.stop();
    }
  }

  // ===============================================
  // KEYLISTENER METHODS
  // ===============================================

  Set<Integer> keyPressed = new HashSet<Integer>();

  @Override
  public void keyPressed(KeyEvent e) {
    Falcon fal = CommandCenter.getFalcon();
    int nKey = e.getKeyCode();
    // System.out.println(nKey);
    keyPressed.add(nKey);

    if (nKey == START && !CommandCenter.isPlaying()) {
      startGame();
    }

    if (fal != null) {

      switch (nKey) {
        case QUIT:
          System.exit(0);
          break;
        case UP:
          fal.move(UP);
          //fal.thrustOn();
          //	if (!CommandCenter.isPaused())
          //clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
          break;
        case LEFT:
          fal.move(LEFT);
          break;
        case RIGHT:
          fal.move(RIGHT);
          break;
        case DOWN:
          fal.move(DOWN);
          break;
        default:
          break;
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    Falcon fal = CommandCenter.getFalcon();
    int nKey = e.getKeyCode();
    keyPressed.remove(nKey);

    if (fal != null) {
      switch (nKey) {
        case FIRE:
          int extraBullets = fal.getNumExtraBullets();
          CommandCenter.movFriends.add(new Bullet(fal, 0));
          int bulletAngle = 5;
          for (int i = 1; i <= extraBullets; i++) {
            CommandCenter.movFriends.add(new Bullet(fal, bulletAngle * i));
            CommandCenter.movFriends.add(new Bullet(fal, -bulletAngle * i));
          }
          Sound.playSound("laser.wav");
          break;

        //special is a special weapon, current it just fires the cruise missile.
     //   case SPECIAL:
       //   CommandCenter.movFriends.add(new Cruise(fal));//this line was removed during class
          //for (int nC = 0;)
       //   Sound.playSound("laser.wav");
         // break;
        case LEFT:
          fal.stopMoving(LEFT);
          break;
        case RIGHT:
          fal.stopMoving(RIGHT);
          break;
        case UP:
          fal.stopMoving(UP);
          break;
        case DOWN:
          fal.stopMoving(DOWN);
          break;
        case MUTE:
          if (!bMuted) {
            stopLoopingSounds(clpMusicBackground);
            bMuted = !bMuted;
          } else {
            clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
            bMuted = !bMuted;
          }
          break;
        case PAUSE:
          togglePause();
          break;
        default:
          break;
      }
    }
  }

  private void togglePause() {
    pause ^= true;
  }

  @Override
  // Just need it b/c of KeyListener implementation
  public void keyTyped(KeyEvent e) {
  }
}

// ===============================================
// ==A tuple takes a reference to an ArrayList and a reference to a Movable
//This class is used in the collision detection method, to avoid mutating the array list while we are iterating
// it has two public methods that either remove or add the movable from the appropriate ArrayList 
// ===============================================

class Tuple {
  //this can be any one of several CopyOnWriteArrayList<Movable>
  private CopyOnWriteArrayList<Movable> movMovs;
  //this is the target movable object to remove
  private Movable movTarget;

  public Tuple(CopyOnWriteArrayList<Movable> movMovs, Movable movTarget) {
    this.movMovs = movMovs;
    this.movTarget = movTarget;
  }

  public void removeMovable() {
    movMovs.remove(movTarget);
  }

  public void addMovable() {
    movMovs.add(movTarget);
  }
}
