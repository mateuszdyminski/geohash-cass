package com.geohash.api;

import com.geohash.dao.GeohashDao;
import com.geohash.model.GeoData;
import com.geohash.model.Point;
import com.github.davidmoten.geo.Coverage;
import com.github.davidmoten.geo.GeoHash;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * User: mdyminski
 */
public class GeoApi {

    private static final Logger LOGGER = LogManager.getLogger(GeoApi.class.getName());

    public static final int GEO_HASH_LENGTH = 7;

    private final GeohashDao dao;

    public GeoApi(GeohashDao dao) {
        this.dao = dao;
    }

    public Set<String> getCoverageInGeoHash(Point topLeft, Point bottomRight, int level) {
        return GeoHash.coverBoundingBox(topLeft.getLatitude(), topLeft.getLongitude(), bottomRight.getLatitude(), bottomRight.getLongitude(), level).getHashes();
    }

    public List<GeoData> getCoverageV1(Point topLeft, Point bottomRight) {
        Coverage cov = GeoHash.coverBoundingBox(topLeft.getLatitude(), topLeft.getLongitude(), bottomRight.getLatitude(), bottomRight.getLongitude(), GEO_HASH_LENGTH);

        List<GeoData> datas = new ArrayList<>();
        cov.getHashes().forEach(p -> datas.addAll(dao.get(p, 0, new Date().getTime())));

        return datas;
    }

    public List<GeoData> getCoverageV1_parallel(Point topLeft, Point bottomRight) {
        Coverage cov = GeoHash.coverBoundingBox(topLeft.getLatitude(), topLeft.getLongitude(), bottomRight.getLatitude(), bottomRight.getLongitude(), GEO_HASH_LENGTH);

        List<GeoData> datas = Collections.synchronizedList(new ArrayList<>());
        cov.getHashes().parallelStream().forEach(p -> datas.addAll(dao.get(p, 0, new Date().getTime())));

        return datas;
    }

    public List<GeoData> getCoverageV2(Point topLeft, Point bottomRight) {
        Coverage cov = GeoHash.coverBoundingBox(topLeft.getLatitude(), topLeft.getLongitude(), bottomRight.getLatitude(), bottomRight.getLongitude(), GEO_HASH_LENGTH);

        List<GeoData> datas = dao.getForMany(new ArrayList<>(cov.getHashes()), 0l, new Date().getTime());

        return datas;
    }

    public List<GeoData> getCoverageV2_smallBatches(Point topLeft, Point bottomRight) {
        Coverage cov = GeoHash.coverBoundingBox(topLeft.getLatitude(), topLeft.getLongitude(), bottomRight.getLatitude(), bottomRight.getLongitude(), GEO_HASH_LENGTH);

        List<List<String>> listOfLists = new ArrayList<>();
        List<String> list = new ArrayList<>();
        int i = 0;
        for (String s : cov.getHashes()) {
            if (i != 0 && i % 100 == 0) {
                listOfLists.add(list);
                list = new ArrayList<>();
            }

            list.add(s);
        }
        listOfLists.add(list);

        List<GeoData> datas = new ArrayList<>();

        listOfLists.forEach(p -> datas.addAll(dao.getForMany(p, 0l, new Date().getTime())));

        return datas;
    }

    public List<GeoData> getCoverageV2_smallBatches_parallel(Point topLeft, Point bottomRight) {
        Coverage cov = GeoHash.coverBoundingBox(topLeft.getLatitude(), topLeft.getLongitude(), bottomRight.getLatitude(), bottomRight.getLongitude(), GEO_HASH_LENGTH);

        List<List<String>> listOfLists = new ArrayList<>();
        List<String> list = new ArrayList<>();
        int i = 0;
        for (String s : cov.getHashes()) {
            if (i != 0 && i % 100 == 0) {
                listOfLists.add(list);
                list = new ArrayList<>();
            }

            list.add(s);
        }
        listOfLists.add(list);

        List<GeoData> datas = Collections.synchronizedList(new ArrayList<>());
        listOfLists.parallelStream().forEach(p -> datas.addAll(dao.getForMany(p, 0l, new Date().getTime())));

        return datas;
    }

    public List<GeoData> getCoverageV3_rx(Point topLeft, Point bottomRight) {
        Coverage cov = GeoHash.coverBoundingBox(topLeft.getLatitude(), topLeft.getLongitude(), bottomRight.getLatitude(), bottomRight.getLongitude(), GEO_HASH_LENGTH);

        List<GeoData> datas = new ArrayList<>();
        datas.addAll(dao.getRx(new ArrayList<>(cov.getHashes()), 0l, new Date().getTime()));

        return datas;
    }

    public List<GeoData> getCoverageV3_rx_many(Point topLeft, Point bottomRight) {
        Coverage cov = GeoHash.coverBoundingBox(topLeft.getLatitude(), topLeft.getLongitude(), bottomRight.getLatitude(), bottomRight.getLongitude(), GEO_HASH_LENGTH);

        List<List<String>> listOfLists = new ArrayList<>();
        List<String> list = new ArrayList<>();
        int i = 0;
        for (String s : cov.getHashes()) {
            if (i != 0 && i % 100 == 0) {
                listOfLists.add(list);
                list = new ArrayList<>();
            }

            list.add(s);
        }
        listOfLists.add(list);

        return dao.getManyRx(listOfLists, 0l, new Date().getTime());
    }
}
