package com.grzegorz.mariobros.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.Mario;
import com.grzegorz.mariobros.sprites.enemies.Enemy;

public class Goomba extends Enemy {

    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        frames = new Array<TextureRegion>();
        for (int i=0; i<2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));

        walkAnimation = new Animation(0.4f, frames );
        stateTime = 0;
        // tym ustalam rozmiar textury
        setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt) {
        stateTime += dt;
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            // 32 - poniewaz jest to 3 tekstura w pliku (0, 16, 32, 48,...)
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            stateTime =0;
        } else if (!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() , getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.MARIO_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        // creat the head here
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1 / MarioBros.PPM);
        vertice[1] = new Vector2(5, 8).scl(1 / MarioBros.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / MarioBros.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / MarioBros.PPM);
        head.set(vertice);      // tutaj przypisujemy wielokat do head

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        /* po to przypisuje klase Goomba, poniewaz pozniej moge np odniesc sie do tego
        przez getUserData() i wykastowac na ogolna klase Enemy i wziac z niej jakas metode np */
        b2body.createFixture(fdef).setUserData(this);
    }


    @Override
    public void draw(Batch batch){
        if (!destroyed || stateTime < 1){
            super.draw(batch);
        }
    }


    @Override
    /* Dlaczego nie moge tak po prostu usunac Goomby tutaj?
        Poniewaz w PlayScreenie jest funkcja world.step(), ktora odswieza cala gre,
        nie moge tak po prostu usunac obiektu, bo co jesli np. koliduje w tym momencie
        z dwoma obiektami?
        Dlatego trzeba to zrobic w metodzie update() */
    public void hitOnHead(Mario mario){
        setToDestroy = true;
        // TODO stopm sound
    }
}