package com.scep.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VelibDataService {
    static final private String[] JSON_FILES = {
            "velib-0-8.json",
            "velib-0-18.json",
            "velib-1-8.json",
            "velib-1-18.json",
            "velib-2-8.json",
            "velib-2-18.json",
            "velib-3-8.json",
            "velib-3-18.json",
            "velib-4-8.json",
            "velib-4-18.json"
    };

    private List<VelibStation[]> parsedData;
    private Demand demand;

    public VelibDataService() {
        long l1 = System.currentTimeMillis();
        initParsedData();
        initDemand();
        long l2 = System.currentTimeMillis();
        System.out.printf("VelibDataService initialised in  %.3fs\n", (l2-l1)/1000.0);
    }

    private VelibDataService(List<VelibStation[]> parsedData) {
        this.parsedData = parsedData;
        initDemand();
    }

    private void initParsedData() {
        parsedData = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            for(String fn : JSON_FILES) {
                URL resource = getClass().getResource("/velib/" + fn);
                VelibStation[] data = mapper.readValue(resource, VelibStation[].class);
                parsedData.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private int[][] createMatrix(VelibStation[] stations) {
        int size = stations.length;
        int[][] res = new int[size][size];
        for(int i=0; i<size; i++) for(int j=0; j<size; j++) {
            res[i][j] = i==j ? 0 : (stations[i].getBikes() + stations[j].getBikes())/2;
        }
        return res;
    }

    private void initDemand() {
        int[][][] matrices = parsedData.stream()
                .map(VelibDataService::createMatrix)
                .toArray(int[][][]::new);

        int matCount = matrices.length;
        int matSize = matrices[0].length;

        long[][] sum = new long[matSize][matSize];
        long[][] squareSum = new long[matSize][matSize];

        for(int h=0; h<matCount; h++) for(int i=0; i<matSize; i++) for(int j=0; j<matSize; j++) {
            sum[i][j] += matrices[h][i][j];
            squareSum[i][j] += matrices[h][i][j] * matrices[h][i][j];
        }

        float[][] avg = new float[matSize][matSize];
        float[][] stdDeviation = new float[matSize][matSize];

        for(int i=0; i<matSize; i++) for(int j=0; j<matSize; j++) {
            avg[i][j] = sum[i][j] / (float) matCount;
            stdDeviation[i][j] = (float) Math.sqrt(squareSum[i][j]/(double) matCount - Math.pow(avg[i][j], 2));
        }

        demand = new Demand(avg, stdDeviation);
    }

    public List<VelibStation[]> getParsedData() {
        return parsedData;
    }

    public Demand getDemand() {
        return demand;
    }

    public VelibDataService getSubset(int size) {
        List<VelibStation[]> stationSubsets = new ArrayList<>();
        for(VelibStation[] lst : parsedData)
            stationSubsets.add(Arrays.copyOf(lst, size));
        return new VelibDataService(stationSubsets);
    }
}
