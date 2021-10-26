package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameClearScreen implements Screen {

    final RunGame game;

    OrthographicCamera camera;
    Viewport viewport;

    //ゲームクリア画面のアニメーション
    Animation<TextureRegion> clearAnime;

    //アニメーション進度の管理用
    float stateTime;

    public GameClearScreen(final RunGame gam) {
        game = gam;

        camera = new OrthographicCamera(192, 384);
        viewport = new FitViewport(192, 384, camera);
        camera.setToOrtho(false, 192, 384);

        //アニメーション用画像をロード
        TextureRegion texture1 = new TextureRegion(new Texture("goal_screen1.png"));
        TextureRegion texture2 = new TextureRegion(new Texture("goal_screen2.png"));
        TextureRegion texture3 = new TextureRegion(new Texture("goal_screen3.png"));
        TextureRegion texture4 = new TextureRegion(new Texture("goal_screen4.png"));

        //1〜4の画像をアニメーションに割り当て、1フレームあたり0.25秒で再生
        clearAnime = new Animation<TextureRegion>(0.25f, texture1, texture2, texture3, texture4);
        clearAnime.setPlayMode(Animation.PlayMode.LOOP);

        stateTime = 0.0f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        stateTime += Gdx.graphics.getDeltaTime(); //アニメーションの前のフレームからの経過時間を進行時間に加算
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(clearAnime.getKeyFrame(stateTime), 0, 0);
        game.batch.end();

        if(Gdx.input.isKeyPressed(Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
            dispose();
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
    }

}
