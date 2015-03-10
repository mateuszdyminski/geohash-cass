package com.geohash.api;

import com.geohash.dao.GeohashDao;
import com.geohash.model.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class GeoApiTest {

    private static final Logger LOGGER = LogManager.getLogger(GeoApiTest.class.getName());

    private static final int SM_GEO_DATA_NUM = 273;
    private static final Point smQueryTopLeft = new Point(14.69790459, 53.92486286);
    private static final Point smQueryBottomRight = new Point(14.72498417, 53.90787762);

    private static final Point bigQueryTopLeft = new Point(15.25588989, 54.05616357);
    private static final Point bigQueryBottomRight = new Point(15.41244507, 53.91081009);

    private GeoApi api = new GeoApi(null);

    @Test
    public void getCoverageInGeoHash_sm() {
        // when
        Set<String> hashes = api.getCoverageInGeoHash(smQueryTopLeft, smQueryBottomRight, 7);

        // then
        LOGGER.info("Number of hashes: " + hashes.size());
        for (String s : hashes) {
            LOGGER.info("Hash: " + s);
        }
    }

    @Test
    public void getCoverageInGeoHash_big() {
        // when
        Set<String> hashes = api.getCoverageInGeoHash(bigQueryTopLeft, bigQueryBottomRight, 7);

        // then
        LOGGER.info("Number of hashes: " + hashes.size());
        for (String s : hashes) {
            LOGGER.info("Hash: " + s);
        }
    }
}