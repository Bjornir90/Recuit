package com.scep.data;

public class Demand
{
    float[][] avg;
    float[][] stdDeviation;

    public Demand(float[][] avg, float[][] stdDeviation) {
        this.avg = avg;
        this.stdDeviation = stdDeviation;
    }

    public int[][] generateScenario(int scen, int scenTotNb){
        int size = avg.length;
        float coef = scenTotNb==1 ? 0:scen/(float)scenTotNb * 3 - 2;
        int[][] sc = new int[size][size];
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                sc[i][j] = Math.max((int) (avg[i][j] + coef*stdDeviation[i][j]), 0);
            }
        }
        return sc;
    }

    static public String matrixToString(float[][] matrix) {
        StringBuilder res = new StringBuilder();
        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[i].length; j++) {
                res.append(matrix[i][j]);
                res.append(' ');
            }
            res.append('\n');
        }
        return res.toString();
    }

    static public String matrixToString(int[][] matrix) {
        StringBuilder res = new StringBuilder();
        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[i].length; j++) {
                res.append(matrix[i][j]);
                res.append(' ');
            }
            res.append('\n');
        }
        return res.toString();
    }

    @Override
    public String toString() {
        return "Demand{\n" +
                "avg:\n" + matrixToString(avg) +
                "stdDeviation:\n" + matrixToString(stdDeviation) +
                '}';
    }

    public String scenToString(int scen, int scenTotNb){
        return "Scen 0{\n"+
                matrixToString(generateScenario(scen, scenTotNb))
                + '}';
    }
}
