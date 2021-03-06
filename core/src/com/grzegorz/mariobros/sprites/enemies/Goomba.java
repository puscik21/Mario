package com.grzegorz.mariobros.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.Mario;

public class Goomba extends Enemy {

    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    public enum State {KILLED_BY_BRICK, LIVING}
    private State currentState;
    private float deadRotationDegrees;
    private boolean jumped;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        frames = new Array<TextureRegion>();
        for (int i=0; i<2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));

        walkAnimation = new Animation<TextureRegion>(0.4f, frames );
        stateTime = 0;
        // tym ustalam rozmiar textury
        setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setToDestroy = false;
        currentState = State.LIVING;
        deadRotationDegrees = 0;
    }

    public void update(float dt) {
        stateTime += dt;
        if (setToDestroy) {
            if (currentState == State.KILLED_BY_BRICK){
                setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
                deadRotationDegrees += 0.1;
                rotate(deadRotationDegrees);
                if (!jumped) {
                    jumped = true;
                    Filter filter = new Filter();
                    filter.maskBits = MarioBros.NOTHING_BIT;
                    for (Fixture fixture : b2body.getFixtureList())
                        fixture.setFilterData(filter);
                    b2body.applyLinearImpulse(new Vector2(0.5f, 3f), b2body.getWorldCenter(), true);
                }
                else if (stateTime > 3) {
                    removeBody = true;
                }
            }
            else {
                // when goomba is killed by Mario or turtle he get's nothing bit, so he cannot colide with anything
                Filter filter = new Filter();
                filter.maskBits = MarioBros.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList())
                    fixture.setFilterData(filter);

                // 32 - poniewaz jest to 3 tekstura w pliku (0, 16, 32, 48,...)
                setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            }
            if (stateTime > 1)
                removeBody = true;

        } else {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    public void brickKill(){
        currentState = State.KILLED_BY_BRICK;
        setToDestroy = true;
        stateTime = 0;
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
                MarioBros.BUMPED_BRICK_BIT |
                MarioBros.ENEMY_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        // create the head here
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
            super.draw(batch);
    }

    public void onEnemyHit(Enemy enemy){
        if (enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL) {
            setToDestroy = true;
            stateTime = 0;
        }
        else
            reverseVelocity(true, false);
    }


    @Override
    /* Dlaczego nie moge tak po prostu usunac Goomby tutaj?
        Poniewaz w PlayScreenie jest funkcja world.step(), ktora odswieza cala gre,
        nie moge tak po prostu usunac obiektu, bo co jesli np. koliduje w tym momencie
        z dwoma obiektami?  */
    public void hitOnHead(Mario mario){
        setToDestroy = true;
        dangerous = false;
        stateTime = 0;
        // TODO stomp sound
    }
}
