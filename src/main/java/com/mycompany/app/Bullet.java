package com.mycompany.app;

import java.awt.Rectangle;

public class Bullet implements Actor, Runnable {
    private ServerModel model;
    private int id;
    private int xPos;
    private int yPos;
    private int size;
    private int type;
    private int direction;
    private int speed;
    private Actor owner;
    private Rectangle border;
    private boolean isDead;

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

    public Bullet(ServerModel model, Actor actor, int id, int type, int direction, int x, int y) {
        this.model = model;
        this.id = id;
        this.xPos = x;
        this.yPos = y;
        this.owner = actor;
        this.type = type;
        this.size = 2;
        this.direction = direction;
        this.speed = 4;
        this.isDead = false;
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
        if (s == null) {
            ;
        } else {
            // 这里需要先检查s的类名信息，然后检查type为1或2，相同为友军，不同为敌人
            // 如果是墙，就直接消失
            // 如果是玩家，就扣血
            // 如果是子弹，就消失
            // 如果是道具，就消失
            // 如果是边界，就消失
            // 如果是敌人，就扣血
            // 没有友伤
            if (s.getClass().getName().equals("com.mycompany.app.Wall")) {
                this.model.deleteActor(this);
                this.setDead(true);
            } else if (s.getClass().getName().equals("com.mycompany.app.Player")) {
                if (this.type == 2) {
                    this.model.deleteActor(this);
                    Player p = (Player) s;
                    p.hurt();
                    this.setDead(true);
                }
            } else if (s.getClass().getName().equals("com.mycompany.app.Bullet")) {
                this.model.deleteActor(this);
                this.model.deleteActor(s);
                this.setDead(true);
            } else if (s.getClass().getName().equals("com.mycompany.app.Prop")) {
                this.model.deleteActor(this);
                this.setDead(true);
            } else if (s.getClass().getName().equals("com.mycompany.app.Boundary")) {
                this.model.deleteActor(this);
                this.setDead(true);
            } else if (s.getClass().getName().equals("com.mycompany.app.Enemy")) {
                if (this.type == 1) {
                    this.model.deleteActor(this);
                    Enemy e = (Enemy) s;
                    int score = e.getEnemyId() * 5;
                    ((Player) this.owner).addScore(score);
                    e.hurt();
                    this.setDead(true);
                }
            }
        }
    }

    public String toString() {
        String info = "B" + this.xPos + "," + this.yPos + "," + this.direction + ";";
        return info;
    }

    // 根据传入的String来设置bullet的信息
    public void setBullet(String s) {
        String[] info = s.split(",");
        this.xPos = Integer.parseInt(info[0].substring(1));
        this.yPos = Integer.parseInt(info[1]);
        this.direction = Integer.parseInt(info[2]);
    }
}
