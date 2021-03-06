package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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

public class GameScreen implements Screen {
    final RunGame game;

    boolean playerPos = false; //プレイヤーがゲーム開始位置にいるかどうか

    int gameSpeed = 600; //スクロールの速さ（毎秒何ピクセルかを設定）
    int obstSpan = 500000000; //障害物の生成スパンをナノ秒単位で設定

    //カメラ関連の変数
    OrthographicCamera camera;
    Viewport viewport;

    //操作キャラクター関連の変数
    Rectangle player;
    Animation<TextureRegion> playerAnime;

    //ゴール関連の変数
    Texture goalImage;
    Rectangle goal;

    //背景関連の変数
    Texture backgroundImage;
    Array<Rectangle> backgrounds;
    float backgroundPos;

    //障害物関連の変数
    Array<Rectangle> obstacles;
    Animation<TextureRegion> obstAnime;
    long lastObstTime;
    int obstCount = 0; //障害物がdisposeされた回数
    int gameCleaNumber = 30; //obstCountが幾つになったらゴールが表示されるかの設定値

    //スタート時の文関連の変数
    Rectangle startWord;
    Animation<TextureRegion> startAnime;

    //アニメーション関連の変数
    float stateTime; //アニメーション進度の管理用
    FPSLogger logger; //FPS計測用

    //音関連の変数
    Music runMusic; //足音
    Sound missSound; //衝突音

    public GameScreen(final RunGame gam) {
        this.game = gam;

        //プレイヤーキャラクターのアニメーション用画像をロード
        TextureRegion player1 = new TextureRegion(new Texture("player1.png"));
        TextureRegion player2 = new TextureRegion(new Texture("player2.png"));
        TextureRegion player3 = new TextureRegion(new Texture("player3.png"));
        TextureRegion player4 = new TextureRegion(new Texture("player4.png"));
        TextureRegion player5 = new TextureRegion(new Texture("player5.png"));
        TextureRegion player6 = new TextureRegion(new Texture("player6.png"));

        //1〜6の画像をアニメーションに割り当て、1フレームあたり0.05秒で再生
        playerAnime = new Animation<TextureRegion>(0.05f, player1, player2, player3, player4, player5, player6);
        playerAnime.setPlayMode(Animation.PlayMode.LOOP); //アニメーションをループ再生

        //障害物のアニメーションを設定
        TextureRegion obstacle1 = new TextureRegion(new Texture("obstacle1.png"));
        TextureRegion obstacle2 = new TextureRegion(new Texture("obstacle2.png"));
        obstAnime = new Animation<TextureRegion>(0.25f, obstacle1, obstacle2);
        obstAnime.setPlayMode(Animation.PlayMode.LOOP);

        //スタート時の文字のアニメーション設定
        TextureRegion ready = new TextureRegion(new Texture("ready.png"));
        TextureRegion go = new TextureRegion(new Texture("go.png"));
        startAnime = new Animation<TextureRegion>(1.0f, ready, go);
        startAnime.setPlayMode(Animation.PlayMode.NORMAL);

        stateTime = 0.0f; //アニメーション用の進行時間
        logger = new FPSLogger();

        //背景とゴールの画像をロード
        backgroundImage = new Texture(Gdx.files.internal("background.png"));
        goalImage = new Texture(Gdx.files.internal("goal.png"));

        //音素材をロード
        runMusic = Gdx.audio.newMusic(Gdx.files.internal("run_se.mp3"));
        missSound = Gdx.audio.newSound(Gdx.files.internal("miss_se.mp3"));

        //足音をループ再生
        runMusic.setLooping(true);
        runMusic.play();

        //カメラで描画する座標を設定
        camera = new OrthographicCamera(192, 384);
        viewport = new FitViewport(192, 384, camera);
        camera.setToOrtho(false, 192, 384);

        //プレイヤー(playerAnime)用のRectangleを設定
        player = new Rectangle();
        player.x = 64;
        player.y = -64;
        player.width = 64;
        player.height = 32;

        //ゴール用のRectangleを設定
        goal = new Rectangle();
        goal.x = 0;
        goal.y = 1152;
        goal.width = 192;
        goal.height = 16;

        //スタート時の文字用Rectangleを設定
        startWord = new Rectangle();
        startWord.x = 0;
        startWord.y = 192;
        startWord.width = 192;
        startWord.height = 64;

        //障害物用のRectangleリスト
        obstacles = new Array<Rectangle>();
        spawnObstacle();

        //背景用のRectangleリスト
        backgrounds = new Array<Rectangle>();
        spawnBackground();

    }

