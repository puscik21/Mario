package com.grzegorz.mariobros.sprites.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.screens.PlayScreen;

public class BumpedBrick extends Item{

    private PlayScreen screen;
    private float firstY;

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
        body.applyLinearImpulse(new Vector2(0, 2f), body.getWorldCenter(), true);
        if (body.getPosition().y < firstY)
            body.setLinearVelocity(new Vector2(0, 0));
    }
}
