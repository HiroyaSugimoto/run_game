package com.mygdx.game;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
    long lastObstTime;

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
    }

    private void spawnObstacle() {
        Rectangle obstacle = new Rectangle();

        //x軸は指定した3箇所からランダムに取得
        int[] i = {0, 64, 128};
        Random r = new Random();
        int xPos = i[r.nextInt(3)];
        obstacle.x = xPos;

        obstacle.y = 384;
        obstacle.width = 64;
        obstacle.height = 64;
        obstacles.add(obstacle);
        lastObstTime = TimeUtils.nanoTime();
    }

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
            game.batch.draw(obstacleImage, obstacle.x, obstacle.y);
        }

        game.batch.end();

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
        if(TimeUtils.nanoTime() - lastObstTime > 500000000) {
            spawnObstacle();
        }

        Iterator<Rectangle> iter = obstacles.iterator();
        while(iter.hasNext()) {
            Rectangle obstacle = iter.next();
            obstacle.y -= 600 * Gdx.graphics.getDeltaTime();
            if(obstacle.y + 64 < 0)
                iter.remove();
            if(obstacle.overlaps(player)) {
                game.setScreen(new GameOverScreen(game));
                dispose();
            }
        }

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


