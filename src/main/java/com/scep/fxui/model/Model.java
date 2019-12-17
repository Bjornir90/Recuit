package com.scep.fxui.model;

import com.scep.data.VelibDataService;
import com.scep.data.VelibStation;
import com.scep.fxui.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.function.Function;

public class Model {
    VelibDataService dataService;
    ObservableList<StationRow> rowList;

    public Model() {
        dataService = new VelibDataService();
        dataService = dataService.getSubset(Main.nStation);
        rowList = FXCollections.observableArrayList();

        // create rows with data from one json document
        if(dataService.getParsedData().isEmpty()) return;
        VelibStation[] data = dataService.getParsedData().get(0);
        for(VelibStation st : data) {
            StationRow row = new StationRow(st.getCode(), st.getName(), st.getCapacity());
            rowList.add(row);
        }
    }

    public VelibDataService getDataService() {
        return dataService;
    }

    public ObservableList<StationRow> getRowList() {
        return rowList;
    }

    private float[] getFloatColumnValues(Function<StationRow, Float> getter) {
        float[] res = new float[rowList.size()];
        for(int i=0; i<rowList.size(); i++) res[i] = getter.apply(rowList.get(i));
        return res;
    }

    private int[] getIntColumnValues(Function<StationRow, Integer> getter) {
        int[] res = new int[rowList.size()];
        for(int i=0; i<rowList.size(); i++) res[i] = getter.apply(rowList.get(i));
        return res;
    }

    public float[] getC() {
        return getFloatColumnValues(StationRow::getC);
    }

    public float[] getV() {
        return getFloatColumnValues(StationRow::getV);
    }

    public float[] getW() {
        return getFloatColumnValues(StationRow::getW);
    }

    public int[] getK() {
        return getIntColumnValues(StationRow::getK);
    }

    public void setX(int[] x) {
        if(x.length != rowList.size()) throw new Error("Different sizes");
        for(int i=0; i<x.length; i++) {
            rowList.get(i).setX(x[i]);
        }
    }
}
