package com.scep;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Recuit extends RecuitGenerique {
    private float temp;
    private int nbIterationExt, nbIterationInt, fMin;
    private List<Integer> C, V, W, K, P;
    private float[] X, meilleurX;
    private List<List<Integer>> Iminus, Iplus, Ominus;
    private List<List<List<Integer>>> Beta, Ksi;


    public Recuit(
            int nbIterationExt,
            int nbIterationInt,
            List<Integer> c,
            List<Integer> v,
            List<Integer> w,
            List<Integer> k,
            List<Integer> p,
            List<List<List<Integer>>> beta,
            List<List<List<Integer>>> ksi)
    {

        this.nbIterationExt = nbIterationExt;
        this.nbIterationInt = nbIterationInt;
        C = c;
        V = v;
        W = w;
        K = k;
        P = p;
        Beta = beta;
        Ksi = ksi;
        X = new float[K.size()];
        Iplus = new ArrayList<>();
        Iminus = new ArrayList<>();
        Ominus = new ArrayList<>();
        for (int i = 0; i < K.size(); i++) {
            Iplus.add(new ArrayList<>());
            Iminus.add(new ArrayList<>());
            Ominus.add(new ArrayList<>());
        }
    }

    private float[] mvmt(float[] X){
        float[] Xsuiv = new float[K.size()];
        for (int i = 0; i < K.size(); i++) {
            int delta = ThreadLocalRandom.current().nextInt(-K.get(i)/4, K.get(i)/4);
            Xsuiv[i] = X[i]+delta;
        }
        return Xsuiv;
    }


    private void iterationInt(){
        float[] Xsuiv = mvmt(X);

        //Mise à jour des variables de second niveau

        for (int s = 0; s < P.size(); s++) {
            for (int i = 0; i < K.size(); i++) {
                int betaSum = 0;
                for (int j = 0; j < Beta.get(s).get(i).size(); j++) {
                    betaSum += Beta.get(s).get(i).get(j);
                }
                Iplus.get(s).add(i, Math.max((int)Xsuiv[i]-betaSum, 0));
                Iminus.get(s).add(i, (int) Math.max(betaSum-Xsuiv[i], 0));
            }
        }

        int delta = (int) (objectif(Xsuiv)-objectif(X));

        //On accepte l'amélioration
        if(delta < 0){
            X = Xsuiv;
            if(objectif(X) < fMin){
                fMin = (int) objectif(X);
                meilleurX = X;
            }
        //On vérifie que l'on est suffisament chaud pour accepter le résultat pire
        } else {
            double p = ThreadLocalRandom.current().nextDouble(0, 1);
            if(p <= Math.exp(-delta/temp)){
                X = Xsuiv;
            }
        }
    }

    private void iterationExt(){
        for (int i = 0; i < nbIterationInt; i++) {
            iterationInt();
        }
        temp = temp/1.2f;
    }

    public Solution compute(){
        //Initialise Xi à zéro pour tout i
        for (int i = 0; i < K.size(); i++) {
            X[i] = 0;
        }

        meilleurX = X;
        fMin = (int) objectif(X);
        temp = fMin/20;

        for (int i = 0; i < nbIterationExt; i++) {
            iterationExt();
        }
        //return meilleurX;
        return null;
    }

    @Override
    public float objectif(float[] x) {
        float result = 0;
        for (int i = 0; i < x.length; i++) {
            result += x[i]*C.get(i);
        }
        //Pour chaque scénario
        for (int s = 0; s < P.size(); s++) {

            int coutScenario = 0;

            //Pour chaque station
            for (int i = 0; i < V.size(); i++) {
                coutScenario += V.get(i)* Iminus.get(s).get(i);
                coutScenario += W.get(i)* Ominus.get(s).get(i);//Math.max((X.get(i) - K.get(i)), 0);
            }

            result += coutScenario*P.get(s);
        }
        return result;
    }

    @Override
    public boolean verifiesConstraints(float[] newX) {
        for (int i = 0; i < K.size(); i++) {
            if(newX[i] > K.get(i))
                return false;

            for (int s = 0; s < P.size(); s++) {

                int ksiSum = 0;//Somme sur les j de ksi, pour la troisième contrainte

                for (int j = 0; j < K.size(); j++) {
                    ksiSum += Ksi.get(s).get(i).get(j);
                    //Deuxième contrainte
                    if(Beta.get(s).get(i).get(j) != Ksi.get(s).get(i).get(j)- Iminus.get(s).get(i))
                        return false;
                }

                //Troisième contrainte
                if(Iplus.get(s).get(i)-Iminus.get(s).get(i) != newX[i]-ksiSum)
                    return false;
            }
        }
        return true;
    }
}
