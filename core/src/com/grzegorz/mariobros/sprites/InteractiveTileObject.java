package com.grzegorz.mariobros.sprites;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.grzegorz.mariobros.MarioBros;

public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;

    protected Fixture fixture;


    public InteractiveTileObject(World world, TiledMap map, Rectangle bounds){
        this.world = world;
        this.map = map;
        this.bounds = bounds;

        BodyDef bDef = new BodyDef();
        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.position.set((bounds.getX() + bounds.getWidth() / 2) / MarioBros.PPM, (bounds.getY() + bounds.getHeight() / 2) / MarioBros.PPM);

        body = world.createBody(bDef);

        shape.setAsBox(bounds.getWidth() / 2 / MarioBros.PPM, bounds.getHeight() / 2 / MarioBros.PPM);
        fDef.shape = shape;
        fixture = body.createFixture(fDef);
    }

    public abstract void onHeadHit();

    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }


    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);
        return layer.getCell((int) (body.getPosition().x * MarioBros.PPM / 16),
                (int) (body.getPosition().y * MarioBros.PPM / 16));
                // /16 bo to rozmiar komorki
    }
}
