package edu.uchicago.cs.java.finalproject.game.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;


import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.game.model.CommandCenter;
import edu.uchicago.cs.java.finalproject.game.model.Falcon;
import edu.uchicago.cs.java.finalproject.game.model.Movable;


 public class GamePanel extends Panel {
	
	// ==============================================================
	// FIELDS 
	// ============================================================== 
	 
	// The following "off" vars are used for the off-screen double-bufferred image. 
	private Dimension dimOff;
	private Image imgOff;
	private Graphics grpOff;
	
	private GameFrame gmf;
	private Font fnt = new Font("SansSerif", Font.BOLD, 12);
	private Font fntBig = new Font("SansSerif", Font.BOLD + Font.ITALIC, 36);
	private FontMetrics fmt; 
	private int nFontWidth;
	private int nFontHeight;
	private String strDisplay = "";
	

	// ==============================================================
	// CONSTRUCTOR 
	// ==============================================================
	
	public GamePanel(Dimension dim){
	    gmf = new GameFrame();
		gmf.getContentPane().add(this);
		gmf.pack();
		initView();
		
		gmf.setSize(dim);
		gmf.setTitle("Game Base");
		gmf.setResizable(false);
		gmf.setVisible(true);
		this.setFocusable(true);
	}
	
	
	// ==============================================================
	// METHODS 
	// ==============================================================
	
	private void drawScoreLevel(Graphics g) {
		g.setColor(Color.white);
		g.setFont(fnt);
		if (CommandCenter.getScore() != 0) {
			g.drawString("SCORE :  " + CommandCenter.getScore() + "    " + "LEVEL:  "
                    + CommandCenter.getLevel(), nFontWidth, nFontHeight);
		} else {
			g.drawString("NO SCORE", nFontWidth, nFontHeight);
		}
	}

     private void drawLevel(Graphics g) {
         g.setColor(Color.white);
         g.setFont(fnt);
         g.drawString("LEVEL :  " + CommandCenter.getLevel(), nFontWidth, nFontHeight);

         }

	@SuppressWarnings("unchecked")
	public void update(Graphics g) {
		if (grpOff == null || Game.DIM.width != dimOff.width
				|| Game.DIM.height != dimOff.height) {
			dimOff = Game.DIM;
			imgOff = createImage(Game.DIM.width, Game.DIM.height);
			grpOff = imgOff.getGraphics();
		}
		// Fill in background with black.
		grpOff.setColor(Color.black);
		grpOff.fillRect(0, 0, Game.DIM.width, Game.DIM.height);

		drawScoreLevel(grpOff);
		
		if (!CommandCenter.isPlaying()) {
			displayTextOnScreen();
		} else if (CommandCenter.isPaused()) {
			strDisplay = "Game Paused";
			grpOff.drawString(strDisplay,
					(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);
		}
		
		//playing and not paused!
		else {
			
			//draw them in decreasing level of importance
			//friends will be on top layer and debris on the bottom
			iterateMovables(grpOff, 
					   CommandCenter.movDebris,
			           CommandCenter.movFloaters, 
			           CommandCenter.movFoes,
			           CommandCenter.movFriends);
			
			
			drawNumberShipsLeft(grpOff);
      drawNumberShieldsRemaining(grpOff);
      drawNumberExtraBullets(grpOff);
			if (CommandCenter.isGameOver()) {
				CommandCenter.setPlaying(false);
				//bPlaying = false;
			}
		}
		//draw the double-Buffered Image to the graphics context of the panel
		g.drawImage(imgOff, 0, 0, this);
	} 


	
	//for each movable array, process it.
	private void iterateMovables(Graphics g, CopyOnWriteArrayList<Movable>...movMovz){
		
		for (CopyOnWriteArrayList<Movable> movMovs : movMovz) {
			for (Movable mov : movMovs) {

				mov.move();
				mov.draw(g);
				mov.fadeInOut();
				mov.expire();
			}
		}
		
	}
	

	// Draw the number of falcons left on the bottom-right of the screen. 
	private void drawNumberShipsLeft(Graphics g) {
		Falcon fal = CommandCenter.getFalcon();
		double[] dLens = fal.getLengths();
		int nLen = fal.getDegrees().length;
		Point[] pntMs = new Point[nLen];
		int[] nXs = new int[nLen];
		int[] nYs = new int[nLen];
	
		//convert to cartesean points
		for (int nC = 0; nC < nLen; nC++) {
			pntMs[nC] = new Point((int) (10 * dLens[nC] * Math.sin(Math
					.toRadians(90) + fal.getDegrees()[nC])),
					(int) (10 * dLens[nC] * Math.cos(Math.toRadians(90)
							+ fal.getDegrees()[nC])));
		}
		
		//set the color to white
		g.setColor(Color.white);
		//for each falcon left (not including the one that is playing)
		for (int nD = 1; nD < CommandCenter.getNumFalcons(); nD++) {
			//create x and y values for the objects to the bottom right using cartesean points again
			for (int nC = 0; nC < fal.getDegrees().length; nC++) {
				nXs[nC] = pntMs[nC].x + Game.DIM.width - (20 * nD);
				nYs[nC] = pntMs[nC].y + Game.DIM.height - 40;
			}
			g.drawPolygon(nXs, nYs, nLen);
		}
  }

   private void drawNumberShieldsRemaining(Graphics g) {
     Falcon fal = CommandCenter.getFalcon();
     int shieldsPercent = fal.getShield();
     g.setColor(Color.cyan);
     g.drawString("Shield: " + shieldsPercent, 20, Game.DIM.height - 40);
   }

   private void drawNumberExtraBullets(Graphics g) {
     Falcon fal = CommandCenter.getFalcon();
     int extraBullets = fal.getNumExtraBullets();
     String boosts = "";
     if (extraBullets > 0) {
       boosts += extraBullets * 2 + " Extra Bullets";
     } else {
       boosts = "NONE";
     }
     g.setColor(Color.red);
     g.drawString("Weapon Boost: " + boosts, 100, Game.DIM.height - 40);
   }

   private void initView() {
		Graphics g = getGraphics();			// get the graphics context for the panel
		g.setFont(fnt);						// take care of some simple font stuff
		fmt = g.getFontMetrics();
		nFontWidth = fmt.getMaxAdvance();
		nFontHeight = fmt.getHeight();
		g.setFont(fntBig);					// set font info
	}
	
	// This method draws some text to the middle of the screen before/after a game
	private void displayTextOnScreen() {

        if (CommandCenter.getScore() != 0) {
		strDisplay = "GAME OVER";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);}
        else {
            strDisplay = "ASTEROIDS";
            grpOff.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);
        }

		strDisplay = "use the LEFT, RIGHT, DOWN and UP arrow keys to turn and thrust";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 40);

		strDisplay = "PRESS the space bar to fire";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 80);

		strDisplay = "PRESS 'S' to Start";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 120);

		strDisplay = "PRESS 'P' to Pause";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 160);

		strDisplay = "PRESS 'Q' to Quit";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 200);

		strDisplay = "Shield";
        grpOff.setColor(Color.CYAN);
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 240);

        strDisplay = "ExtraBullet";
        grpOff.setColor(Color.RED);
        grpOff.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                        + nFontHeight + 280);

        strDisplay = "FireTailDebris";
        grpOff.setColor(Color.ORANGE);
        grpOff.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                        + nFontHeight + 320);

	}
	
	public GameFrame getFrm() {return this.gmf;}
	public void setFrm(GameFrame frm) {this.gmf = frm;}	
}