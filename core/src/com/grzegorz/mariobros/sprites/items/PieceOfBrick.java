package com.grzegorz.mariobros.sprites.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.screens.PlayScreen;

public class PieceOfBrick extends Item {

    public PieceOfBrick(PlayScreen screen, float x, float y, float impulseX, float impulseY) {
        super(screen, x, y);
        this.screen = screen;
        setRegion(new TextureRegion(new Texture(Gdx.files.internal("piece_of_brick.png"))));
        setBounds(getX(), getY(), 8 / MarioBros.PPM, 8 / MarioBros.PPM);
        body.applyLinearImpulse(new Vector2(impulseX, impulseY), body.getWorldCenter(), true);
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() , getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4/ MarioBros.PPM, 4 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.NOTHING_BIT;

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
    }
}
