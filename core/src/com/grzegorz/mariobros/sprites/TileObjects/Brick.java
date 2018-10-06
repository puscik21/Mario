package com.grzegorz.mariobros.sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.scenes.Hud;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.Mario;
import com.grzegorz.mariobros.sprites.items.Item;
import com.grzegorz.mariobros.sprites.items.PieceOfBrick;

import java.util.ArrayList;

public class Brick extends InteractiveTileObject {

    // Wewnetrzna klasa Timer
    private class Timer implements Runnable{
        private final long time;

        public Timer() {
            this.time = System.currentTimeMillis();
        }

        @Override
        public void run() {
            while (System.currentTimeMillis() - time < 100){
                try{
                    Thread.sleep(20);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            if (bumpedBrick != null)
                changeBumpedBrick();
            else if (killingBrick != null)
                changeKillingBrick();
        }


        private void changeBumpedBrick(){
            getCell().setTile(map.getTileSets().getTile(2));
            bumpedBrick.destroy();
            doAnimation = false;
        }


        private void changeKillingBrick() {
            killingBrick.destroy();
            doKillingBrick = false;
        }
    }



    // Wewnetrzna klasa BumpedBrick
    public class BumpedBrick extends Item {

        private float firstY;
        private boolean jumped;

        public BumpedBrick(PlayScreen screen, float x, float y) {
            super(screen, x, y);
            setRegion(new TextureRegion(new Texture(Gdx.files.internal("bumped_brick.png"))));
            firstY = y;
            setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        }

        @Override
        public void defineItem() {
            BodyDef bdef = new BodyDef();
            bdef.position.set(getX() , getY());
            bdef.type = BodyDef.BodyType.DynamicBody;
            body = world.createBody(bdef);
        }

        @Override
        public void update(float dt) {
            super.update(dt);
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            body.setGravityScale(2);
            if (!jumped) {
                body.applyLinearImpulse(new Vector2(0, 1.5f), body.getWorldCenter(), true);
                jumped = true;
            }
            if (body.getPosition().y < firstY) {
                body.setLinearVelocity(new Vector2(0, 0));
                body.setGravityScale(0);
            }
        }


        @Override
        public void use() {

        }
    }

    // Wewnetrzna klasa BumpedBrick
    public class KillingBrick extends Item {

        private float firstY;
        private boolean jumped;

        public KillingBrick(PlayScreen screen, float x, float y) {
            super(screen, x, y);
            firstY = y;
            setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        }

        @Override
        public void defineItem() {
            BodyDef bdef = new BodyDef();
            bdef.position.set(getX() , getY());
            bdef.type = BodyDef.BodyType.DynamicBody;
            body = world.createBody(bdef);

            FixtureDef fdef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(8/ MarioBros.PPM, 8 / MarioBros.PPM);
            //TODO KILLING nie BUMPED
            fdef.filter.categoryBits = MarioBros.BUMPED_BRICK_BIT;
            fdef.filter.maskBits = MarioBros.ENEMY_BIT |
                    MarioBros.GROUND_BIT;

            fdef.shape = shape;
            body.createFixture(fdef).setUserData(this);
        }

        @Override
        public void update(float dt) {
            super.update(dt);
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            body.setGravityScale(2);
            if (!jumped) {
                body.applyLinearImpulse(new Vector2(0, 1.5f), body.getWorldCenter(), true);
                jumped = true;
            }
            if (body.getPosition().y < firstY) {
                body.setLinearVelocity(new Vector2(0, 0));
                body.setGravityScale(0);
            }
        }


        @Override
        public void use() {

        }
    }


    // ##### Glowna klasa #####
    private Brick.Timer timer;
    private Brick.BumpedBrick bumpedBrick;
    private Brick.KillingBrick killingBrick;
    private Thread thread;
    private ArrayList<PieceOfBrick> pieces;
    private boolean doAnimation;
    private boolean doKillingBrick;
    private boolean turnToPieces;

    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
        pieces = new ArrayList<PieceOfBrick>(4);
    }

    public boolean isDoAnimation() {
        return doAnimation;
    }

    public boolean isDoKillingBrick() {
        return doKillingBrick;
    }

    public BumpedBrick getBumpedBrick() {
        return bumpedBrick;
    }

    public KillingBrick getKillingBrick() {
        return killingBrick;
    }

    public void setBumpedBrick(BumpedBrick bumpedBrick) {
        this.bumpedBrick = bumpedBrick;
    }

    public void setKillingBrick(KillingBrick killingBrick) {
        this.killingBrick = killingBrick;
    }

    public boolean isTurnToPieces(){
        return turnToPieces;
    }

    public ArrayList<PieceOfBrick> getPieces() {
        return pieces;
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
            turnToPieces = true;
            doKillingBrick = true;

            if (timer == null){
                timer = new Timer();
                thread = new Thread(timer);
                thread.start();
            }

            Hud.addScore(200);
            Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/breakblock.wav"));
            sound.play();
        }
        else {
            /*
                Metoda mocno okrezna, ale lepszej nie moglem znalezc
                jesli maly mario uderzy w cegle ona zniknie, a w jej miejsce pojawi sie tekstura
                podbitej cegly (przez macierz przedmiotow w PlayScreen), po chwili ona zniknie
                i pojawi sie z powrotem stara cegla
             */

            getCell().setTile(null);
            doAnimation = true;

            if (timer == null){
                timer = new Timer();
                thread = new Thread(timer);
                thread.start();
            }

            Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));
            sound.play();
        }
    }
}
