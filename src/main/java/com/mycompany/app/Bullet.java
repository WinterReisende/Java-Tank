package com.mycompany.app;

import java.awt.*;

public class Bullet implements Actor{
	private int xPos;
	private int yPos;
	private ClientModel gameModel;
	private int direction;

	public Bullet(int xPos, int yPos,  int direction, ClientModel gameModel){
		this.xPos = xPos;
		this.yPos = yPos;
		this.gameModel = gameModel;
		this.direction = direction;
	}

	public void draw(Graphics g){
		g.setColor(Color.lightGray);
		if(direction == 0 || direction == 2)
			g.fillRect(xPos - 1,yPos - 4, 3, 9);
		if(direction == 1 || direction == 3)
			g.fillRect(xPos - 4,  yPos - 1, 9, 3);
	}

	public int getxPos(){
		return xPos;
	}

	public int getyPos(){
		return yPos;
	}
}

