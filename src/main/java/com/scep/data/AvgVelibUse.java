package com.scep.data;

public class AvgVelibUse
{
    float[][] avg;
    float[][] stdDeviation;

    public AvgVelibUse(float[][] avg, float[][] stdDeviation) {
        this.avg = avg;
        this.stdDeviation = stdDeviation;
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
        return "AvgVelibUse{\n" +
                "avg:\n" + matrixToString(avg) +
                "stdDeviation:\n" + matrixToString(stdDeviation) +
                '}';
    }
}
