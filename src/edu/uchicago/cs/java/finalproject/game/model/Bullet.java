package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;


public class Bullet extends Sprite {

  private final double FIRE_POWER = 35.0;

  public Bullet(Falcon fal, int orientation) {

    super();

    //defined the points on a cartesean grid
    ArrayList<Point> pntCs = new ArrayList<Point>();
    //don't need to worry about this part.[0, 0] is the centre point
    pntCs.add(new Point(0, 3)); //top point

    pntCs.add(new Point(1, -1));
    pntCs.add(new Point(0, -2));
    pntCs.add(new Point(-1, -1));

    assignPolarPoints(pntCs);

    //a bullet expires after 20 frames
    setExpire(20);
    setRadius(6);
    setOrientation(orientation + fal.getOrientation());

    //everything is relative to the falcon ship that fired the bullet

    setDeltaX(Math.cos(Math.toRadians(getOrientation())) * FIRE_POWER);
    setDeltaY(Math.sin(Math.toRadians(getOrientation())) * FIRE_POWER);
    setCenter(fal.getCenter());
//    setDim(new Dimension(Game.DIM.width, Game.DIM.height - fal.getCenter().y));
  }

  //override the expire method - once an object expires, then remove it from the arrayList.
  public void expire() {
    if (getExpire() == 0) {
      CommandCenter.movFriends.remove(this);
    } else {
      setExpire(getExpire() - 1);
    }
  }
}
