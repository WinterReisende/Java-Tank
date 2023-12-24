package com.mycompany.app;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/*import javafx.scene.image.Image;
import javafx.scene.paint.Color;*/

public class Wall implements Actor {
    public int xPos;
    public int yPos;
    public BufferedImage wall;
    public ClientModel gameModel;
    public String Type = "wall";

    public Wall(int xPos, int yPos, ClientModel gameModel) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.gameModel = gameModel;
        this.wall = gameModel.getTexture(70);
    }

    public void draw(Graphics g) {
        // g.setColor(Color.lightGray);
        // g.fillRect(xPos - 10, yPos - 10, 20, 20);
        g.drawImage(wall, xPos - 12, yPos - 12, 25, 25, null);
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public String getType() {
        return Type;
    }

}
