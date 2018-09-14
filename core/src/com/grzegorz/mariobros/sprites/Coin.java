package com.grzegorz.mariobros.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.scenes.Hud;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.items.ItemDef;
import com.grzegorz.mariobros.sprites.items.Mushroom;

public class Coin extends InteractiveTileObject{
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("NES - Super Mario Bros - Tileset");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }


    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Coin", "Collision");
        Sound coinSound = Gdx.audio.newSound(Gdx.files.internal("sounds/coin.wav"));
        Sound bumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));

        if (getCell().getTile().getId() == BLANK_COIN)  // jesli byl juz zbity to nabij guza ._.
            bumpSound.play();
            //MarioBros.manager.get("sounds/bump.wav", Sound.class).play();
        else {
            getCell().setTile(tileSet.getTile(BLANK_COIN));
            if(object.getProperties().containsKey("mushroom"))
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM),
                    Mushroom.class));
            Hud.addScore(200);
            coinSound.play();
            //MarioBros.manager.get("sounds/coin.wav", Sound.class).play();
        }
    }
}
