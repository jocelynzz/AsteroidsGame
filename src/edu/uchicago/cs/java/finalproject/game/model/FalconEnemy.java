package edu.uchicago.cs.java.finalproject.game.model;

/**
 * Created by Joceyn on 12/4/14.
 */

import edu.uchicago.cs.java.finalproject.controller.Game;

import java.awt.*;
import java.util.ArrayList;

import java.awt.*;
import java.util.ArrayList;


public class FalconEnemy extends Sprite {

    // ==============================================================
    // FIELDS
    // ==============================================================

   // private final double THRUST = .65;

    final int DEGREE_STEP = 7;

    private boolean bFlame = false;
    //private boolean bProtected; //for fade in and out

    private final double[] FLAME =
            {23 * Math.PI / 24 + Math.PI / 2, Math.PI + Math.PI / 2, 25 * Math.PI / 24 + Math.PI / 2};

    private int[] nXFlames = new int[FLAME.length];
    private int[] nYFlames = new int[FLAME.length];

    private Point[] pntFlames = new Point[FLAME.length];
    private int _hits;

  // ==============================================================
    // CONSTRUCTOR
    // ==============================================================



  public FalconEnemy() {
        super();

    ArrayList<Point> pntCs = new ArrayList<Point>();

        // top of ship
        pntCs.add(new Point(0, 18));

        //right points
        pntCs.add(new Point(3, 3));
        pntCs.add(new Point(12, 0));
        pntCs.add(new Point(13, -2));
        pntCs.add(new Point(13, -4));
        pntCs.add(new Point(11, -2));
        pntCs.add(new Point(4, -3));
        pntCs.add(new Point(2, -10));
        pntCs.add(new Point(4, -12));
        pntCs.add(new Point(2, -13));

        //left points
        pntCs.add(new Point(-2, -13));
        pntCs.add(new Point(-4, -12));
        pntCs.add(new Point(-2, -10));
        pntCs.add(new Point(-4, -3));
        pntCs.add(new Point(-11, -2));
        pntCs.add(new Point(-13, -4));
        pntCs.add(new Point(-13, -2));
        pntCs.add(new Point(-12, 0));
        pntCs.add(new Point(-3, 3));

        assignPolarPoints(pntCs);

        int nDY = 1 + Game.R.nextInt(5);
        setDeltaY(nDY);

        setCenter(new Point(Game.R.nextInt(Game.DIM.width), 0));

        setOrientation(90);

        //this is the size of the falconEnemey
        setRadius(50);
        setColor(Color.MAGENTA);
        //setExpire(250);

    }

    // METHODS
    // ==============================================================

    @Override
    public int getOrientation() {
        double x = CommandCenter.getFalcon().getCenter().getX();
        double y = CommandCenter.getFalcon().getCenter().getY();
        double x1 = this.getCenter().getX();
        double y1 = this.getCenter().getY();
        double angle = 0.0;
        if (x >= x1 && y >= y1) {
            angle = Math.toDegrees(Math.atan((y - y1) / (x - x1)));
        } else if (x < x1 && y >= y1) {
            angle = 180 - Math.toDegrees(Math.atan((y - y1) / (x1 - x)));
        } else if (y < y1 && x < x1) {
            angle = 180 + Math.toDegrees(Math.atan((y1 - y) / (x1 - x)));
        } else {
            angle = -Math.toDegrees(Math.atan((y1 - y) / (x - x1)));
        }
        int ang = (int) angle;
        return ang;
    }

    public void move() {
      if (_hits == 1) {
        setColor(Color.PINK);
      }
        super.move();
        setOrientation(getOrientation());
        if (Game.getTick() % 10 == 0) {
          CommandCenter.movFoes.add(new BulletFoe(this));
        }
    }

    public void draw(Graphics g) {

        Color colShip;
        colShip = getColor();

        //thrusting
        if (bFlame) {
            g.setColor(colShip);
            //the flame
            for (int nC = 0; nC < FLAME.length; nC++) {
                if (nC % 2 != 0) //odd
                {
                    pntFlames[nC] = new Point(
                            (int) (getCenter().x + 2 * getRadius() * Math.sin(Math.toRadians(getOrientation()) + FLAME[nC])),
                            (int) (getCenter().y - 2 * getRadius() * Math.cos(Math.toRadians(getOrientation()) + FLAME[nC])));
                } else //even
                {
                    pntFlames[nC] = new Point(
                            (int) (getCenter().x + getRadius() * 1.1 * Math.sin(Math.toRadians(getOrientation()) + FLAME[nC])),
                            (int) (getCenter().y - getRadius() * 1.1 * Math.cos(Math.toRadians(getOrientation()) + FLAME[nC])));
                } //end even/odd else
            } //end for loop

            for (int nC = 0; nC < FLAME.length; nC++) {
                nXFlames[nC] = pntFlames[nC].x;
                nYFlames[nC] = pntFlames[nC].y;
            } //end assign flame points

            //g.setColor( Color.white );

            g.fillPolygon(nXFlames, nYFlames, FLAME.length);
        } //end if flame

        drawShipWithColor(g, colShip);
    } //end draw()

    public void drawShipWithColor(Graphics g, Color col) {
        super.draw(g);
        g.setColor(col);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

  public int getHits() {
    return _hits;
  }

  public void setHits(int hits) {
    _hits = hits;
  }

    }
//end class
