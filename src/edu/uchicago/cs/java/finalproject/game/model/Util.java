package edu.uchicago.cs.java.finalproject.game.model;

import edu.uchicago.cs.java.finalproject.controller.Game;

public class Util {
    //generate Asteroid type
    public static AsteroidType generateAsteroidType() {
        int ran = Game.R.nextInt(100);
        for (AsteroidType t : AsteroidType.values()) {
            if (t.isInSpawnRange(ran))
            {
                return t;
            }
        }
        throw new IllegalStateException("Unable to select asteroid type");
    }
}
