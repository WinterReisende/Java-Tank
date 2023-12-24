package com.mycompany.app;

import java.awt.image.BufferedImage;

public class Player implements Actor {
    private int xPos;
    private int yPos;
    private int ClientID;
    private int health;
    private int score;
    private ClientModel gameModel;
    private int direction;
    private BufferedImage[][] player;;

    public Player(int ClientID, int xPos, int yPos, int direction, int health, int score, ClientModel gameModel) {
        this.ClientID = ClientID;
        this.xPos = xPos;
        this.yPos = yPos;
        this.gameModel = gameModel;
        this.direction = direction;
        this.health = health;
        this.score = score;
        this.player = new BufferedImage[5][4];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                this.player[i][j] = gameModel.getTexture(10 + i * 4 + j);
            }
        }
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public int getClientId() {
        return ClientID;
    }

    public int getHealth() {
        return health;
    }

    public int getScore() {
        return score;
    }

    public BufferedImage getMainImage() {
        return player[ClientID][0];
    }

    public void draw(java.awt.Graphics g) {
        if (direction == 0)
            g.drawImage(player[ClientID][0], xPos - 12, yPos - 12, 25, 25, null);
        if (direction == 1)
            g.drawImage(player[ClientID][3], xPos - 12, yPos - 12, 25, 25, null);
        if (direction == 2)
            g.drawImage(player[ClientID][1], xPos - 12, yPos - 12, 25, 25, null);
        if (direction == 3)
            g.drawImage(player[ClientID][2], xPos - 12, yPos - 12, 25, 25, null);
    }

}
