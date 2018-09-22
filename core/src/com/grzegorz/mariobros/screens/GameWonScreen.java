package com.grzegorz.mariobros.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.grzegorz.mariobros.MarioBros;

public class GameWonScreen implements Screen{

    private Stage stage;
    private Viewport viewport;
    private Game game;

    public GameWonScreen(Game game){
        this.game = game;
        this.viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MarioBros) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label.LabelStyle font_yellow = new Label.LabelStyle(new BitmapFont(), Color.YELLOW);

        Table table = new Table();
        table.center();
        table.setFillParent(true); // ze wypelni caly stage

        Label gameOverLabel = new Label("WYGRANKO", font);
        Label maybeLaterLabel = new Label("Moze jeszcze kiedys zrobie drugi poziom ;)", font);
        Label playAgainLevel = new Label("Click to Play Again", font_yellow);

        table.add(gameOverLabel).expandX();
        table.row();
        table.add(maybeLaterLabel).expandX().padTop(20f);
        table.row();
        table.add(playAgainLevel).expandX().padTop(20f);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            game.setScreen(new PlayScreen((MarioBros) game));
            // pamietac o dispose gdy zmieniamy screena
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
