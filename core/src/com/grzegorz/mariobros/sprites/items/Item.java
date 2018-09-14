package com.grzegorz.mariobros.sprites.items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.Mario;


/** Gdy przy zbieraniu grzybka glowa wystepowal blad zrobilem maly refaktor na wlasna reke
 * w klasie Item usunalem argument Mario mario - teraz do Mario odwoluje sie z klasy Item i nie musze
 * go przesylac np do Mushrooma
 * Do tego w WorldContactListener dodalem kontakt itemu z glowa
 * Zeby nie rosl x4 ustawilem zmienna isBig do sprawdzenia czy juz zebral glowa
 * W Mario glowie dodalem filtr GROUND_BIT
 */
public abstract class Item extends Sprite {
    protected PlayScreen screen;
    protected World world;
    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroyed;
    protected Body body;
    protected Mario mario;

    public Item(PlayScreen screen, float x, float y){
        this.screen = screen;
        this.world = screen.getWorld();
        this.mario = screen.getPlayer();
        setPosition(x, y);
        setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        defineItem();
        toDestroy = false;
        destroyed = false;
    }

    public abstract void defineItem();
    public abstract void use();

    public void update(float dt){
        if (toDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
        }
    }

    @Override
    public void draw(Batch batch){
        if (!destroyed)
            super.draw(batch);
    }

    public void destroy(){
        toDestroy = true;
    }

    public void reverseVelocity(boolean x, boolean y) {
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }
}
