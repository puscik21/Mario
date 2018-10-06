package com.grzegorz.mariobros.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.Mario;

public abstract class Enemy extends Sprite {

    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public Vector2 velocity;
    protected boolean dangerous;
    protected boolean setToDestroy;
    protected boolean destroyed;
    protected boolean removeBody;
    protected boolean removeTexture;

    public Enemy(PlayScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(-0.7f, -1.2f);
        b2body.setActive(false);
        dangerous = true;
    }

    public boolean isRemoveBody() {
        return removeBody;
    }

    protected abstract void defineEnemy();

    public abstract void update(float dt);

    public abstract void hitOnHead(Mario mario);

    public abstract void brickKill();

    public abstract void onEnemyHit(Enemy enemy);

    public boolean isDangerous() {
        return dangerous;
    }

    public void reverseVelocity(boolean x, boolean y) {
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }
}

