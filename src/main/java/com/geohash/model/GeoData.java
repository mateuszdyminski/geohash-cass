package com.geohash.model;

import java.io.Serializable;

/**
 * User: mdyminski
 */
public class GeoData implements Serializable {

    private static final long serialVersionUID = -8574553174640790229L;

    private String geoHash;
    private long timestamp;
    private long totalRsrp;
    private long totalRsrq;
    private int numberOfMeasRsrp;
    private int numberOfMeasRsrq;

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public long getTotalRsrp() {
        return totalRsrp;
    }

    public void setTotalRsrp(long totalRsrp) {
        this.totalRsrp = totalRsrp;
    }

    public long getTotalRsrq() {
        return totalRsrq;
    }

    public void setTotalRsrq(long totalRsrq) {
        this.totalRsrq = totalRsrq;
    }

    public int getNumberOfMeasRsrp() {
        return numberOfMeasRsrp;
    }

    public void setNumberOfMeasRsrp(int numberOfMeasRsrp) {
        this.numberOfMeasRsrp = numberOfMeasRsrp;
    }

    public int getNumberOfMeasRsrq() {
        return numberOfMeasRsrq;
    }

    public void setNumberOfMeasRsrq(int numberOfMeasRsrq) {
        this.numberOfMeasRsrq = numberOfMeasRsrq;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "GeoData{" +
                "geoHash='" + geoHash + '\'' +
                ", timestamp=" + timestamp +
                ", totalRsrp=" + totalRsrp +
                ", totalRsrq=" + totalRsrq +
                ", numberOfMeasRsrp=" + numberOfMeasRsrp +
                ", numberOfMeasRsrq=" + numberOfMeasRsrq +
                '}';
    }
}