package com.mycompany.app;

import java.awt.image.BufferedImage;

public class Enemy implements Actor {
    private int xPos;
    private int yPos;
    private int EnemyId;
    private int health;
    private ClientModel gameModel;
    private int direction;
    private BufferedImage[][] enemy;;

    public Enemy(int EnemyID, int xPos, int yPos, int direction, int health, ClientModel gameModel) {
        this.EnemyId = EnemyID;
        this.xPos = xPos;
        this.yPos = yPos;
        this.gameModel = gameModel;
        this.direction = direction;
        this.health = health;
        this.enemy = new BufferedImage[2][4];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                this.enemy[i][j] = gameModel.getTexture(2 + i * 8 + j);
            }
        }
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void draw(java.awt.Graphics g) {
        if (direction == 0)
            g.drawImage(enemy[EnemyId - 1][0], xPos - 12, yPos - 12, 25, 25, null);
        if (direction == 1)
            g.drawImage(enemy[EnemyId - 1][3], xPos - 12, yPos - 12, 25, 25, null);
        if (direction == 2)
            g.drawImage(enemy[EnemyId - 1][1], xPos - 12, yPos - 12, 25, 25, null);
        if (direction == 3)
            g.drawImage(enemy[EnemyId - 1][2], xPos - 12, yPos - 12, 25, 25, null);
    }

}
