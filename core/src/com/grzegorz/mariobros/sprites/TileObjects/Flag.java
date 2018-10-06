package com.grzegorz.mariobros.sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.Mario;

public class Flag extends InteractiveTileObject {

    public Flag(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.FLAG_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {

    }
}
