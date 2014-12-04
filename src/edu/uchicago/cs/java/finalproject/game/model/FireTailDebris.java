package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;


public class FireTailDebris extends Sprite {

  public FireTailDebris(Point center) {
    super();
    setCenter(center);
    setExpire(4);
    ArrayList<Point> pntCs = new ArrayList<Point>();
    // top of ship
    pntCs.add(new Point(2, 2));
    pntCs.add(new Point(2, 0));
    pntCs.add(new Point(0, 0));
    pntCs.add(new Point(0, 2));
    setColor(Color.ORANGE);
    setRadius(5);

    assignPolarPoints(pntCs);
  }

  @Override
  public void draw(Graphics g) {
    super.draw(g);
    //fill this polygon (with whatever color it has)
    g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
    //now draw a white border
  }

  @Override
  public void expire() {
    if (getExpire() == 0) {
      return;
    } else {
      setExpire(getExpire() - 1);
    }
  }
}