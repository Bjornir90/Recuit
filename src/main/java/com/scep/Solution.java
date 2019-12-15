package com.scep;

public class Solution {

    public int[] dVars;
    public double objRes;

    public Solution(int[] dVars, double objRes){
        this.dVars = dVars;
        this.objRes = objRes;
    }

    @Override
    public String toString() {
        String res = "Solution : [\n\tFonction objectif : " + objRes + "\n\tVariables de décisions : [\n";
        for(int i=0; i<dVars.length-1; i++){
            res += "\t\tx" + i + " = " + dVars[i] + ",\n";
        }
        res += "\t\tx" + (dVars.length-1) + " = " + dVars[dVars.length-1] + "\n\t]\n";
        return res;
    }
}
