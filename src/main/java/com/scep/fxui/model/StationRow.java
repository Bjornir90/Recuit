package com.scep.fxui.model;

public class StationRow {
    int code;
    String name;
    float c = 1;
    float v = 1;
    float w = 1;
    int k;
    int x = 0;

    public StationRow(int code, String name, int k) {
        this.code = code;
        this.name = name;
        this.k = k;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public float getC() {
        return c;
    }

    public void setC(float c) {
        this.c = c;
    }

    public float getV() {
        return v;
    }

    public void setV(float v) {
        this.v = v;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }
}
