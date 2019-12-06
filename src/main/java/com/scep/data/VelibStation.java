package com.scep.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class VelibStation {

    protected String name;
    protected int code;
    protected int capacity;
    protected int bikes;
    protected ZonedDateTime timestamp;

    @JsonProperty("fields")
    private void unpackFields(Map<String, Object> properties) {
        name = (String) properties.get("station_name");
        code = Integer.parseInt((String) properties.get("station_code"));
        capacity = (int) properties.get("nbedock") + (int) properties.get("nbdock");
        bikes = (int) properties.get("nbbike") + (int) properties.get("nbebike");
        // + (int) properties.get("nbbikeoverflow") + (int) properties.get("nbebikeoverflow");
    }

    @JsonProperty("record_timestamp")
    private void parseTimestamp(String tmstmp) {
        timestamp = ZonedDateTime.parse(tmstmp);
    }

    @Override
    public String toString() {
        return "VelibStation{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", capacity=" + capacity +
                ", bikes=" + bikes +
                ", timestamp=" + timestamp +
                '}';
    }
}
