package com.mycompany.app;

import java.awt.*;
import javax.swing.*;

//the drawing panel class belong to the client program
public class DrawingPanel extends JPanel {
	private Image offScreenImage;

	private boolean gameStarted;
	private int playerNumber;

	private long lastCallTime;

	public DrawingPanel() {
		playerNumber = 1;
		lastCallTime = System.currentTimeMillis();
		offScreenImage = createImage(getPreferredSize().width, getPreferredSize().height);
	}

	public void paintComponent(Graphics g) {
		if (offScreenImage == null) {
			offScreenImage = createImage(this.getWidth(), this.getHeight());
		}
		Graphics offScreenGraphics;
		offScreenGraphics = offScreenImage.getGraphics();
		myPaint(offScreenGraphics);
		g.drawImage(offScreenImage, 0, 0, this);
	}

	public void myPaint(Graphics g) {
		super.paintComponent(g);

		if (gameStarted) {
			long currentTime = System.currentTimeMillis();
			long elapsedTime = currentTime - lastCallTime;
			// draw game information
			g.setColor(new Color(81, 111, 230));
			g.drawString("refresh interval: " + elapsedTime, 527, 39);
			lastCallTime = currentTime;
		}
	}

	public synchronized void setgameStart(boolean c) {
		this.gameStarted = c;
	}

	public synchronized void setPlayerNumber(int n) {
		this.playerNumber = n;
	}

}