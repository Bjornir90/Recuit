package com.scep;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;


public class CplexGenerique implements Solver{

    protected enum compOp {
        GE,
        LE,
        EQ
    }

    private IloCplex model;
    private IloIntVar[] dVars;
    private IloLinearNumExpr obj;

    public CplexGenerique() throws IloException {
        this.model = new IloCplex();
    }

    // Création des variables de décision et de la fonction objectif
    // (Ici on considère que les variables de décisions sont tjrs des entiers)
    protected void addObjective(int dVarsNb, double[] coefs, boolean minimize) throws IloException {

        dVars = new IloIntVar[dVarsNb];
        obj = model.linearNumExpr();
        for(int i=0; i<dVarsNb; i++){
            dVars[i] = model.intVar(0, Integer.MAX_VALUE);
            obj.addTerm(coefs[i], dVars[i]);
        }
        if(minimize){
            model.addMinimize(obj);
        }
        else {
            model.addMaximize(obj);
        }

    }

    protected void addConstraint(double coefs[], double rMember, compOp compOperator) throws IloException {
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
                int[] resVars = new int[dVars.length];
                for(int i=0; i<dVars.length; i++){
                    resVars[i] = (int) model.getValue(dVars[i]);
                }
                return new Solution(resVars, model.getObjValue());
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
        test.addObjective(2, new double[]{10,20}, false);
        test.addConstraint(new double[]{1,0}, 4, compOp.LE);
        test.addConstraint(new double[]{0,1}, 6, compOp.LE);
        Solution s = test.compute();
    }
}