    private void spawnObstacle() {
        Rectangle obstacle = new Rectangle();

        //x軸は0,64,128の3箇所からランダムに取得
        obstacle.x = MathUtils.random.nextInt(3) * 64;
        obstacle.y = 768;
        obstacle.width = 64;
        obstacle.height = 64;
        obstacles.add(obstacle);
        lastObstTime = TimeUtils.nanoTime();
    }

    private void spawnBackground() {
        Rectangle background = new Rectangle();

        background.x = 0;
        background.y = 0;
        background.width = 192;
        background.height = 768;
        backgrounds.add(background);
        backgroundPos = background.y;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update(); //カメラ更新処理
        logger.log(); //コンソールにFPS値を出力

        stateTime += Gdx.graphics.getDeltaTime(); //アニメーションの前フレームからの経過時間を進行時間に加算

        //SpriteBatchにcameraによって指定された座標系でレンダリングするよう指示
        game.batch.setProjectionMatrix(camera.combined);

        //各batchを配置
        game.batch.begin();

        //背景を繰り返し生成
        for (Rectangle background : backgrounds) {
            game.batch.draw(backgroundImage, background.x, background.y);
        }

        //ゴール
        game.batch.draw(goalImage, goal.x, goal.y);

        //障害物を繰り返し生成
        for (Rectangle obstacle : obstacles) {
            game.batch.draw(obstAnime.getKeyFrame(stateTime), obstacle.x, obstacle.y);
        }

        //プレイヤー
        game.batch.draw(playerAnime.getKeyFrame(stateTime), player.x, player.y);

        //画面左上に現在の進行状況を表示
        game.font.draw(game.batch, obstCount + " m", 5, 379);

        game.batch.draw(startAnime.getKeyFrame(stateTime), startWord.x, startWord.y);

        game.batch.end(); //batch.begin()からここまでの描写リクエストをまとめて処理

        //ユーザーのキー入力処理
        if (Gdx.input.isKeyJustPressed(Keys.LEFT))
            player.x -= 64;
        if (Gdx.input.isKeyJustPressed(Keys.RIGHT))
            player.x += 64;

        //playerを画面外に出さないための処理
        if (player.x < 0)
            player.x = 0;
        if (player.x > 192 - 64)
            player.x = 192 - 64;

        //背景の生成条件を設定
        if (backgroundPos < -384) {
            spawnBackground();
        }

        player.y += 75 * Gdx.graphics.getDeltaTime();
        if (player.y > 15) {
            player.y = 15;
            playerPos = true;
        }

        if (playerPos == true) {
            startWord.y -= gameSpeed * Gdx.graphics.getDeltaTime();

            //背景のArrayリストに次のオブジェクトがある場合の処理
            Iterator<Rectangle> bgIter = backgrounds.iterator();
            while (bgIter.hasNext()) {
                Rectangle background = bgIter.next();
                background.y -= gameSpeed * Gdx.graphics.getDeltaTime();
                backgroundPos = background.y;
                if (backgroundPos < -768) {
                    bgIter.remove();
                }
            }

            //障害物の生成条件を設定
            if (TimeUtils.nanoTime() - lastObstTime > obstSpan && obstCount < gameCleaNumber) {
                spawnObstacle();
                obstCount++;
            }

            //障害物のArrayリストに次のオブジェクトがある場合の処理
            Iterator<Rectangle> obstIter = obstacles.iterator();
            while (obstIter.hasNext()) {
                Rectangle obstacle = obstIter.next();
                obstacle.y -= gameSpeed * Gdx.graphics.getDeltaTime();
                if (obstacle.y + 64 < 0)
                    obstIter.remove();
                if (obstacle.overlaps(player)) {
                    missSound.play(0.3f);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    game.setScreen(new GameOverScreen(game));
                    dispose();
                }
            }

            if (obstCount >= gameCleaNumber) {
                goal.y -= gameSpeed * Gdx.graphics.getDeltaTime();
                if (goal.y + 18 < 0) {
                    game.setScreen(new GameClearScreen(game));
                    dispose();
                }
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
        goalImage.dispose();
        backgroundImage.dispose();
        runMusic.dispose();
        missSound.dispose();
    }
}
