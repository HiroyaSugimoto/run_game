package com.mygdx.game.model;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends ApplicationAdapter {

    private SpriteBatch batch;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private FPSLogger logger;

    @Override
    public void create() {
        batch = new SpriteBatch();

        TextureRegion texture1 = new TextureRegion(new Texture("player1.png"));
        TextureRegion texture2 = new TextureRegion(new Texture("player2.png"));
        TextureRegion texture3 = new TextureRegion(new Texture("player3.png"));
        TextureRegion texture4 = new TextureRegion(new Texture("player4.png"));
        TextureRegion texture5 = new TextureRegion(new Texture("player5.png"));
        TextureRegion texture6 = new TextureRegion(new Texture("player6.png"));

        animation = new Animation<TextureRegion>(0.1f, texture1, texture2, texture3, texture4, texture5, texture6);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        stateTime = 0.0f;

        logger = new FPSLogger();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += Gdx.graphics.getDeltaTime();

        batch.begin();
        batch.draw(animation.getKeyFrame(stateTime), 10, 10);
        batch.end();

        logger.log();
    }



}
