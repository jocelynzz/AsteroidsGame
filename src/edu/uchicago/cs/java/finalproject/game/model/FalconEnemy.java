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

    // ==============================================================
    // CONSTRUCTOR
    // ==============================================================

    public FalconEnemy(int hits) {
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

      /*  if (hits == 0) {
        setColor(Color.YELLOW);
        } else if (hits == 1) {
            setColor(Color.GRAY);
        } else if (hits == 2)  {
            setColor(Color.LIGHT_GRAY);
        }*/

        int nDY = 1;
        setDeltaY(nDY);

        setCenter(new Point(Game.R.nextInt(Game.DIM.width), 0));

        setOrientation(90);

        //this is the size of the falconEnemey
        setRadius(50);
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
        if (x>=x1 && y >= y1) {
            angle = 90 - Math.toDegrees(Math.atan((y - y1) / (x - x1)));
        }
        else if (x<x1 && y >= y1) {
            angle = 90 + Math.toDegrees(Math.atan((y - y1) / (x1 - x)));
        }
        else if (y < y1) {
            angle = 90;
        }
        int ang = (int) angle;
        return ang;
    }

    public void move() {
        super.move();
        setOrientation(getOrientation());
        CommandCenter.movFoes.add(new BulletFoe(this));
    }

    public void draw(Graphics g) {

        Color colShip;
        colShip = Color.GREEN;

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

   // @Override
 // public void expire() {
   //     if (getExpire() == 0)
     //       CommandCenter.movFoes.remove(this);
       // else
         //   setExpire(getExpire() - 1);
    //}
    //@Override
    //public void expire() {
      //  ammo.expire();
        //super.expire();
   // }

    }
//end class
