package com.geohash.util;

import com.geohash.model.GeoData;
import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SerializerNettyTest {

    private ISerializer<GeoData> serializer = new GeoSerializerNetty();

    @Test
    public void perfTest() {
        // given
        int loopRound = 10000000;
        GeoData geo = prepareData();

        // when
        Stopwatch sw = Stopwatch.createStarted();
        for (int i = 0; i < loopRound; i++) {
            ByteBuffer bb = serializer.toBuffer(geo);
            serializer.fromBuffer(bb);
        }

        // then
        System.out.println("Netty Serialize + deserialize " + loopRound + " times took: " + sw.elapsed(TimeUnit.MILLISECONDS) + " ms");
    }

    private GeoData prepareData() {
        GeoData data = new GeoData();
        data.setGeoHash("asdfghj");
        data.setNumberOfMeasRsrp(12);
        data.setNumberOfMeasRsrq(32);
        data.setTotalRsrp(231242151L);
        data.setTotalRsrq(451245217L);
        data.setTimestamp(new Date().getTime());

        return data;
    }
}