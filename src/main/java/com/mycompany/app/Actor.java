package com.mycompany.app;

import java.awt.*;

public interface Actor {
    public Rectangle getBorder();

    public int getType();

    public int getId();

    public int getX();

    public int getY();

    public int getDirection();

    public void setId(int id);

    public void setX(int x);

    public void setY(int y);

    public void setDirection(int direction);
}
