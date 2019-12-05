package com.scep;

import java.util.concurrent.ThreadLocalRandom;

public abstract class RecuitGenerique implements Solver{

    protected float temp, bestObjective;
    protected float[] bestX, currentX;

    public abstract float objectif(float[] x);

    public abstract boolean verifiesConstraints();

    protected void updateSolution(float[] newX){
        float delta = objectif(newX) - objectif(currentX);

        if(delta < 0){

            currentX = newX;
            if(objectif(currentX) < bestObjective){
                bestObjective = objectif(currentX);
                bestX = currentX;
            }

        } else {

            double random = ThreadLocalRandom.current().nextDouble(0, 1);
            if(random <= Math.exp(-delta/temp)){
                currentX = newX;
            }

        }
    }

}
