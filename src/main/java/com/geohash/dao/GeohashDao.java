package com.geohash.dao;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.datastax.driver.core.*;
import com.datastax.driver.core.BatchStatement.Type;

import com.geohash.util.Serializer;
import com.geohash.model.Buffer;
import com.geohash.model.CassConfig;
import com.geohash.model.GeoData;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class GeohashDao extends Dao {

    private static final Logger LOGGER = LogManager.getLogger(GeohashDao.class.getName());

    private static final String COLUMN_FAMILY_NAME = "geohashes";

    private static final String INSERT_STATEMENT = "INSERT INTO geohashes(geohash, timestamp, geodata) values(?,?,?) USING TTL ";

    private static final String SELECT_SINGLE_GEOHASH_STATEMENT = "SELECT * FROM geohashes WHERE geohash=? AND timestamp>? AND timestamp<? ALLOW FILTERING;";

    private static final String SELECT_STATEMENT = "SELECT * FROM geohashes WHERE timestamp>? AND timestamp<?  ALLOW FILTERING;";

    private static final String SELECT_MANY_GEOHASH_STATEMENT = "SELECT * FROM geohashes WHERE geohash in ? AND timestamp>? AND timestamp<? ALLOW FILTERING;";

    private PreparedStatement selectGeoData;

    private PreparedStatement sliceSelect;

    private PreparedStatement insertGeo;

    private PreparedStatement selectManyGeoData;

    private final Buffer<GeoData> geoDataList = new Buffer<>();

    private final Serializer serializer = new Serializer();

    public GeohashDao(CassConfig configuration) {
        super(configuration, COLUMN_FAMILY_NAME);
    }

    private PreparedStatement getInsertSingleStatement() {
        if (insertGeo == null) {
            insertGeo = getSession().prepare(INSERT_STATEMENT + getTTL());
        }

        return insertGeo;
    }

    private PreparedStatement getSelectStatement() {
        if (selectGeoData == null) {
            selectGeoData = getSession().prepare(SELECT_SINGLE_GEOHASH_STATEMENT);
        }

        return selectGeoData;
    }

    private PreparedStatement getSliceStatement() {
        if (sliceSelect == null) {
            sliceSelect = getSession().prepare(SELECT_STATEMENT);
        }

        return sliceSelect;
    }

    private PreparedStatement getManySelectStatement() {
        if (selectManyGeoData == null) {
            selectManyGeoData = getSession().prepare(SELECT_MANY_GEOHASH_STATEMENT);
        }

        return selectManyGeoData ;
    }

    public void save(GeoData geo) {
        try {
            if (geo.getGeoHash() == null) {
                throw new RuntimeException("Geohash can't be null");
            }

            BoundStatement bind = getBoundStatement(geo);

            getSession().execute(bind);
        } catch (Exception e) {
            throw new RuntimeException("Unable to save geo data with geoHash: " + geo.getGeoHash(), e);
        }
    }

    public List<GeoData> get(String geohash, long from, long to) {
        try {
            BoundStatement bind = getSelectStatement().bind(geohash, from, to);

            ResultSet res = getSession().execute(bind);

            return getGeoDataFromResult(res);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get geodata for geo hash: " + geohash, e);
        }
    }

    public List<GeoData> slice(Long from, Long to) {
        try {
            BoundStatement bind = getSliceStatement().bind(from, to);

            ResultSet res = getSession().execute(bind);

            return getGeoDataFromResult(res);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get geo datas for time range: " + from + " to: " + to, e);
        }
    }


    public List<GeoData> getForMany(List<String> hashes, Long from, Long to) {
        try {
            BoundStatement bind = getManySelectStatement().bind(hashes, from, to);

            ResultSet res = getSession().execute(bind);

            return getGeoDataFromResult(res);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get geo datas for time range: " + from + " to: " + to, e);
        }
    }

    public List<GeoData> getRx(List<String> hashes, Long from, Long to) {
        try {
            List<GeoData> datas = Collections.synchronizedList(new ArrayList<>());

            List<ResultSetFuture> futures = Lists.newArrayListWithExpectedSize(hashes.size());
            for (String hash: hashes)
                futures.add(getSession().executeAsync(SELECT_SINGLE_GEOHASH_STATEMENT, hash, from, to));

            Scheduler scheduler = Schedulers.io();
            List<Observable<ResultSet>> observables = Lists.transform(futures, (ResultSetFuture future) -> Observable.from(future, scheduler));

            CountDownLatch latch = new CountDownLatch(1);
            Observable o  = Observable.merge(observables);
            o.subscribe(new Observer<ResultSet>() {
                @Override
                public void onNext(ResultSet resultSet) {
                    datas.addAll(getGeoDataFromResult(resultSet));
                }

                @Override
                public void onError(Throwable throwable) {
                    throw new RuntimeException("Can't get geo data", throwable);
                }

                @Override
                public void onCompleted() {
                    latch.countDown();
                }
            });

            latch.await();

            return datas;
        } catch (Exception e) {
            throw new RuntimeException("Unable to get geo datas for time range: " + from + " to: " + to, e);
        }
    }

    public List<GeoData> getManyRx(List<List<String>> hashes, Long from, Long to) {
        try {
            List<GeoData> datas = Collections.synchronizedList(new ArrayList<>());

            List<ResultSetFuture> futures = Lists.newArrayListWithExpectedSize(hashes.size());
            for (List<String> list: hashes)
                futures.add(getSession().executeAsync(SELECT_MANY_GEOHASH_STATEMENT, list, from, to));

            Scheduler scheduler = Schedulers.io();
            List<Observable<ResultSet>> observables = Lists.transform(futures, (ResultSetFuture future) -> Observable.from(future, scheduler));

            CountDownLatch latch = new CountDownLatch(1);
            Observable o  = Observable.merge(observables);
            o.subscribe(new Observer<ResultSet>() {
                @Override
                public void onNext(ResultSet resultSet) {
                    datas.addAll(getGeoDataFromResult(resultSet));
                }

                @Override
                public void onError(Throwable throwable) {
                    throw new RuntimeException("Can't get geo data", throwable);
                }

                @Override
                public void onCompleted() {
                    latch.countDown();
                }
            });

            latch.await();

            return datas;
        } catch (Exception e) {
            throw new RuntimeException("Unable to get geo datas for time range: " + from + " to: " + to, e);
        }
    }

    public void batchSave(GeoData geoData) {
        try {
            geoDataList.add(geoData);
            if (geoDataList.size() >= getBatchSize()) {
                flushBatch();
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to save geo data last geo hash: " + geoData.getGeoHash(), e);
        }
    }

    private void flushBatch() {
        BatchStatement batch = new BatchStatement(Type.UNLOGGED);

        for (GeoData data : geoDataList.empty()) {
            batch.add(getBoundStatement(data));
        }

        executeBatch(batch);
        LOGGER.info("Batch saved!");
    }

    private BoundStatement getBoundStatement(GeoData geodata) {
        return getInsertSingleStatement().bind(geodata.getGeoHash(), geodata.getTimestamp(),
                ByteBuffer.wrap(serializer.toBytes(geodata)));
    }

    private List<GeoData> getGeoDataFromResult(ResultSet res) {
        List<GeoData> geoDatas = new ArrayList<>();
        com.datastax.driver.core.Row one = res.one();
        while (one != null) {
            geoDatas.add(getGeoData(one));
            one = res.one();
        }

        return geoDatas;
    }

    private GeoData getGeoData(com.datastax.driver.core.Row one) {
        ByteBuffer bb = one.getBytes("geodata");
        byte[] b = new byte[bb.remaining()];
        bb.get(b);
        return serializer.fromBytes(b, GeoData.class);
    }
}

