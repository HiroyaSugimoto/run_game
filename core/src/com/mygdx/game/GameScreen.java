package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    final RunGame game;

    Texture playerImage;
    Texture obstacleImage;
    OrthographicCamera camera;
    Viewport viewport;
    Rectangle player;

    Array<Rectangle> obstacles;
    long lastObst;

    int obstaclePos;

    /*
    Array<Rectangle> leftObsts;
    Array<Rectangle> centerObsts;
    Array<Rectangle> rightObsts;

    long leftLastTime;
    long centerLastTime;
    long rightLastTime;
    */

    public GameScreen(final RunGame gam) {
        this.game = gam;

        //64×64ピクセルのプレイヤーと障害物の画像をロード
        playerImage = new Texture(Gdx.files.internal("player.png"));
        obstacleImage = new Texture(Gdx.files.internal("Obstacle.png"));

        //カメラとSpriteBatchを生成
        camera = new OrthographicCamera(192, 384);
        viewport = new FitViewport(192, 384, camera);
        camera.setToOrtho(false, 192, 384);

        //プレイヤーを表すRectangleを生成
        player = new Rectangle();
        player.x = 64;
        player.y = 10;
        player.width = 64;
        player.height = 64;

        obstacles = new Array<Rectangle>();
        spawnObstacle();

        //障害物のArrayListを作成し、最初の障害物をスポーンする
        /*
        leftObsts = new Array<Rectangle>();
        spawnLeftObst();
        centerObsts = new Array<Rectangle>();
        spawnCenterObst();
        rightObsts = new Array<Rectangle>();
        spawnRightObst();
        */

    }

    private void spawnObstacle() {
        Rectangle obstacle = new Rectangle();
        obstacle.x = obstaclePos;
        obstacle.y = 384;
        obstacle.width = 64;
        obstacle.height = 64;
        obstacles.add(obstacle);
        lastObst = TimeUtils.nanoTime();

    }

    /*
    private void spawnLeftObst() {
        Rectangle leftObst = new Rectangle();
        leftObst.x = 0;
        leftObst.y = 1000;
        leftObst.width = 64;
        leftObst.height = 64;
        leftObsts.add(leftObst);
        leftLastTime = TimeUtils.nanoTime();
    }

    private void spawnCenterObst() {
        Rectangle centerObst = new Rectangle();
        centerObst.x = 64;
        centerObst.y = 384;
        centerObst.width = 64;
        centerObst.height = 64;
        centerObsts.add(centerObst);
        centerLastTime = TimeUtils.nanoTime();
    }

    private void spawnRightObst() {
        Rectangle rightObst = new Rectangle();
        rightObst.x = 128;
        rightObst.y = 700;
        rightObst.width = 64;
        rightObst.height = 64;
        rightObsts.add(rightObst);
        rightLastTime = TimeUtils.nanoTime();
    }
    */

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        //SpriteBatchにcameraによって指定された座標系でレンダリングするよう指示
        game.batch.setProjectionMatrix(camera.combined);

        //batchの生成開始とプレイヤーと障害物の画像を表示
        game.batch.begin();
        game.batch.draw(playerImage, player.x, player.y);

        for(Rectangle obstacle : obstacles) {
            if(MathUtils.randomBoolean()) {
                game.batch.draw(obstacleImage, obstacle.x = 0, obstacle.y);
            } else if(MathUtils.randomBoolean()) {
                game.batch.draw(obstacleImage, obstacle.x = 64, obstacle.y);
            } else {
                game.batch.draw(obstacleImage, obstacle.x = 128, obstacle.y);
            }
        }
        game.batch.end();

        /*
        for(Rectangle obstacle : leftObsts) {
            game.batch.draw(obstacleImage, obstacle.x, obstacle.y);
        }
        for(Rectangle obstacle : centerObsts) {
            game.batch.draw(obstacleImage, obstacle.x, obstacle.y);
        }
        for(Rectangle obstacle : rightObsts) {
            game.batch.draw(obstacleImage, obstacle.x, obstacle.y);
        }
        */

        //ユーザーのキー入力処理
        if(Gdx.input.isKeyJustPressed(Keys.LEFT))
            player.x -= 64;
        if(Gdx.input.isKeyJustPressed(Keys.RIGHT))
            player.x += 64;

        //playerを画面外に出さないための処理
        if(player.x < 0)
            player.x = 0;
        if(player.x > 192 - 64)
            player.x = 192 - 64;

        //障害物の生成が必要かチェックして生成
        if(TimeUtils.nanoTime() - lastObst > 2000000000) {
            spawnObstacle();
        }


        Iterator<Rectangle> iter = obstacles.iterator();
        while(iter.hasNext()) {
            Rectangle obstacle = iter.next();
            obstacle.y -= 200 * Gdx.graphics.getDeltaTime();
            if(obstacle.y + 64 < 0)
                iter.remove();
        }

        /*
        long nextTime = MathUtils.random(2000000000, 2000000000);

        if(TimeUtils.nanoTime() - leftLastTime > nextTime)
            spawnLeftObst();
        if(TimeUtils.nanoTime() - centerLastTime > nextTime)
            spawnCenterObst();
        if(TimeUtils.nanoTime() - rightLastTime > nextTime)
            spawnRightObst();

        Iterator<Rectangle> leftIter = leftObsts.iterator();
        while(leftIter.hasNext()) {
            Rectangle obstract = leftIter.next();
            obstract.y -= 200 * Gdx.graphics.getDeltaTime();
            if(obstract.y + 64 < 0)
                leftIter.remove();
        }

        Iterator<Rectangle> centerIter = centerObsts.iterator();
        while(centerIter.hasNext()) {
            Rectangle obstract = centerIter.next();
            obstract.y -= 200 * Gdx.graphics.getDeltaTime();
            if(obstract.y + 64 < 0)
                centerIter.remove();
        }

        Iterator<Rectangle> rightIter = rightObsts.iterator();
        while(rightIter.hasNext()) {
            Rectangle obstract = rightIter.next();
            obstract.y -= 200 * Gdx.graphics.getDeltaTime();
            if(obstract.y + 64 < 0)
                rightIter.remove();
        }
        */

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        playerImage.dispose();
        obstacleImage.dispose();
    }
}


