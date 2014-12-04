package edu.uchicago.cs.java.finalproject.game.model;
//pac

import java.awt.*;
import java.util.Arrays;

import edu.uchicago.cs.java.finalproject.controller.Game;


public class Asteroid extends Sprite {

  private final AsteroidType type;

  private int nSpin;

  //radius of a large asteroid
  private final int RAD = 60;

  //nSize determines if the Asteroid is Large (0), Medium (1), or Small (2)
  //when you explode a Large asteroid, you should spawn 2 or 3 medium asteroids
  //same for medium asteroid, you should spawn small asteroids
  //small asteroids get blasted into debris
  public Asteroid(int nSize, AsteroidType type) {

    //call Sprite constructor
    super();
    this.type = type;

    //the spin will be either plus or minus 0-9
    int nSpin = Game.R.nextInt(10);
    if (nSpin % 2 == 0) {
      nSpin = -nSpin;
    }
    setSpin(nSpin);

    //random delta-y
    int nDY = Game.R.nextInt(10) + 3;
    if (type == AsteroidType.FIRE) {
      nDY += 35;
    }
    setDeltaY(nDY);

    setCenter(new Point(Game.R.nextInt(Game.DIM.width), 0));
    setOrientation(90);

    assignRandomShape();
    assignColor();

    //an nSize of zero is a big asteroid
    //a nSize of 1 or 2 is med or small asteroid respectively
    if (nSize == 0) {
      setRadius(RAD);
    } else {
      setRadius(RAD / (nSize * 2));
    }
  }

  public Asteroid(Asteroid astExploded, AsteroidType type) {
    //call Sprite constructor
    super();
    this.type = type;

    int nSizeNew = astExploded.getSize() + 1;

    //the spin will be either plus or minus 0-9
    int nSpin = Game.R.nextInt(10);
    if (nSpin % 2 == 0) {
      nSpin = -nSpin;
    }
    setSpin(nSpin);
    //fix the
    //random delta-x
    int nDX = Game.R.nextInt(10 + nSizeNew * 2);
    if (type == AsteroidType.FIRE) {
      nDX += 35;
    }
    if (nDX % 2 == 0) {
      nDX = -nDX;
    }
    setDeltaX(nDX);

    //random delta-y
    int nDY = Game.R.nextInt(15 + nSizeNew * 2) + 1;
    if (type == AsteroidType.FIRE) {
      nDY += 35;
    }
    if (nDY % 2 == 0) {
      nDY = -nDY;
    }
    setDeltaY(nDY);

    assignRandomShape();
    assignColor();

    //an nSize of zero is a big asteroid
    //a nSize of 1 or 2 is med or small asteroid respectively
    // new Asteroid(nSizeNew);
    int radius = RAD / (nSizeNew * 2);
    if (type == AsteroidType.FIRE) {
      radius /= 2;
    }
    setRadius(radius);
    setCenter(astExploded.getCenter());
  }

  private void drawTail() {
    if (type == AsteroidType.FIRE) {
      CommandCenter.scheduledDebris.add(new FireTailDebris(this.getCenter()));
    }
  }

  private void assignColor() {
      //SHIELD
    if (type == AsteroidType.SHIELD) {
      setColor(Color.CYAN);
      //EXTRA－BULLETS
    } else if (type == AsteroidType.EXTRA_BULLET) {
      setColor(Color.RED);
     //DEBRIS
    } else if (type == AsteroidType.FIRE) {
      setColor(Color.ORANGE);
    }
  }

  public int getSize() {

    int nReturn = 0;

    switch (getRadius()) {
      case 60:
        nReturn = 0;
        break;
      case 30:
        nReturn = 1;
        break;
      case 15:
        nReturn = 2;
        break;
    }
    return nReturn;
  }

  //overridden
  public void move() {
    super.move();
    //an asteroid spins, so you need to adjust the orientation at each move()
    setOrientation(getOrientation() + getSpin());
    drawTail();
  }

  public int getSpin() {
    return this.nSpin;
  }

  public void setSpin(int nSpin) {
    this.nSpin = nSpin;
  }

  //this is for an asteroid only
  public void assignRandomShape() {
    int nSide = Game.R.nextInt(7) + 7;
    int nSidesTemp = nSide;

    int[] nSides = new int[nSide];
    for (int nC = 0; nC < nSides.length; nC++) {
      int n = nC * 48 / nSides.length - 4 + Game.R.nextInt(8);
      if (n >= 48 || n < 0) {
        n = 0;
        nSidesTemp--;
      }
      nSides[nC] = n;
    }

    Arrays.sort(nSides);

    double[] dDegrees = new double[nSidesTemp];
    for (int nC = 0; nC < dDegrees.length; nC++) {
      dDegrees[nC] = nSides[nC] * Math.PI / 24 + Math.PI / 2;
    }
    setDegrees(dDegrees);

    double[] dLengths = new double[dDegrees.length];
    for (int nC = 0; nC < dDegrees.length; nC++) {
      if (nC % 3 == 0) {
        dLengths[nC] = 1 - Game.R.nextInt(40) / 100.0;
      } else {
        dLengths[nC] = 1;
      }
    }
    setLengths(dLengths);
  }

  public AsteroidType getType() {
    return type;
  }
}
