package com.geohash.model;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AuthenticationException;
import com.datastax.driver.core.exceptions.NoHostAvailableException;

public final class CassSession {

    private Session datastaxSession;

    private static CassSession instance = null;

    private Cluster cluster;

    public Session getDatastaxSession() {
        return datastaxSession;
    }

    public Cluster getDatastaxCluster() {
        return cluster;
    }

    public static synchronized CassSession getInstance(CassConfig configuration) {
        if (instance == null) {
            instance = new CassSession(configuration);
        }
        return instance;
    }

    public static synchronized void shutdown() {
        if (instance != null) {
            instance.getDatastaxSession().close();
            instance.getDatastaxCluster().close();
            instance = null;
        }
    }

    private CassSession(CassConfig configuration) {
        try {
            prepareDatastaxConnection(configuration);
        } catch (IllegalArgumentException | SecurityException | NoHostAvailableException | AuthenticationException e) {
            throw new RuntimeException("Cannot create session to Cassandra DB", e);
        }
    }

    private void prepareDatastaxConnection(CassConfig configuration) {
        Builder builder = Cluster.builder().withPort(configuration.getNativeConnectionPort())
                .addContactPoints(configuration.getContactPoints());

		/* builder = builder.withSSL() // Uncomment if using client to node encryption */
        cluster = builder.build();
        datastaxSession = cluster.connect(configuration.getKeyspace());
    }

}
