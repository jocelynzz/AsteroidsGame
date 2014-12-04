package edu.uchicago.cs.java.finalproject.game.model;

/**
 * Created by Jocelyn on 11/28/14.
 */
public enum AsteroidType {
    NORMAL(0, 70),
    SHIELD(70, 85),
    EXTRA_BULLET(85, 95),
    FIRE(90, 100);

    private final int probabilitySpawnMin;
    private final int probabilitySpawnMax;

    AsteroidType(int min, int max) {
        probabilitySpawnMin = min;
        probabilitySpawnMax = max;
    }

    public boolean isInSpawnRange(int v) {
        return v >= probabilitySpawnMin && v < probabilitySpawnMax;
    }
}
