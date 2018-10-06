package com.grzegorz.mariobros.sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.scenes.Hud;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.Mario;
import com.grzegorz.mariobros.sprites.items.CoinAnimation;
import com.grzegorz.mariobros.sprites.items.ItemDef;
import com.grzegorz.mariobros.sprites.items.Mushroom;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;

    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("NES - Super Mario Bros - Tileset");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }


    @Override
    public void onHeadHit(Mario mario) {
        final int BLANK_COIN = 28;
        Sound coinSound = Gdx.audio.newSound(Gdx.files.internal("sounds/coin.wav"));
        Sound bumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));

        if (getCell().getTile().getId() == BLANK_COIN)  // jesli byl juz zbity to nabij guza ._.
            bumpSound.play();
        else {
            getCell().setTile(tileSet.getTile(BLANK_COIN));
            if(object.getProperties().containsKey("mushroom"))
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM),
                        Mushroom.class));
            else {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM),
                        CoinAnimation.class));
            }
            Hud.addScore(200);
            coinSound.play();
        }
    }
}
