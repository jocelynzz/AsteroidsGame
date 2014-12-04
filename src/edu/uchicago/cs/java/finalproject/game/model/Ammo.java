package edu.uchicago.cs.java.finalproject.game.model;

/**
 * Created by Jocelyn on 11/28/14.
 */
public class Ammo extends Sprite {
//set the expiry time for extra bullets
  @Override
  public void expire() {
    if (getExpire() == 0) {
      return;
    } else {
      setExpire(getExpire() - 1);
    }
  }
}
