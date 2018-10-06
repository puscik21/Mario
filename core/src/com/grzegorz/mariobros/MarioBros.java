package com.grzegorz.mariobros;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grzegorz.mariobros.screens.PlayScreen;

public class MarioBros extends Game {
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;
    public static final float PPM = 100;

    /* Rozroznienie na Ground bit i Object bit jest dlatego, aby przeciwnik
     * zmienil kierunek ruchu gdy dotknie rury */
    public static final short NOTHING_BIT = 0;
    public static final short GROUND_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short ITEM_BIT = 256;
    public static final short MARIO_HEAD_BIT = 512;
    public static final short FLAG_BIT = 1024;
    public static final short WALL_BIT = 2048;
    public static final short BUMPED_BRICK_BIT = 4096;

	public SpriteBatch batch;


	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));

		// TODO ogarnac dzwieki
//        flaga
//        game over
//        smierc Mario
//        skok na mobka
	}

	@Override
	public void render () {
		super.render();
	}

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }
}
