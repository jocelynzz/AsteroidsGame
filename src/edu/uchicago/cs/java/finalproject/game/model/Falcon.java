package edu.uchicago.cs.java.finalproject.game.model;

import edu.uchicago.cs.java.finalproject.controller.Game;

import java.awt.*;
import java.util.ArrayList;


public class Falcon extends Sprite {

  // ==============================================================
  // FIELDS
  // ==============================================================

  private final double THRUST = .65;

  final int DEGREE_STEP = 7;

  //private boolean bShield = false;
  private boolean bFlame = false;
  private boolean bProtected; //for fade in and out

  /*private boolean bThrusting = false;
  private boolean bTurningRight = false;
  private boolean bTurningLeft = false;*/
  public static final int SPEED = 24;

  private Shield nShield = new Shield();
  private Ammo ammo = new Ammo();

  private final double[] FLAME =
      {23 * Math.PI / 24 + Math.PI / 2, Math.PI + Math.PI / 2, 25 * Math.PI / 24 + Math.PI / 2};

  private int[] nXFlames = new int[FLAME.length];
  private int[] nYFlames = new int[FLAME.length];

  private Point[] pntFlames = new Point[FLAME.length];
  private int numExtraBullets;

  // ==============================================================
  // CONSTRUCTOR
  // ==============================================================

  public Falcon() {
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

    setColor(Color.white);

    double n = Game.DIM.height * 0.75;
    //put falcon in the middle.
    setCenter(new Point(Game.DIM.width / 2, (int) n));

    //with random orientation
    setOrientation(270);
    //setOrientation(Game.R.nextInt(360));

    //this is the size of the falcon
    setRadius(35);

    //these are falcon specific
    setProtected(true);
    setFadeValue(0);
  }

  // ==============================================================
  // METHODS
  // ==============================================================

  public void move(int dir) {//control the movement of the falcon.
    switch (dir) {
      case UP:
        setDeltaY(-SPEED);
        break;
      case DOWN:
        setDeltaY(SPEED);
        break;
      case LEFT:
        setDeltaX(-SPEED);
        break;
      case RIGHT:
        setDeltaX(SPEED);

        //  default:
        // break; /*
    /*super.move();
		if (bThrusting) {
			bFlame = true;
			double dAdjustX = Math.cos(Math.toRadians(getOrientation()))
					* THRUST;
			double dAdjustY = Math.sin(Math.toRadians(getOrientation()))
					* THRUST;
			setDeltaX(getDeltaX() + dAdjustX);
			setDeltaY(getDeltaY() + dAdjustY);
		}
		if (bTurningLeft) {

			//if (getOrientation() <= 0 && bTurningLeft) {
				//setOrientation(360);//
            setDeltaX(-SPEED);
			//}
			//setOrientation(getOrientation() - DEGREE_STEP);
		} 
		if (bTurningRight) {
			/*if (getOrientation() >= 360 && bTurningRight) {
				setOrientation(0);
			}
			setOrientation(getOrientation() + DEGREE_STEP);*/
        // setDeltaX(SPEED);
    }
  }
  //} //end move

	/*public void rotateLeft() {
		bTurningLeft = true;
	}

	public void rotateRight() {
		bTurningRight = true;
	}

	public void stopRotating() {
		bTurningRight = false;
		bTurningLeft = false;
	}

	public void thrustOn() {
		bThrusting = true;
	}

	public void thrustOff() {
		bThrusting = false;
		bFlame = false;
	}*/

  private int adjustColor(int nCol, int nAdj) {
    if (nCol - nAdj <= 0) {
      return 0;
    } else {
      return nCol - nAdj;
    }
  }

  public void stopMoving(int Dir) {

    switch (Dir) {
      case UP:
        setDeltaY(0);
        break;
      case DOWN:
        setDeltaY(0);
        break;
      case LEFT:
        setDeltaX(0);
        break;
      case RIGHT:
        setDeltaX(0);
        break;
      default:
        break;
    }
  }

  public void draw(Graphics g) {

    //does the fading at the beginning or after hyperspace
    Color colShip;
    if (getFadeValue() == 255) {
      colShip = Color.white;
    } else {
      colShip = new Color(adjustColor(getFadeValue(), 200), adjustColor(getFadeValue(), 175), getFadeValue());
    }

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

    if (CommandCenter.getFalcon().getShield() > 0) {

      //setShield(getShield() - 1);

      g.setColor(Color.cyan);
      g.drawOval(getCenter().x - getRadius(), getCenter().y - getRadius(), getRadius() * 2, getRadius() * 2);
    } //end if shield
    g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
  }

  public void fadeInOut() {
    if (getProtected()) {
      setFadeValue(getFadeValue() + 3);
    }
    if (getFadeValue() == 255) {
      setProtected(false);
    }
  }

  public void setProtected(boolean bParam) {
    if (bParam) {
      setFadeValue(0);
    }
    bProtected = bParam;
  }

  public void setProtected(boolean bParam, int n) {
    if (bParam && n % 3 == 0) {
      setFadeValue(n);
    } else if (bParam) {
      setFadeValue(0);
    }
    bProtected = bParam;
  }

  @Override
  public void expire() {
    nShield.expire();
    ammo.expire();
    super.expire();
  }

  public boolean getProtected() {
    return bProtected;
  }

  public void setShield(int n) {
    nShield.setExpire(n);
  }

  public int getShield() {
    return nShield.getExpire();
  }

  public void setBoost(AsteroidType boost) {
    if (boost == AsteroidType.SHIELD) {
      setShield(100);
    } else if (boost == AsteroidType.EXTRA_BULLET) {
      int expire = ammo.getExpire();
      ammo.setExpire(Math.min(expire == 0 ? 150 : expire + 100, 375));
    }
  }

  public int getNumExtraBullets() {
    return Math.min(ammo.getExpire() / 100, 3);
  }
} //end class
