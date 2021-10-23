package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.model.Player;

public class GameScreen implements Screen {
    final RunGame game;
    Player play;

    Texture playerImage;
    Texture obstacleImage;
    Texture goalImage;
    OrthographicCamera camera;
    Viewport viewport;
    Rectangle player;
    Rectangle goal;
    Array<Rectangle> obstacles;
    long lastObstTime;
    int obstCount = 0; //障害物が生成された回数
    int gameCleaNumber = 30; //obstCountが幾つになったらゴールが表示されるかの設定値

    //
    //private SpriteBatch batch;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private FPSLogger logger;
    //

    public GameScreen(final RunGame gam) {
        this.game = gam;

        //
        //batch = new SpriteBatch();

        TextureRegion texture1 = new TextureRegion(new Texture("player1.png"));
        TextureRegion texture2 = new TextureRegion(new Texture("player2.png"));
        TextureRegion texture3 = new TextureRegion(new Texture("player3.png"));
        TextureRegion texture4 = new TextureRegion(new Texture("player4.png"));
        TextureRegion texture5 = new TextureRegion(new Texture("player5.png"));
        TextureRegion texture6 = new TextureRegion(new Texture("player6.png"));

        animation = new Animation<TextureRegion>(0.05f, texture1, texture2, texture3, texture4, texture5, texture6);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        stateTime = 0.0f;

        logger = new FPSLogger();
        //

        //使用する画像をロード
        playerImage = new Texture(Gdx.files.internal("player.png"));
        obstacleImage = new Texture(Gdx.files.internal("Obstacle.png"));
        goalImage = new Texture(Gdx.files.internal("goal.png"));

        //カメラとSpriteBatchを生成
        camera = new OrthographicCamera(192, 384);
        viewport = new FitViewport(192, 384, camera);
        camera.setToOrtho(false, 192, 384);

        //プレイヤーのRectangleを生成
        player = new Rectangle();
        player.x = 64;
        player.y = 10;
        player.width = 64;
        player.height = 64;

        //ゴールのRectangleを生成
        goal = new Rectangle();
        goal.x = 0;
        goal.y = 768;
        goal.width = 192;
        goal.height = 16;

        obstacles = new Array<Rectangle>();
        spawnObstacle();
    }

    private void spawnObstacle() {
        Rectangle obstacle = new Rectangle();
        //x軸は0,64,128の3箇所からランダムに取得
        obstacle.x = MathUtils.random.nextInt(3) * 64;
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

        //
        stateTime += Gdx.graphics.getDeltaTime();
        //

        //SpriteBatchにcameraによって指定された座標系でレンダリングするよう指示
        game.batch.setProjectionMatrix(camera.combined);

        //各batchを配置
        game.batch.begin();
        game.batch.draw(playerImage, player.x, player.y); //プレイヤー

        //障害物を繰り返し生成
        for(Rectangle obstacle : obstacles) {
            game.batch.draw(obstacleImage, obstacle.x, obstacle.y);
        }

        game.font.draw(game.batch, obstCount + " m", 5, 379); //画面左上に現在の進行状況を表示
        game.batch.draw(goalImage, goal.x, goal.y); //ゴール
        //
        game.batch.draw(animation.getKeyFrame(stateTime), 10, 10);
        //
        game.batch.end();

        //
        logger.log();
        //

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
        if(TimeUtils.nanoTime() - lastObstTime > 500000000 && obstCount < gameCleaNumber) {
            spawnObstacle();
            obstCount++;
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

        if(obstCount >= gameCleaNumber) {
            goal.y -= 300 * Gdx.graphics.getDeltaTime();
            if(goal.y + 18 < 0) {
                game.setScreen(new GameClearScreen(game));
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


