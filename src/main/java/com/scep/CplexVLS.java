package com.scep;

import com.scep.data.Demand;
import ilog.concert.IloException;


public class CplexVLS extends CplexGenerique {

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
            int nbScenario
    ) throws IloException {

        System.out.println(demand.toString());
        System.out.println(demand.scenToString());

        // Construction du tableau de coef pour la fonction objectif
        int nbStation = k_i.length;
        ufVarsNb = nbStation;
        float[] coefs = new float[nbStation + 2*(nbScenario * nbStation * nbStation) + 2*(nbScenario * nbStation)];
        for(int i=0; i<nbStation; i++) {
            coefs[i] = c_i[i];
        }
        int start = nbStation;
        for(int s=0; s<nbScenario; s++){
            for(int i=0; i<nbStation; i++){
                for(int j=0; j<nbStation; j++){
                    coefs[start+s*nbStation*nbStation+i*nbStation+j] = v_i[i] * gauss_proba(s, nbScenario);
                }
            }
        }
        start += nbStation*nbStation*nbScenario;
        for(int s=0; s<nbScenario; s++){
            for(int i=0; i<nbStation; i++){
                coefs[start+s*nbStation+i] = w_i[i] * gauss_proba(s, nbScenario);
            }
        }
        start += nbScenario*nbStation;
        // Apparemment c'est initialisé à zero déjà

        //for(int i=start; i<coefs.length; i++) coefs[i] = 0;

        // Appel de la fonction addObjective
        this.addObjective(coefs.length, coefs, true);

        // Construction des contraintes
        float[] zero_coefs = new float[nbStation + 2*(nbScenario * nbStation * nbStation) + 3*(nbScenario * nbStation)];
        // (1a)
        for(int i=0; i<nbStation; i++){
            zero_coefs[i] = 1;
            this.addConstraint(zero_coefs, k_i[i], compOp.LE);
            zero_coefs[i] = 0;
        }
        // (1b) et (1c) et (1d)
        for(int s=0; s<nbScenario; s++){
            int[][] ksi = demand.generateScenario(s, nbScenario);
            for(int i=0; i<nbStation; i++) {
                // (1b)
                for (int j = 0; j < nbStation; j++) {
                    zero_coefs[start+s*nbStation*nbStation+i*nbStation+j] = 1;
                    zero_coefs[nbStation+s*nbStation*nbStation+i*nbStation+j] = 1;
                    this.addConstraint(zero_coefs, ksi[i][j], compOp.EQ);
                    zero_coefs[start+s*nbStation*nbStation+i*nbStation+j] = 0;
                    zero_coefs[nbStation+s*nbStation*nbStation+i*nbStation+j] = 0;
                }
                // (1c)
                zero_coefs[nbStation+2*(nbStation*nbStation*nbScenario)+nbStation*nbScenario+s*nbStation+i] = 1;
                for(int j=0; j<nbStation; j++){
                    zero_coefs[nbStation+s*nbStation*nbStation+i*nbStation+j] = -1;
                }
                zero_coefs[i] = -1;
                int ksi_sum = 0;
                for(int j=0; j<nbStation; j++){
                    ksi_sum += ksi[i][j];
                }
                this.addConstraint(zero_coefs, ksi_sum, compOp.EQ);
                zero_coefs[nbStation+2*(nbStation*nbStation*nbScenario)+nbStation*nbScenario+s*nbStation+i] = 0;
                for(int j=0; j<nbStation; j++){
                    zero_coefs[nbStation+s*nbStation*nbStation+i*nbStation+j] = 0;
                }
                zero_coefs[i] = 0;
                // (1d)
                int ois_plus_start = nbStation + 2*(nbStation*nbStation*nbScenario) + 2*(nbStation*nbScenario);
                zero_coefs[ois_plus_start+s*nbStation+i] = 1;
                zero_coefs[nbStation*(1+nbStation*nbScenario)+s*nbStation+i] = -1;
                zero_coefs[i] = 1;
                for(int j=0; j<nbStation; j++){
                    zero_coefs[start+s*nbStation*nbStation+i*nbStation+j] = -1;
                    zero_coefs[start+s*nbStation*nbStation+j*nbStation+i] = 1;
                }
                this.addConstraint(zero_coefs, k_i[i], compOp.EQ);
                zero_coefs[ois_plus_start+s*nbStation+i] = 0;
                zero_coefs[nbStation*(1+nbStation*nbScenario)+s*nbStation+i] = 0;
                zero_coefs[i] = 0;
                for(int j=0; j<nbStation; j++){
                    zero_coefs[start+s*nbStation*nbStation+i*nbStation+j] = 0;
                    zero_coefs[start+s*nbStation*nbStation+j*nbStation+i] = 0;
                }
            }
        }

    }

    public static void main(String[] args) throws IloException {
        Demand demand = new Demand(new float[][]{{0,20},{30,0}}, new float[][]{{0,2},{3,0}});
        CplexVLS test = new CplexVLS();
        test.buildProblem(demand, new float[]{1,2}, new float[]{2,3}, new float[]{1,3}, new int[]{10,10}, 2);
        Solution sol = test.compute();
        System.out.println(sol);
    }


}