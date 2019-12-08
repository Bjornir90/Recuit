package com.scep.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class VelibStation {

    private String name;
    private int code;
    private int capacity;
    private int bikes;

    @JsonProperty("fields")
    private void unpackFields(Map<String, Object> properties) {
        name = (String) properties.get("station_name");
        code = Integer.parseInt((String) properties.get("station_code"));
        capacity = (int) properties.get("nbedock") + (int) properties.get("nbdock");
        bikes = (int) properties.get("nbbike") + (int) properties.get("nbebike");
        // + (int) properties.get("nbbikeoverflow") + (int) properties.get("nbebikeoverflow");
    }

    @Override
    public String toString() {
        return "VelibStation{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", capacity=" + capacity +
                ", bikes=" + bikes +
                '}';
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getBikes() {
        return bikes;
    }

}
