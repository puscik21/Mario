package com.grzegorz.mariobros.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.enemies.Enemy;
import com.grzegorz.mariobros.sprites.enemies.Turtle;


public class Mario extends Sprite{
    public static final float FIRST_MARIO_POSITION_X = 128 / MarioBros.PPM;
    public static final float FIRST_MARIO_POSITION_Y = 16 / MarioBros.PPM;

    public World world;
    public Body b2body;

    // Animation variables
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD, SLIDING, ENDING};
    public State currentState;
    public State previousState;
    private TextureRegion marioStand;
    private Animation<TextureRegion> marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private TextureRegion marioSlide;

    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private TextureRegion bigMarioSlide;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;
    private boolean marioIsSliding;
    private boolean marioIsEnding;

    private int numbersOfUsage;         // TODO do metody slide, ale moze do usuniecia

    // TODO dorzucic ghost vertices zeby Mario nie podskakiwal na cegielkach
    public Mario(PlayScreen screen){
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        // animacja biegu
        // tworzymy tablice z odpowiednimi teksturami animacji
        com.badlogic.gdx.utils.Array<TextureRegion> frames = new com.badlogic.gdx.utils.Array<TextureRegion>(14);
        // wypelniamy tablice obrazkami biegu
        for (int i=1; i<4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        // przypisujemy animacje
        marioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();
        for (int i=1; i<4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        bigMarioRun = new Animation<TextureRegion>(0.1f, frames);
        // czyscimy tablice dla nastepnej animacji
        frames.clear();

        // animacja skoku
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);


        // stojacy Mario
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        // smierc Mario
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        // sliding Mario
        marioSlide = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 128, 0, 16, 16);
        bigMarioSlide = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 128, 0, 16, 32);

        // Mario lvl up
        for (int i=0; i<3; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        }
        growMario = new Animation<TextureRegion>(0.2f, frames);

        // przypisanie parametrow w Box2d
        defineMario();
        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setRegion(marioStand);
    }

    public void update(float dt){
        // getWidth odnosi sie do Sprite'a
        // uaktualnia pozycje tekstury do pozycji hitboxa
        if (marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 0.06f);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

        // uaktualnia odpowiednia teksture animacji
        setRegion(getFrame(dt));

        if (timeToDefineBigMario)
            defineBigMario();
        if (timeToRedefineMario)
            reDefineMario();

        if (marioIsEnding && b2body.getLinearVelocity().x < 1.5f) {
            b2body.applyLinearImpulse(new Vector2(0.1f, 0), b2body.getWorldCenter(), true);
        }

        if (currentState == State.SLIDING)
            slide(dt);

    }

    private void slide(float dt){
        if (getY() < 0.36f) {
            // gdy juz zjedzie na dol, ma sie przesunac na druga strone flagi (robi to tylko raz)
            if (numbersOfUsage == 0) {
                b2body.setTransform(b2body.getPosition().x + 0.16f, b2body.getPosition().y, b2body.getAngle()); // obrot wokol flagi
                b2body.setLinearVelocity(0, 0);
            }
            TextureRegion region = getFrame(dt);
            region.flip(true,false);
            setRegion(region);
            if (stateTimer > 1.5f) {
                b2body.applyLinearImpulse(new Vector2(0.2f, 0), b2body.getWorldCenter(), true);
                b2body.setGravityScale(1);
                marioIsSliding = false;
                marioIsEnding = true;
            }
            numbersOfUsage += 1;
        }
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region;

        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) :
                        marioRun.getKeyFrame(stateTimer, true);
                break;
            case SLIDING:
                region = marioIsBig ? bigMarioSlide : marioSlide;
                break;
            case FALLING:
            case STANDING:
                default:
                    region = marioIsBig ? bigMarioStand : marioStand;
                    break;
        }

        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }
        else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        /* jezeli stan jest taki sam jak poprzedni to zwiekszamy stateTimer
        * jesli swie zwiekszyl, musimy zresetowac timer */
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;

        return region;
    }

    // zwraca stan Mario
    public State getState(){
        if (runGrowAnimation)
            return State.GROWING;
        else if (marioIsDead)
            return State.DEAD;
        else if (marioIsSliding)
            return State.SLIDING;
        else if (b2body.getLinearVelocity().y > 0 || b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public boolean isMarioBig() {
        return marioIsBig;
    }

    public void grow(){
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        // TODO miejsce na dzwiek
    }

    public boolean isDead(){
        return marioIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    public void hit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL) {
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        } else if (enemy.isDangerous()){
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                // TODO power_down sound
            } else {
                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = MarioBros.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList())
                    fixture.setFilterData(filter);
                b2body.applyLinearImpulse(new Vector2(-0.5f, 4f), b2body.getWorldCenter(), true);
                // TODO mario_die sound
            }
        }
    }

    public void captureTheFlag(){
        Gdx.app.log("Mario", "WINNER");
        marioIsSliding = true;
        b2body.setGravityScale(0);
        b2body.setLinearVelocity(new Vector2(0, -1f));
    }

    public void reDefineMario(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT |
                MarioBros.FLAG_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM),
                new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.shape = head;
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.isSensor = true;       // sensor nie oddzialuje z niczym w swiecie

        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineMario = false;
    }

    public void defineBigMario(){
        // Zniszcz starego hitboxa i zastap nowym - wiekszym
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / MarioBros.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT |
                MarioBros.FLAG_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM),
                new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.shape = head;
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.isSensor = true;       // sensor nie oddzialuje z niczym w swiecie

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(FIRST_MARIO_POSITION_X, FIRST_MARIO_POSITION_Y);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT |
                MarioBros.FLAG_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM),
                new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.shape = head;
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.isSensor = true;       // sensor nie oddzialuje z niczym w swiecie

        b2body.createFixture(fdef).setUserData(this);
    }
}
