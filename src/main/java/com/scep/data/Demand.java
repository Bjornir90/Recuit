package com.scep.data;

public class Demand
{
    float[][] avg;
    float[][] stdDeviation;

    public Demand(float[][] avg, float[][] stdDeviation) {
        this.avg = avg;
        this.stdDeviation = stdDeviation;
    }

    public float[][] generateScenario(int scen, int scenTotNb){
        int size = avg.length;
        float coef = scen/scenTotNb * 4 - 2;
        float[][] sc = new float[size][size];
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                sc[i][j] = avg[i][j] + coef*stdDeviation[i][j];
            }
        }
        return sc;
    }

    static private String matrixToString(float[][] matrix) {
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
}
