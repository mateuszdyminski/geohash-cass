package com.geohash.api;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
import com.geohash.dao.GeohashDao;
import com.geohash.model.CassConfig;
import com.geohash.model.GeoData;
import com.geohash.model.Point;
import com.github.davidmoten.geo.Coverage;
import com.github.davidmoten.geo.GeoHash;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@AxisRange(min = 0, max = 1)
@BenchmarkMethodChart(filePrefix = "geo_data")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GeohashDaoPerfTest extends AbstractBenchmark {

    private static final Logger LOGGER = LogManager.getLogger(GeohashDaoPerfTest.class.getName());

    private static CassConfig config;

    private static GeoApi api;
    private static GeohashDao dao;

    // sample data on which query are run against. It's 664692 geohashes
    private static final Point topLeft = new Point(14.51293945, 54.45726668);
    private static final Point bottomRight = new Point(16.10046387, 53.67068019);

    // expected number of elements = 3894
    private static final int GEO_DATA_NUM = 3894;
    private static final Point queryTopLeft = new Point(15.25713806, 54.0795342);
    private static final Point queryBottomRight = new Point(15.34584045, 53.99997426);

    // expected number of elements = 273
    private static final int SM_GEO_DATA_NUM = 273;
    private static final Point smQueryTopLeft = new Point(14.69790459, 53.92486286);
    private static final Point smQueryBottomRight = new Point(14.72498417, 53.90787762);

    // expected number of elements = 12412
    private static final int BIG_GEO_DATA_NUM = 12412;
    private static final Point bigQueryTopLeft = new Point(15.25588989, 54.05616357);
    private static final Point bigQueryBottomRight = new Point(15.41244507, 53.91081009);

    public static final long NOW = new Date().getTime();

    @BeforeClass
    public static void setup() {
        config = new CassConfig();
        dao = new GeohashDao(config);
        api = new GeoApi(dao);

        if (config.insertSamples()) {
            LOGGER.info("Inserting sample data!");
            dao.truncateColumnFamily();
            insertSampleData();
        }
    }

    private static void insertSampleData() {
        Coverage cov = GeoHash.coverBoundingBox(topLeft.getLatitude(), topLeft.getLongitude(), bottomRight.getLatitude(), bottomRight.getLongitude(), 7);//GeoApi.GEO_HASH_LENGTH);
        LOGGER.info("Number of geohashes to insert: " + cov.getHashes().size());

        cov.getHashes().forEach(p -> insert(p));
    }

    private static void insert(String geohash) {
        GeoData data = new GeoData();
        data.setGeoHash(geohash);
        data.setNumberOfMeasRsrp(12);
        data.setNumberOfMeasRsrq(32);
        data.setTotalRsrp(231242151L);
        data.setTotalRsrq(451245217L);
        data.setTimestamp(NOW);

        dao.batchSave(data);
    }

    // tests for average rectangle
    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void mid_singleGeoHash() {
        assertThat(api.getCoverageV1(queryTopLeft, queryBottomRight).size(), is(GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void mid_singleGeoHash_parallel() {
        assertThat(api.getCoverageV1_parallel(queryTopLeft, queryBottomRight).size(), is(GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void mid_manyGeoHashes_allInBatch() {
        assertThat(api.getCoverageV2(queryTopLeft, queryBottomRight).size(), is(GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void mid_manyGeoHash_smaller_batches() {
        assertThat(api.getCoverageV2_smallBatches(queryTopLeft, queryBottomRight).size(), is(GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void mid_singleGeoHash_rx() {
        assertThat(api.getCoverageV3_rx(queryTopLeft, queryBottomRight).size(), is(GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void mid_manyGeoHashes_rx() {
        assertThat(api.getCoverageV3_rx_many(queryTopLeft, queryBottomRight).size(), is(GEO_DATA_NUM));
    }

    // tests for small rectangle
    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void sm_singleGeoHash() {
        assertThat(api.getCoverageV1(smQueryTopLeft, smQueryBottomRight).size(), is(SM_GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void sm_singleGeoHash_parallel() {
        assertThat(api.getCoverageV1_parallel(smQueryTopLeft, smQueryBottomRight).size(), is(SM_GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void sm_manyGeoHashes_allInBatch() {
        assertThat(api.getCoverageV2(smQueryTopLeft, smQueryBottomRight).size(), is(SM_GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void sm_manyGeoHash_smaller_batches() {
        assertThat(api.getCoverageV2_smallBatches(smQueryTopLeft, smQueryBottomRight).size(), is(SM_GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void sm_singleGeoHash_rx() {
        assertThat(api.getCoverageV3_rx(smQueryTopLeft, smQueryBottomRight).size(), is(SM_GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void sm_manyGeoHashes_rx() {
        assertThat(api.getCoverageV3_rx_many(smQueryTopLeft, smQueryBottomRight).size(), is(SM_GEO_DATA_NUM));
    }

    // tests for big rectangle
    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void big_singleGeoHash() {
        assertThat(api.getCoverageV1(bigQueryTopLeft, bigQueryBottomRight).size(), is(BIG_GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void big_singleGeoHash_parallel() {
        assertThat(api.getCoverageV1_parallel(bigQueryTopLeft, bigQueryBottomRight).size(), is(BIG_GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void big_manyGeoHashes_allInBatch() {
        assertThat(api.getCoverageV2(bigQueryTopLeft, bigQueryBottomRight).size(), is(BIG_GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void big_manyGeoHash_smaller_batches() {
        assertThat(api.getCoverageV2_smallBatches(bigQueryTopLeft, bigQueryBottomRight).size(), is(BIG_GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void big_singleGeoHash_rx() {
        assertThat(api.getCoverageV3_rx(bigQueryTopLeft, bigQueryBottomRight).size(), is(BIG_GEO_DATA_NUM));
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 0)
    public void big_manyGeoHashes_rx() {
        assertThat(api.getCoverageV3_rx_many(bigQueryTopLeft, bigQueryBottomRight).size(), is(BIG_GEO_DATA_NUM));
    }
}
