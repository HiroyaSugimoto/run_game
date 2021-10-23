package com.mygdx.game.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Obstacle {

    static final float SIZE = 64.0f;

    Vector2 position = new Vector2();
    Rectangle bounds = new Rectangle();

    public Obstacle(Vector2 pos) {
        this.position = pos;
        this.bounds.width = SIZE;
        this.bounds.height = SIZE;
    }
}
