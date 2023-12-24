package com.mycompany.app;

import java.awt.*;
import javax.swing.*;

//the drawing panel class belong to the client program
public class DrawingPanel extends JPanel {
	private Image offScreenImage;

	private boolean gameStarted;
	private int playerNumber;
	private int[] Pnid, Pnaddressx, Pnaddressy;

	private long lastCallTime;

	public DrawingPanel() {
		// P1Image = Toolkit.getDefaultToolkit().getImage("image/" + 55 + ".jpg");
		// P2Image = Toolkit.getDefaultToolkit().getImage("image/" + 73 + ".jpg");
		// xs = 0;
		playerNumber = 1;
		Pnid = new int[5];
		Pnaddressx = new int[5];
		Pnaddressy = new int[5];
		lastCallTime = System.currentTimeMillis();
		offScreenImage = createImage(getPreferredSize().width, getPreferredSize().height);
	}

	public void paintComponent(Graphics g) {
		if (offScreenImage == null) {
			offScreenImage = createImage(this.getWidth(), this.getHeight());
		}
		Graphics offScreenGraphics;
		/*
		 * if (offScreenImage == null) {
		 * offScreenImage = createImage(640, 550);
		 * }
		 */
		offScreenGraphics = offScreenImage.getGraphics();
		myPaint(offScreenGraphics);
		g.drawImage(offScreenImage, 0, 0, this);
		// g.drawImage(offScreenImage, 0, 0, this);

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

	public synchronized void setPlayerInfo(int[] id, int[] x, int[] y) {
		for (int i = 0; i < playerNumber; i++) {
			this.Pnid[i] = id[i];
			this.Pnaddressx[i] = x[i];
			this.Pnaddressy[i] = y[i];
		}
	}

	public synchronized void setPlayerAddress(int id, int x, int y) {
		this.Pnid[id] = id;
		this.Pnaddressx[id] = x;
		this.Pnaddressy[id] = y;

	}

	public synchronized void deletePlayer(int id) {
		this.Pnid[id] = -1;
		this.Pnaddressx[id] = -1;
		this.Pnaddressy[id] = -1;
	}
}