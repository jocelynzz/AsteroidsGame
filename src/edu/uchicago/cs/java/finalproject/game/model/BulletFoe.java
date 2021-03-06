package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jocelyn on 12/4/14.
 */
public class BulletFoe extends Sprite {
    private final double FIRE_POWER = 30.0;
    public BulletFoe(FalconEnemy falEnemy) {

        super();

        //defined the points on a cartesean grid
        ArrayList<Point> pntCs = new ArrayList<Point>();
        pntCs.add(new Point(0, 3)); //top point

        pntCs.add(new Point(1, -1));
        pntCs.add(new Point(0, -2));
        pntCs.add(new Point(-1, -1));

        setColor(Color.YELLOW);

        assignPolarPoints(pntCs);

        // this bullet expires after 15 frames
        setExpire(15);
        setRadius(4);
        setOrientation(falEnemy.getOrientation());

        //everything is relative to the enemy ship that fired the bullet

        setDeltaX(Math.cos(Math.toRadians(getOrientation())) * FIRE_POWER);
        setDeltaY(Math.sin(Math.toRadians(getOrientation())) * FIRE_POWER);
        setCenter(falEnemy.getCenter());
    }

    //override the expire method - once an object expires, then remove it from the arrayList.
    public void expire() {
        if (getExpire() == 0) {
            CommandCenter.movFoes.remove(this);
        } else {
            setExpire(getExpire() - 1);
        }
    }
}
