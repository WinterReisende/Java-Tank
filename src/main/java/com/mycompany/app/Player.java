package com.mycompany.app;

import java.awt.Rectangle;

public class Player implements Actor, Runnable {
    private ServerModel model;
    private int clientId;
    private int id;
    private int xPos;
    private int yPos;
    private int size;
    private int direction;
    private int speed;
    private int health;
    private int score;
    private int type;
    private boolean isDead;
    private boolean isMoving;
    private boolean isShooting;
    private int shootingInterval;
    private Rectangle border;

    public Rectangle getBorder() {
        return this.border;
    }

    public Player(ServerModel model, int clientId, int id, int x, int y) {
        this.model = model;
        this.clientId = clientId;
        this.id = id;
        this.xPos = x;
        this.yPos = y;
        this.size = 12;
        this.direction = 0;
        this.isMoving = false;
        this.isShooting = false;
        this.shootingInterval = 10;
        this.speed = 2;
        this.health = 3;
        this.isDead = false;
        this.score = 0;
        this.type = 1;// 1:player 2:enemy
        this.border = new Rectangle(xPos - size, yPos - size, size * 2, size * 2);
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

    public void setId(int id) {
        this.id = id;
    }

    public void setX(int x) {
        this.xPos = x;
    }

    public void setY(int y) {
        this.yPos = y;
    }

    public int getSize() {
        return this.size;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int d) {
        this.direction = d;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int s) {
        this.speed = s;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int h) {
        this.health = h;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int s) {
        this.score = s;
    }

    public void setMoving(boolean m) {
        this.isMoving = m;
    }

    public boolean getMoving() {
        return this.isMoving;
    }

    public void setShooting(boolean s) {
        this.isShooting = s;
    }

    public boolean getShooting() {
        return this.isShooting;
    }

    public int getClientId() {
        return this.clientId;
    }

    public void setClientId(int id) {
        this.clientId = id;
    }

    public void setDead(boolean d) {
        this.isDead = d;
    }

    public boolean isDead() {
        return this.isDead;
    }

    @Override
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
                if (this.isDead) {
                    continue;
                }
                if (this.isMoving) {
                    this.move();
                }
                this.shoot();
                if (model.getGameStarted() == false) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public synchronized int getType() {
        return this.type;
    }

    public synchronized void move() {
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
                // 如果碰到非子弹的物体，就停下来
                this.setMoving(false);
                // 然后把自己的位置调整回来
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
            }
        }
    }

    public void addScore(int s) {
        this.score += s;
    }

    public synchronized void shoot() {
        if (this.getShooting() && this.shootingInterval <= 0) {
            this.setShooting(false);
            this.shootingInterval = 10;
            // 然后要创建子弹
            Bullet bullet = new Bullet(model, this, -1, this.type, this.direction, this.xPos, this.yPos);
            bullet.setDirection(this.direction);
            model.addActor(bullet);
        } else {
            shootingInterval--;
        }
    }

    // 需要一个受伤函数
    public synchronized void hurt() {
        this.setHealth(this.getHealth() - 1);
        if (this.getHealth() == 0) {
            this.model.setPlayerLivingNumber(this.model.getPlayerLivingNumber() - 1);
            this.setDead(true);
        }
    }

    public String toString() {
        // 参考：info += "P" + p.getClientId() + "," + p.getX() + "," + p.getY() + "," +
        // p.getDirection() + ","
        // + p.getHealth() + "," + p.getScore() + ";";
        String info = "P" + this.getClientId() + "," + this.getX() + "," + this.getY() + "," + this.getDirection() + ","
                + this.getHealth() + "," + this.getScore() + ";";
        return info;
    }

    //根据String重新设置player的信息
    public void setInfo(String s) {
        //s = s.substring(1, s.length());
        String[] info = s.split(",");
        this.setClientId(Integer.parseInt(info[0].substring(1)));
        this.setX(Integer.parseInt(info[1]));
        this.setY(Integer.parseInt(info[2]));
        this.setDirection(Integer.parseInt(info[3]));
        this.setHealth(Integer.parseInt(info[4]));
        this.setScore(Integer.parseInt(info[5]));
    }

    public void responseInput(boolean up, boolean down, boolean left, boolean right, boolean shoot) {
        System.out
                .println("Player:up:" + up + " down:" + down + " left:" + left + " right:" + right + " shoot:" + shoot);
        if (up) {
            this.setDirection(0);
            this.setMoving(true);
        } else if (down) {
            this.setDirection(2);
            this.setMoving(true);
        } else if (left) {
            this.setDirection(3);
            this.setMoving(true);
        } else if (right) {
            this.setDirection(1);
            this.setMoving(true);
        } else {
            this.setMoving(false);
        }
        if (shoot) {
            this.setShooting(true);
        } else {
            this.setShooting(false);
        }
        System.out.println("Player:direction:" + this.getDirection() + " moving:" + this.getMoving() + " shooting:"
                + this.getShooting());
    }

}
