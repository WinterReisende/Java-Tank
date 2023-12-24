package com.mycompany.app;

import java.awt.Rectangle;

public class Enemy implements Actor, Runnable {
    private ServerModel model;
    private int id;
    private int enemyId;
    private int xPos;
    private int yPos;
    private int size;
    private int type;
    private int direction;
    private int speed;
    private Actor owner;
    private Rectangle border;
    private boolean isDead;
    private int shootingInterval;
    private int turningInterval;
    private int health;
    private EnemyCreater creater;

    public Rectangle getBorder() {
        return this.border;
    }

    public int getType() {
        return this.type;
    }

    public int getId() {
        return this.id;
    }

    public int getX() {
        return this.xPos;
    }

    public int getY() {
        return this.yPos;
    }

    public int getDirection() {
        return this.direction;
    }

    public Actor getOwner() {
        return this.owner;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setX(int x) {
        this.xPos = x;
    }

    public void setY(int y) {
        this.yPos = y;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }

    public boolean getDead() {
        return this.isDead;
    }

    public void setShootingInterval(int shootingInterval) {
        this.shootingInterval = shootingInterval;
    }

    public int getShootingInterval() {
        return this.shootingInterval;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return this.health;
    }

    public void setOwner(Actor owner) {
        this.owner = owner;
    }

    public void setBorder(Rectangle border) {
        this.border = border;
    }

    public void setEnemyId(int enemyId) {
        this.enemyId = enemyId;
    }

    public int getEnemyId() {
        return this.enemyId;
    }

    public Enemy(ServerModel model, int enemyId, int id, int x, int y) {
        this.model = model;
        this.creater = model.getEnemyCreater();
        this.enemyId = enemyId;
        this.id = id;
        this.xPos = x;
        this.yPos = y;
        this.size = 12;
        // 用一个随机数作为方向
        this.direction = (int) (Math.random() * 3) + 1;
        this.owner = null;
        // 用一个随机数作为射击间隔
        this.shootingInterval = (int) (Math.random() * 32) + 58;
        // 用一个随机数作为转向间隔
        this.turningInterval = (int) (Math.random() * 52) + 28;
        this.speed = 2;
        this.health = this.enemyId;
        this.type = 2;// 1:player 2:enemy
        this.border = new Rectangle(xPos - size, yPos - size, size * 2, size * 2);
    }

    public void run() {
        try {
            long previousTime = System.currentTimeMillis();
            while (true) {
                long currentTime = System.currentTimeMillis();
                long timeDiff = currentTime - previousTime;
                long sleep = 33 - timeDiff;
                if (sleep < 0) {
                    sleep = 0;
                }
                Thread.sleep(sleep);
                previousTime = System.currentTimeMillis();
                this.move();
                this.shoot();
                this.turn();
                if (this.getDead()) {
                    break;
                }
                if (model.getGameStarted() == false) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void move() {
        this.turn();
        if (this.direction == 0) {
            this.yPos -= this.speed;
        } else if (this.direction == 1) {
            this.xPos += this.speed;
        } else if (this.direction == 2) {
            this.yPos += this.speed;
        } else if (this.direction == 3) {
            this.xPos -= this.speed;
        }
        this.border.setLocation(this.xPos - size, this.yPos - size);
        this.checkCollision();
    }

    public synchronized void checkCollision() {
        Actor s = this.model.checkCollison(this);
        if (s != null) {
            if (!s.getClass().getName().equals("com.mycompany.app.Bullet")) {
                // 如果碰到非子弹的物体，就把自己的位置调整回来
                if (this.direction == 0) {
                    this.yPos += this.speed;
                } else if (this.direction == 1) {
                    this.xPos -= this.speed;
                } else if (this.direction == 2) {
                    this.yPos -= this.speed;
                } else if (this.direction == 3) {
                    this.xPos += this.speed;
                }
                this.border.setLocation(this.xPos - size, this.yPos - size);
                // 然后顺时针转向
                this.direction = (this.direction + 1) % 4;
            }
        }
    }

    public synchronized void shoot() {
        if (this.shootingInterval <= 0) {
            Bullet b = new Bullet(this.model, this, -1, this.type, this.direction, this.xPos, this.yPos);
            this.model.addActor(b);
            this.shootingInterval = (int) (Math.random() * 32) + 58;
        } else {
            this.shootingInterval--;
        }
    }

    // 到达间隔则随机转向
    public synchronized void turn() {
        if (this.turningInterval == 0) {
            this.direction = (int) (Math.random() * 4);
            assert (this.direction >= 0 && this.direction <= 3);
            this.turningInterval = (int) (Math.random() * 102) + 28;
        } else {
            this.turningInterval--;
        }
    }

    // 受伤函数，生命值归零则死亡
    public synchronized void hurt() {
        this.health--;
        if (this.health == 0) {
            this.creater.setLeftEnemy(this.creater.getLeftEnemy() - 1);
            this.setDead(true);
            this.model.deleteActor(this);// 这个可以考虑，是留下与不留下尸体的区别
        }
    }

    public String toString() {
        String info = "E" + this.enemyId + "," + this.xPos + "," + this.yPos + "," + this.direction + ","
                + this.health + ";";
        return info;
    }

    // 根据String重新设置enemy的信息
    public void setInfo(String s) {
        String[] info = s.split(",");
        this.setEnemyId(Integer.parseInt(info[0].substring(1)));
        this.setX(Integer.parseInt(info[1]));
        this.setY(Integer.parseInt(info[2]));
        this.setDirection(Integer.parseInt(info[3]));
        this.setHealth(Integer.parseInt(info[4]));
    }
}
