package com.mycompany.app;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class DrawingPanel extends JPanel {
	private Image offScreenImage;
	private ClientModel model;
	private boolean gameStarted;
	private int state;

	public DrawingPanel() {
		model = null;
		state = 0;
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
		if (state == 0) {
			if (gameStarted) {
				System.out.println("just try to repaint");
				g.setColor(Color.blue);
				g.drawRect(1, 1, 501, 601);

				// 根据model的drawingList画图
				for (int i = 0; i < 400; i++) {
					Actor actor = model.getActor(i);
					if (actor != null) {
						actor.draw(g);
					}
				}
				for (int i = 0; i < 100; i++) {
					Actor actor = model.getActor2(i);
					if (actor != null) {
						actor.draw(g);
						if (actor.getClass().getName().equals("com.mycompany.app.Player")) {
							Player p = (Player) actor;
							BufferedImage plyerImage = p.getMainImage();
							g.drawImage(plyerImage, 550, 95 + p.getClientId() * 100, 25, 25, null);
							g.drawString("Health: " + p.getHealth(), 600,
									100 + p.getClientId() * 100);
							g.drawString("Score:  " + p.getScore(), 600,
									120 + p.getClientId() * 100);
						}
					}
				}
			}
		} else if (state == 1) {
			g.setColor(Color.black);
			g.fillRect(0, 0, 640, 550);
			g.setColor(Color.white);
			g.drawString("You Win!", 300, 300);
			this.gameStarted = false;
		} else if (state == 2) {
			g.setColor(Color.black);
			g.fillRect(0, 0, 640, 550);
			g.setColor(Color.white);
			g.drawString("You Lose!", 300, 300);
			this.gameStarted = false;
		}

	}

	public void setModel(ClientModel model) {
		this.model = model;
	}

	public void setgameStart(boolean c) {
		this.gameStarted = c;
	}

	public void setState(int state) {
		this.state = state;
	}
}