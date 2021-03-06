package edu.uchicago.cs.java.finalproject.game.model;

/**
 * Created by Joceyn on 11/17/14.
 */

import java.awt.*;
import java.util.ArrayList;



import edu.uchicago.cs.java.finalproject.controller.Game;


public class RingBullet extends Bullet {

    private final double FIRE_POWER = 20.0;



    public RingBullet(Falcon fal, int nOr){

        super(fal, 0);


        //defined the points on a cartesean grid
        ArrayList<Point> pntCs = new ArrayList<Point>();
        //don't need to worry about this part.[0, 0] is the centre point
        pntCs.add(new Point(0,3)); //top point
        pntCs.add(new Point(1,-1));
        pntCs.add(new Point(0,-2));
        pntCs.add(new Point(-1,-1));

        assignPolarPoints(pntCs);

        //a bullet expires after 20 frames
        setExpire( 20 );
        setRadius(6);


        //everything is relative to the falcon ship that fired the bullet
        setDeltaX( fal.getDeltaX() +
                Math.cos( Math.toRadians( fal.getOrientation() ) ) * FIRE_POWER );
        setDeltaY( fal.getDeltaY() +
                Math.sin( Math.toRadians( fal.getOrientation() ) ) * FIRE_POWER );
        setCenter( fal.getCenter() );

        //set the bullet orientation to the falcon (ship) orientation
        setOrientation(fal.getOrientation());


    }

    //override the expire method - once an object expires, then remove it from the arrayList.
    public void expire(){
        if (getExpire() == 0)
            CommandCenter.movFriends.remove(this);
        else
            setExpire(getExpire() - 1);
    }

}
