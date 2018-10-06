package com.grzegorz.mariobros.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.sprites.enemies.Enemy;
import com.grzegorz.mariobros.sprites.TileObjects.InteractiveTileObject;
import com.grzegorz.mariobros.sprites.Mario;
import com.grzegorz.mariobros.sprites.items.Item;

public class WorldContactListener implements ContactListener{
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // '|' <-- bitowe OR
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;


        switch (cDef){
            // uderzenie w cegle
            // po przypadku z brick nie ma break'a, wiec jesli sie zgadza to wykona sie funkcjonalnosc dla monety
            case MarioBros.MARIO_HEAD_BIT | MarioBros.BRICK_BIT:
            // uderzenie w monete
            case MarioBros.MARIO_HEAD_BIT | MarioBros.COIN_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.MARIO_HEAD_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;
            // Wskoczenie na mobka
            case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT)
                    ((Enemy) fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                else
                    ((Enemy) fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                break;
            // zwalczanie mobkow cegla
            case MarioBros.ENEMY_BIT | MarioBros.BUMPED_BRICK_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).brickKill();
                else
                    ((Enemy) fixB.getUserData()).brickKill();
            // Mobek + obiekt (rura)
            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            // Smierc Mario
            case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                        ((Mario) fixA.getUserData()).hit((Enemy) fixB.getUserData());
                else
                        ((Mario) fixB.getUserData()).hit((Enemy) fixA.getUserData());
                break;
            // Mobek + mobkiem
            case MarioBros.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).onEnemyHit((Enemy) fixB.getUserData());
                ((Enemy)fixB.getUserData()).onEnemyHit((Enemy) fixA.getUserData());
                break;
                // Mario lvl up
            case MarioBros.MARIO_BIT | MarioBros.ITEM_BIT:
                boolean isBig;
                if (fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                    isBig = ((Mario) fixA.getUserData()).isMarioBig();
                else
                    isBig = ((Mario) fixB.getUserData()).isMarioBig();
                if(!isBig) {
                    if (fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT)
                        ((Item) fixA.getUserData()).use();
                    else
                        ((Item) fixB.getUserData()).use();
                }
                break;
                // Mushroom run into object (pipe e.g)
            case MarioBros.OBJECT_BIT | MarioBros.ITEM_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT)
                    ((Item) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item) fixB.getUserData()).reverseVelocity(true, false);
                break;
                // flaga wygranko
            case MarioBros.MARIO_BIT | MarioBros.FLAG_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                    ((Mario) fixA.getUserData()).captureTheFlag();
                else
                    ((Mario) fixB.getUserData()).captureTheFlag();
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
