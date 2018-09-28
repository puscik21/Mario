package com.grzegorz.mariobros.tools;

import com.grzegorz.mariobros.sprites.TileObjects.InteractiveTileObject;

public class Timer implements Runnable {
    private final long time;
    private boolean itsTime;
    private int neededTime;

    public Timer(int neededTime){
        this.time = System.currentTimeMillis();
        itsTime = false;
        this.neededTime = neededTime;
    }

    public boolean isItTime() {
        return itsTime;
    }

    @Override
    public void run() {
        while (System.currentTimeMillis() - time < neededTime){
            try{
                Thread.sleep(100);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        itsTime = true;
    }
}
