package com.grzegorz.mariobros.sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.grzegorz.mariobros.MarioBros;
import com.grzegorz.mariobros.screens.PlayScreen;
import com.grzegorz.mariobros.sprites.Mario;

public class TheWall extends InteractiveTileObject{

    public TheWall(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.WALL_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {

    }
}
