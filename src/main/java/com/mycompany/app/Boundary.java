package com.mycompany.app;

import java.awt.*;

public class Boundary implements Actor {
    private ServerModel model;
    private int id;
    private int xPos;
    private int yPos;
    private int size;
    private int type;
    private int direction;
    private Actor owner;
    private Rectangle border;

    public synchronized Rectangle getBorder() {
        return this.border;
    }

    public synchronized int getType() {
        return this.type;
    }

    public synchronized int getId() {
        return this.id;
    }

    public synchronized int getX() {
        return this.xPos;
    }

    public synchronized int getY() {
        return this.yPos;
    }

    public synchronized int getDirection() {
        return this.direction;
    }

    public synchronized Actor getOwner() {
        return this.owner;
    }

    public synchronized void setId(int id) {
        this.id = id;
    }

    public synchronized void setX(int x) {
        this.xPos = x;
    }

    public synchronized void setY(int y) {
        this.yPos = y;
    }

    public synchronized void setDirection(int direction) {
        this.direction = direction;
    }

    public synchronized void setOwner(Actor owner) {
        this.owner = owner;
    }

    public synchronized void setBorder(Rectangle border) {
        this.border = border;
    }

    public synchronized void setType(int type) {
        this.type = type;
    }

    public Boundary(ServerModel model, int id, int x, int y) {
        this.model = model;
        this.owner = null;
        this.id = id;
        this.type = 0;
        this.xPos = x;
        this.yPos = y;
        this.size = 12;
        this.border = new Rectangle(xPos - size, yPos - size, size * 2, size * 2);
    }

}
