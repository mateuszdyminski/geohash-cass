package com.geohash.model;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * User: mdyminski
 */
public class CassConfig {

    private final Properties properties;

    public CassConfig() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(System.getProperty("config", "config/geo.properties")));
        } catch (IOException e) {
            throw new RuntimeException("Cannot open config file");
        }
    }

    public boolean useAsync() {
        return Boolean.parseBoolean(properties.getProperty("useAsync"));
    }

    public int getBatchSize() {
        return Integer.parseInt(properties.getProperty("batch_size"));
    }

    public int getTimeToLive() {
        return Integer.parseInt(properties.getProperty("ttl"));
    }

    public int getNativeConnectionPort() {
        return Integer.parseInt(properties.getProperty("native_connection_port"));
    }

    public String getContactPoints() {
        return properties.getProperty("seed_nodes");
    }

    public String getKeyspace() {
        return properties.getProperty("keyspace");
    }

    public boolean insertSamples() {
        return Boolean.parseBoolean(properties.getProperty("insertSamples"));
    }
}
