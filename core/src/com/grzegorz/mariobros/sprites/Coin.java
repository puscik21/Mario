package com.grzegorz.mariobros.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.scenes.Hud;

public class Coin extends InteractiveTileObject{
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(World world, TiledMap map, Rectangle bounds){
        super(world, map, bounds);
        tileSet = map.getTileSets().getTileSet("NES - Super Mario Bros - Tileset");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }


    @Override
    public void onHeadHit() {
        Gdx.app.log("Coin", "Collision");
        getCell().setTile(tileSet.getTile(BLANK_COIN));
        Hud.addScore(500);
    }
}
