package com.geohash.dao;

import com.geohash.model.CassConfig;
import com.geohash.model.GeoData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GeohashDaoTest {

    private static final String GEO_HASH = "G345RE2342";

    public static final long NOW = new Date().getTime();

    private GeohashDao geohashDao;

    @Before
    public void setup() {
        CassConfig config = new CassConfig();
        geohashDao = new GeohashDao(config);
        geohashDao.truncateColumnFamily();
    }

    @Test
    public void save_shouldSaveGeoHash() {
        // given
        GeoData geoData = prepareData(GEO_HASH);

        // when
        geohashDao.save(geoData);

        // then
        GeoData inserted = geohashDao.get(GEO_HASH, NOW - 10, NOW + 10).get(0);
        assertThat(inserted.getGeoHash(), is(geoData.getGeoHash()));
        assertThat(inserted.getNumberOfMeasRsrp(), is(geoData.getNumberOfMeasRsrp()));
        assertThat(inserted.getNumberOfMeasRsrq(), is(geoData.getNumberOfMeasRsrq()));
        assertThat(inserted.getTimestamp(), is(geoData.getTimestamp()));
        assertThat(inserted.getTotalRsrp(), is(geoData.getTotalRsrp()));
        assertThat(inserted.getTotalRsrq(), is(geoData.getTotalRsrq()));
    }

    @Test
    public void batchSave_shouldSaveGeoSurveys() {
        // given
        List<GeoData> geoDatas = new ArrayList<>();

        for (int i = 0; i < 101; i++) {
            geoDatas.add(prepareData(GEO_HASH + i));
        }

        // when
        for (GeoData geoData: geoDatas) {
            geohashDao.batchSave(geoData);
        }

        // then
        Assert.assertThat(geohashDao.slice(NOW - 10, NOW + 10).size(), is(100));
    }

    @Test
    public void getForMany_shouldGetForManyGeoHashes() {
        // given
        List<String> hashes = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            hashes.add(GEO_HASH + i);
            geohashDao.save(prepareData(GEO_HASH + i));
        }

        // when
        List<GeoData> datas = geohashDao.getForMany(hashes, NOW - 10, NOW + 10);

        // then
        Assert.assertThat(datas.size(), is(11));
    }

    @Test
    public void getRx_shouldGetAllRecords() {
        // given
        List<String> hashes = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            hashes.add(GEO_HASH + i);
            geohashDao.save(prepareData(GEO_HASH + i));
        }

        // when
        List<GeoData> datas = geohashDao.getRx(hashes, NOW - 10, NOW + 10);

        // then
        Assert.assertThat(datas.size(), is(11));
    }

    private GeoData prepareData(String geoHash) {
        GeoData data = new GeoData();
        data.setGeoHash(geoHash);
        data.setNumberOfMeasRsrp(12);
        data.setNumberOfMeasRsrq(32);
        data.setTotalRsrp(231242151L);
        data.setTotalRsrq(451245217L);
        data.setTimestamp(NOW);

        return data;
    }
}
