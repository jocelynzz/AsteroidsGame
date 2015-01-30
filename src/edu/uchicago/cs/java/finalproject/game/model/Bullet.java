package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;


public class Bullet extends Sprite {

  private final double FIRE_POWER = 35.0;

  public Bullet(Falcon fal, int orientation) {

    super();

    ArrayList<Point> pntCs = new ArrayList<Point>();
    pntCs.add(new Point(0, 3));
    pntCs.add(new Point(1, -1));
    pntCs.add(new Point(0, -2));
    pntCs.add(new Point(-1, -1));

    assignPolarPoints(pntCs);

    setExpire(20);
    setRadius(6);
    setOrientation(orientation + fal.getOrientation());

    //everything is relative to the falcon ship that fired the bullet

    setDeltaX(Math.cos(Math.toRadians(getOrientation())) * FIRE_POWER);
    setDeltaY(Math.sin(Math.toRadians(getOrientation())) * FIRE_POWER);
    setCenter(fal.getCenter());
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
