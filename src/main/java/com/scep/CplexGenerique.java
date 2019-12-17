package com.scep;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;


public class CplexGenerique implements Solver{

    protected enum compOp {
        GE,
        LE,
        EQ
    }

    protected IloCplex model;
    protected int ufVarsNb;
    private IloIntVar[] dVars;
    private IloLinearNumExpr obj;

    public CplexGenerique() throws IloException {
        this.model = new IloCplex();
    }

    // Création des variables de décision et de la fonction objectif
    // (Ici on considère que les variables de décisions sont tjrs des entiers)
    protected void addObjective(int dVarsNb, float[] coefs, boolean minimize) throws IloException {

        dVars = new IloIntVar[dVarsNb];
        obj = model.linearNumExpr();
        for(int i=0; i<dVarsNb; i++){
            dVars[i] = model.intVar(0, Integer.MAX_VALUE);
            obj.addTerm(coefs[i], dVars[i]);
        }
        if(minimize){
            model.addMinimize(obj);
        } else {
            model.addMaximize(obj);
        }

    }

    protected void addConstraint(float coefs[], float rMember, compOp compOperator) throws IloException {
            IloLinearNumExpr constraint = model.linearNumExpr();
            for(int i=0; i<dVars.length; i++){
                constraint.addTerm(coefs[i], dVars[i]);
            }
            switch (compOperator){
                case GE:
                    model.addGe(constraint, rMember);
                    break;
                case LE:
                    model.addLe(constraint, rMember);
                    break;
                case EQ:
                    model.addEq(constraint, rMember);
                    break;
            }
    }

    @Override
    public Solution compute(){
        try {
            boolean isSolved = model.solve();
            if(isSolved){
                int resVarNb = dVars.length;
                if(ufVarsNb != 0) resVarNb = ufVarsNb;
                int[] resVars = new int[resVarNb];
                for(int i=0; i<resVarNb; i++){
                    resVars[i] = (int) model.getValue(dVars[i]);
                }
                Solution res = new Solution(resVars, model.getObjValue());
                System.out.println(res);
                return res;
            }
            else {
                System.out.println("Problem could not be solved.");
                return null;
            }
        } catch (IloException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws IloException {
        CplexGenerique test = new CplexGenerique();
        test.addObjective(2, new float[]{10,20}, false);
        test.addConstraint(new float[]{1,0}, 4, compOp.LE);
        test.addConstraint(new float[]{0,1}, 6, compOp.LE);
        Solution s = test.compute();
        System.out.println(s);
    }
}
