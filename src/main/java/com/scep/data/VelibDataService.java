package com.scep.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class VelibDataService {
    private String[] jsonFiles;
    private Map<ZonedDateTime, VelibStation[]> stationData;
    private AvgVelibUse avgVelibUse;

    public VelibDataService(String[] jsonFiles) {
        this.jsonFiles = jsonFiles;
    }

    private void parseInputFiles() {
        stationData = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            for(String fn : jsonFiles) {
                byte[] jsonData = Files.readAllBytes(Paths.get(fn));
                VelibStation[] stations = mapper.readValue(jsonData, VelibStation[].class);

                if(stations.length > 0)
                    stationData.put(stations[0].timestamp, stations); // all records in a file have the same timestamp

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<ZonedDateTime, VelibStation[]> getStationData() {
        // lazy initialisation
        if(stationData == null) parseInputFiles();
        return stationData;
    }

    static private int[][] createMatrix(VelibStation[] stations) {
        int size = stations.length;
        int res[][] = new int[size][size];
        for(int i=0; i<size; i++) for(int j=0; j<size; j++) {
            res[i][j] = i==j ? 0 : (stations[i].bikes + stations[j].bikes)/2;
        }
        return res;
    }

    private int[][][] createMatrices() {
        return getStationData().entrySet().stream()
                .map(e -> e.getValue())
                .map(e -> createMatrix(e))
                .toArray(int[][][]::new);
    }

    public AvgVelibUse getAvgVelibUse() {
        if(avgVelibUse != null) return avgVelibUse;

        int[][][] matrices = createMatrices();

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
            avg[i][j] = sum[i][j] / matCount;
            stdDeviation[i][j] = (float) Math.sqrt(squareSum[i][j]/matCount - Math.pow(avg[i][j], 2));
        }

        return avgVelibUse = new AvgVelibUse(avg, stdDeviation);
    }
}
