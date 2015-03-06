package com.geohash.dao;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BatchStatement.Type;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.geohash.model.CassConfig;
import com.geohash.model.CassSession;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * User: mdyminski
 */
public class Dao {

    private static final Logger LOGGER = LogManager.getLogger(Dao.class);

    private Session datastaxSession;

    private final String columnFamilyName;

    private final boolean isAsync;

    private final int batchSize;

    private final CassConfig configuration;

    private final int timeToLive;

    public Dao(CassConfig configuration,  String columnFamilyName) {
        this.configuration = configuration;
        this.columnFamilyName = columnFamilyName;
        this.isAsync = configuration.useAsync();
        this.batchSize = configuration.getBatchSize();
        this.timeToLive = configuration.getTimeToLive();
    }

    public int getTTL() {
        return timeToLive;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void executeBatch(BatchStatement batch) {
        execute(batch);
    }

    public void execute(Statement statement) {
        try {
            if (isAsync) {
                addCallBack(getSession().executeAsync(statement));
            } else {
                getSession().execute(statement);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to execute", e);
        }

    }

    public void truncateColumnFamily() {
        try {
            getSession().execute("TRUNCATE " + columnFamilyName + ";");
            LOGGER.info("Column family: {} was truncated!", columnFamilyName);
        } catch (Exception e) {
            throw new RuntimeException("Unable to truncate the column family: " + columnFamilyName, e);
        }
    }

    public Session getSession() {
        if (datastaxSession == null) {
            LOGGER.info("New cassandra session was created!");
            datastaxSession = CassSession.getInstance(configuration).getDatastaxSession();
        }
        return datastaxSession;
    }

    public void shutdown() {
        try {
            CassSession.shutdown();
            LOGGER.info("Cassandra session was shutdown!");
        } catch (Exception e) {
            throw new RuntimeException("Error during closing connection to DB", e);
        }
    }

    public void addCallBack(ListenableFuture<ResultSet> future) {

        Futures.addCallback(future, new FutureCallback<ResultSet>() {

            private final Stopwatch stopwatch = Stopwatch.createStarted();

            @Override
            public void onSuccess(ResultSet result) {
                LOGGER.debug("Execution took: {}", stopwatch);
            }

            @Override
            public void onFailure(Throwable t) {
                LOGGER.error("Unable to insert messages", t);
            }
        });

    }

}

