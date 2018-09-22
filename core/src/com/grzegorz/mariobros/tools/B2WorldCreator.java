package com.grzegorz.mariobros.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.TileObjects.Brick;
import com.grzegorz.mariobros.sprites.TileObjects.Coin;
import com.grzegorz.mariobros.sprites.TileObjects.Flag;
import com.grzegorz.mariobros.sprites.enemies.Enemy;
import com.grzegorz.mariobros.sprites.enemies.Goomba;
import com.badlogic.gdx.utils.Array;
import com.grzegorz.mariobros.sprites.enemies.Turtle;

public class B2WorldCreator {
    private Array<Goomba> goombas;
    private Array<Turtle> turtles;
    private Array<Enemy> enemies = new Array<Enemy>();

    public B2WorldCreator(PlayScreen screen){

        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fDef = new FixtureDef();
        Body body;

        // create ground bodies / fixtures
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            bDef.type = BodyDef.BodyType.StaticBody;
            bDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioBros.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bDef);

            shape.setAsBox(rectangle.getWidth() / 2 / MarioBros.PPM, rectangle.getHeight() / 2 / MarioBros.PPM);
            fDef.shape = shape;
            body.createFixture(fDef);
        }

        // create pipes bodies / fixtures
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            bDef.type = BodyDef.BodyType.StaticBody;
            bDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioBros.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bDef);

            shape.setAsBox(rectangle.getWidth() / 2 / MarioBros.PPM, rectangle.getHeight() / 2 / MarioBros.PPM);
            fDef.shape = shape;
            fDef.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fDef);
        }

        // create coins bodies / fixtures
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen, object);
        }

        // create bricks bodies / fixtures
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            new Brick(screen, object);
        }

        // create all goombas
        goombas = new Array<Goomba>();
        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            goombas.add(new Goomba(screen, rectangle.getX() / MarioBros.PPM, rectangle.getY() / MarioBros.PPM));
        }

        // create all turtles
        turtles = new Array<Turtle>();
        for (MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            turtles.add(new Turtle(screen, rectangle.getX() / MarioBros.PPM, rectangle.getY() / MarioBros.PPM));
        }

        // TODO flaga
        for (MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class))
            new Flag(screen, object);


        enemies.addAll(goombas);
        enemies.addAll(turtles);
    }

   public void removeEnemy(Enemy enemy){
        enemies.removeValue(enemy, true);
   }

    public Array<Enemy> getEnemnies() {
        return enemies;
    }
}
