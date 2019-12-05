package main.java.com.scep;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Recuit implements Solver {
    private float temp;
    private int nbIterationExt, nbIterationInt, fMin;
    private List<Integer> C, V, W, K, P, meilleurX, X;
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
        X = new ArrayList<>();
        Iplus = new ArrayList<>();
        Iminus = new ArrayList<>();
        Ominus = new ArrayList<>();
        for (int i = 0; i < K.size(); i++) {
            Iplus.add(new ArrayList<>());
            Iminus.add(new ArrayList<>());
            Ominus.add(new ArrayList<>());
        }
    }

    private List<Integer> mvmt(List<Integer> X){
        List<Integer> Xsuiv = new ArrayList<>();
        for (int i = 0; i < X.size(); i++) {
            int delta = ThreadLocalRandom.current().nextInt(-K.get(i)/4, K.get(i)/4);
            Xsuiv.add(i, X.get(i)+delta);
        }
        return Xsuiv;
    }

    private int objectif(List<Integer> X){
        int result = 0;
        for (int i = 0; i < X.size(); i++) {
            result += X.get(i)*C.get(i);
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

    private void iterationInt(){
        List<Integer> Xsuiv = mvmt(X);

        //Mise à jour des variables de second niveau

        for (int s = 0; s < P.size(); s++) {
            for (int i = 0; i < X.size(); i++) {
                int betaSum = 0;
                for (int j = 0; j < Beta.get(s).get(i).size(); j++) {
                    betaSum += Beta.get(s).get(i).get(j);
                }
                Iplus.get(s).add(i, Math.max(Xsuiv.get(i)-betaSum, 0));
                Iminus.get(s).add(i, Math.max(betaSum-Xsuiv.get(i), 0));
            }
        }

        //Contraintes
        for (int i = 0; i < X.size(); i++) {
            if(Xsuiv.get(i) > K.get(i))
                return;

            for (int s = 0; s < P.size(); s++) {

                int ksiSum = 0;//Somme sur les j de ksi, pour la troisième contrainte

                for (int j = 0; j < X.size(); j++) {
                    ksiSum += Ksi.get(s).get(i).get(j);
                    //Deuxième contrainte
                    if(Beta.get(s).get(i).get(j) != Ksi.get(s).get(i).get(j)- Iminus.get(s).get(i))
                        return;
                }

                //Troisième contrainte
                if(Iplus.get(s).get(i)-Iminus.get(s).get(i) != Xsuiv.get(i)-ksiSum)
                    return;
            }
        }

        int delta = objectif(Xsuiv)-objectif(X);

        //On accepte l'amélioration
        if(delta < 0){
            X = Xsuiv;
            if(objectif(X) < fMin){
                fMin = objectif(X);
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

    public List<Integer> compute(){
        //Initialise Xi à zéro pour tout i
        for (int i = 0; i < K.size(); i++) {
            X.add(0);
        }

        meilleurX = X;
        fMin = objectif(X);
        temp = fMin/20;

        for (int i = 0; i < nbIterationExt; i++) {
            iterationExt();
        }
        return meilleurX;
    }

}
