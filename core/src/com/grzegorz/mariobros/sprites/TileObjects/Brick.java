package com.grzegorz.mariobros.sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.scenes.Hud;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.Mario;
import com.grzegorz.mariobros.sprites.items.Item;

public class Brick extends InteractiveTileObject {

    // Wewnetrzna klasa Timer
    private class Timer implements Runnable{
        private Brick brick;
        private final long time;

        public Timer(Brick brick) {
            this.brick = brick;
            this.time = System.currentTimeMillis();
        }

        @Override
        public void run() {
            while (System.currentTimeMillis() - time < 100){
//                try{
//                    Thread.sleep(10);
//                } catch (InterruptedException e){
//                    e.printStackTrace();
//                }
            }
            changeBrick();
        }

        private void changeBrick(){
                //screen.removeItem();
            System.out.println("czas");
            getCell().setTile(map.getTileSets().getTile(2));
            bumpedBrick.destroy();
            doAnimation = false;
        }
    }



    // Wewnetrzna klasa BumpedBrick
    public class BumpedBrick extends Item {

        private PlayScreen screen;
        private float firstY;
        private boolean jumped;

        public BumpedBrick(PlayScreen screen, float x, float y) {
            super(screen, x, y);
            this.screen = screen;
            setRegion(new TextureRegion(new Texture(Gdx.files.internal("bumped_brick.png"))));
            firstY = y;
            setBounds(getX(), getY(), 16 / MarioBros.PPM, 21 / MarioBros.PPM);
        }

        @Override
        public void defineItem() {
            BodyDef bdef = new BodyDef();
            bdef.position.set(getX() , getY());
            bdef.type = BodyDef.BodyType.DynamicBody;
            body = world.createBody(bdef);

            FixtureDef fdef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(8 / MarioBros.PPM, 8 / MarioBros.PPM);
            fdef.filter.categoryBits = MarioBros.BUMPED_BRICK_BIT;
            fdef.filter.maskBits = MarioBros.ENEMY_BIT |
                    MarioBros.GROUND_BIT;

            fdef.shape = shape;
            body.createFixture(fdef).setUserData(this);
        }

        @Override
        public void use() {

        }

        @Override
        public void update(float dt) {
            super.update(dt);
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            //body.setGravityScale(0);
            //body.setLinearVelocity(new Vector2(0, 0));
            body.setGravityScale(2);
            if (!jumped) {
                body.applyLinearImpulse(new Vector2(0, 1.5f), body.getWorldCenter(), true);
                jumped = true;
            }
            if (body.getPosition().y < firstY) {
                body.setLinearVelocity(new Vector2(0, 0));
                body.setGravityScale(0);
                //destroyed = true;
            }
//            if (body.getPosition().y > firstY + 50 / MarioBros.PPM) {
//                body.setLinearVelocity(new Vector2(0, 0));
//                body.setGravityScale(2);
//            }
        }
    }



    // ##### Glowna klasa #####
    private Brick.Timer timer;
    private Brick.BumpedBrick bumpedBrick;
    private boolean doAnimation;
    private Thread thread;

    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    public boolean isDoAnimation() {
        return doAnimation;
    }

    public BumpedBrick getBumpedBrick() {
        return bumpedBrick;
    }

    public void setBumpedBrick(BumpedBrick bumpedBrick) {
        this.bumpedBrick = bumpedBrick;
    }

    public void setTimerNull() {
        thread.interrupt();
        this.timer = null;
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
            //getCell().getTile().setOffsetY(5);
           // if (object.getProperties().containsKey("offset")){

            //TiledMapTile tile = map.getTileSets().getTile(2);
            //tile.setTextureRegion(new TextureRegion(new Texture(Gdx.files.internal("bumped_brick.png")));
            //tile.setOffsetY(5);

            //getCell().getTile().setTextureRegion(new TextureRegion(new Texture(Gdx.files.internal("bumped_brick.png"))));
           // }

            /*
                Metoda mocno okrezna, ale lepszej nie moglem znalezc
                jesli maly mario uderzy w cegle ona zniknie, a w jej miejsce pojawi sie tekstura
                podbitej cegly (przez macierz przedmiotow w PlayScreen), po chwili ona zniknie
                i pojawi sie z powrotem stara cegla
             */

            getCell().setTile(null);
            //getCell().setTile(map.getTileSets().getTile(2));

//            screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 1 / MarioBros.PPM),
//                    BumpedBrick.class));
            // bumpedBrick = new BumpedBrick(screen, body.getPosition().x, body.getPosition().y + 1 / MarioBros.PPM);
            doAnimation = true;
            if (timer == null){
                timer = new Timer(this);
                thread = new Thread(timer);
                thread.start();
            }
            else
                System.out.println("timer nie null ._.");
            /*
            chyba potrzebuje specjalnej metody w playscreen w ktorej odpali sie ten timer, metoda
            bedzie updatowana wiec bedzie sprawdzac stan timera
            albo specjalny timer do cegly
             */

            Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));
        }
    }
}
