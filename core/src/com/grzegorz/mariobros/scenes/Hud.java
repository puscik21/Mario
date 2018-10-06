package com.grzegorz.mariobros.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.grzegorz.mariobros.MarioBros;

import static java.lang.String.format;

public class Hud implements Disposable{
    public Stage stage;
    private Viewport viewPort;

    // do wyswietlania czasu i punktow
    private Integer worldTimer;
    private float timeCount;
    private static Integer score;

    private Label countdownLabel;
    private static Label scoreLabel;


    public Hud(SpriteBatch sb){
        worldTimer = 180;
        timeCount = 0;
        score = 0;
        Label timeLabel;
        Label levelLabel;
        Label worldLabel;
        Label marioLabel;

        viewPort = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewPort, sb);

        Table table = new Table();      // do rozmieszczenia na tym tych wszystkich rzeczy?
        table.top();                    // umieszczamy table na gorze ekranu
        table.setFillParent(true);      // ustawiamy rozmiar taki jak rozmiar aplikacji

        // %03d oznacza chyba ze 3 liczby wezmiemy do licznika
        countdownLabel = new Label(format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        marioLabel = new Label("MARIO", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(marioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        stage.addActor(table);
    }

    public int getTime() {
        return worldTimer;
    }

    public void update(float dt){
        timeCount += dt;
        if (timeCount >= 1 && worldTimer > 0){
            worldTimer--;
            countdownLabel.setText(format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int value){
        score += value;
        scoreLabel.setText(format("%06d", score));
    }

    public void timeDecrease(){
            worldTimer--;
            countdownLabel.setText(format("%03d", worldTimer));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
