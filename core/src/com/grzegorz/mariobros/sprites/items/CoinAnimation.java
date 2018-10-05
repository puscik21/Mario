package com.grzegorz.mariobros.sprites.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.screens.PlayScreen;

public class CoinAnimation extends Item {
    private float firstY;
    private float stateTimer;
    private Animation<TextureRegion> animation;

    public CoinAnimation(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        this.screen = screen;

        // TODO usunac
        setRegion(new TextureRegion(new Texture(Gdx.files.internal("piece_of_brick.png"))));

        setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        body.applyLinearImpulse(new Vector2(0, 3.8f), body.getWorldCenter(), true);
        firstY = y - 2 / MarioBros.PPM;
        stateTimer = 0;

        // tworzymy tablice z odpowiednimi teksturami animacji
        Array<TextureRegion> frames = new com.badlogic.gdx.utils.Array<TextureRegion>(4);
        // wypelniamy tablice obrazkami biegu
        for (int i=1; i<4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("coin"), i * 16, 0, 16, 16));
        // przypisujemy animacje
        animation = new Animation<TextureRegion>(0.08f, frames);
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() , getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);
    }

    public TextureRegion getFrame(float dt) {
        TextureRegion region;
        region = animation.getKeyFrame(stateTimer, true);
        stateTimer += dt;

        return region;
    }

    @Override
    public void use() {

    }

    @Override
    public void update(float dt) {
        if (getY() < firstY)
            toDestroy = true;

        setRegion(getFrame(dt));
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }
}
