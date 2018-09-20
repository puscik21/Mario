package com.grzegorz.mariobros.sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.scenes.Hud;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.Mario;

public class Brick extends InteractiveTileObject {

    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }


    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isMarioBig()) {
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/breakblock.wav"));
            //MarioBros.manager.get("sounds/breakblock.wav", Sound.class).play();
        } else {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));
        }
    }
}
