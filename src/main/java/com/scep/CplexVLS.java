package com.scep;

import com.scep.data.Demand;
import ilog.concert.IloException;


public class CplexVLS extends CplexGenerique {

    private static int nbStation, nbScenario;

    // accès simplifié aux indices pour le tableau des coefs

    private static int x(int i){
        assert i<nbStation;
        return i;
    }

    private static int i_m(int i, int s){
        assert i<nbStation;
        assert s<nbScenario;
        int start = x(nbStation);
        return start + s*nbStation + i;
    }

    private static int o_m(int i, int s){
        assert i<nbStation;
        assert s<nbScenario;
        int start = i_m(nbStation, nbScenario);
        return start + s*nbStation + i;
    }

    private static int beta(int i, int j, int s){
        assert i<nbStation;
        assert j<nbStation;
        assert s<nbScenario;
        int start = o_m(nbStation, nbScenario);
        return start + s*nbStation*nbStation + i*nbStation + j;
    }

    private static int i_p(int i, int s){
        assert i<nbStation;
        assert s<nbScenario;
        int start = beta(nbStation, nbStation, nbScenario);
        return start + s*nbStation + i;
    }

    private static int o_p(int i, int s){
        assert i<nbStation;
        assert s<nbScenario;
        int start = i_p(nbStation, nbScenario);
        return start + s*nbStation + i;
    }

    public static float gauss_proba(int s, int sTot){
        return (float) (1/Math.sqrt(2*Math.PI)*Math.exp(-(4*s/sTot-2)*(4*s/sTot-2)/2));
    }

    public CplexVLS() throws IloException {
        super();
    }

    public void buildProblem(
            Demand demand,
            float[] c_i,
            float[] v_i,
            float[] w_i,
            int[] k_i,
            int nbScen
    ) throws IloException {

        System.out.println(demand.toString());
        System.out.println(demand.scenToString());
        System.out.println("C :\n");
        for (float aC_i : c_i) System.out.println(aC_i);
        System.out.println("V :\n");
        for (float aV_i : v_i) System.out.println(aV_i);
        System.out.println("W :\n");
        for (float aW_i : w_i)System.out.println(aW_i);
        System.out.println("K :\n");
        for (float aK_i : k_i) System.out.println(aK_i);

        // Construction du tableau de coefs pour la fonction objectif
        nbStation = k_i.length;
        nbScenario = nbScen;
        ufVarsNb = nbStation; // useful variables number
        System.out.println("nb scen : " + nbScenario);
        System.out.println("nb station : " + nbStation);
        float[] coefs = new float[o_p(nbStation, nbScenario)+1];
        for(int i=0; i<nbStation; i++) {
            coefs[x(i)] = c_i[i];
        }
        for(int s=0; s<nbScenario; s++){
            for(int i=0; i<nbStation; i++){
                coefs[i_m(i,s)] = v_i[i] * gauss_proba(s, nbScenario);
                coefs[o_m(i,s)] = w_i[i] * gauss_proba(s, nbScenario);
            }
        }

        // Appel de la fonction addObjective
        this.addObjective(coefs.length, coefs, true);



        // Construction des contraintes
        float[] c_coefs = new float[o_p(nbStation, nbScenario)+1];

        // (1a)
        for(int i=0; i<nbStation; i++){
            c_coefs[x(i)] = 1;
            this.addConstraint(c_coefs, k_i[i], compOp.LE);

            //reset coefs
            c_coefs[x(i)] = 0;
        }


        // (1b) et (1c) et (1d)
        for(int s=0; s<nbScenario; s++){
            int[][] ksi = demand.generateScenario(s, nbScenario);
            for(int i=0; i<nbStation; i++) {
                // (1b)
                for (int j = 0; j < nbStation; j++) {
                    if (i==j) continue;
                    c_coefs[beta(i,j,s)] = 1;
                    c_coefs[i_m(i,s)] = 1;
                    //this.addConstraint(c_coefs, ksi[i][j], compOp.EQ);

                    //reset coefs
                    c_coefs[beta(i,j,s)] = 0;
                    c_coefs[i_m(i,s)] = 0;
                }

                // (1c)
                c_coefs[i_p(i,s)] = 1;
                c_coefs[i_m(i,s)] = -1;
                c_coefs[x(i)] = -1;
                int ksi_sum = 0;
                for(int j=0; j<nbStation; j++){
                    ksi_sum -= ksi[i][j];
                }
                this.addConstraint(c_coefs, ksi_sum, compOp.EQ);

                // reset coefs
                c_coefs[i_p(i,s)] = 0;
                c_coefs[i_m(i,s)] = 0;
                c_coefs[x(i)] = 0;

                // (1d)
                c_coefs[o_p(i,s)] = 1;
                c_coefs[o_m(i,s)] = -1;
                c_coefs[x(i)] = 1;
                int rm = 0;
                for(int j=0; j<nbStation; j++){
                    if (i==j) continue;
                    rm += ksi[i][j];
                    rm -= ksi[j][i];
                }
                this.addConstraint(c_coefs, k_i[i] + rm, compOp.EQ);
                // reset coefs
                /*
                c_coefs[o_p(i,s)] = 0;
                c_coefs[o_m(i,s)] = 0;
                c_coefs[x(i)] = 0;
                for(int j=0; j<nbStation; j++){
                    c_coefs[beta(i,j,s)] = 0;
                    c_coefs[beta(j,i,s)] = 0;
                }
                */
            }
        }
    }

    public static void main(String[] args) throws IloException {
        Demand demand = new Demand(new float[][]{{0,100},{300,0}}, new float[][]{{0,2},{3,0}});
        CplexVLS test = new CplexVLS();
        test.buildProblem(demand, new float[]{1,1}, new float[]{10,10}, new float[]{1,1}, new int[]{100,150}, 100);
        Solution sol = test.compute();
        System.out.println(sol);
    }


}