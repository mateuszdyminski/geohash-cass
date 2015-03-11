package com.geohash.util;


import java.nio.ByteBuffer;

import com.geohash.model.GeoData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.google.common.base.Charsets;

/**
 * User: mdyminski
 */
public class GeoSerializerNetty implements ISerializer<GeoData> {

    private static final int TOTAL_SIZE = 36;

    @Override
    public ByteBuffer toBuffer(GeoData geo) {

        byte[] hash = geo.getGeoHash().getBytes(Charsets.US_ASCII);

        ByteBuf buffer = Unpooled.buffer(TOTAL_SIZE + + hash.length);

        buffer.writeInt(hash.length);
        buffer.writeBytes(hash);
        buffer.writeLong(geo.getTimestamp());
        buffer.writeLong(geo.getTotalRsrp());
        buffer.writeLong(geo.getTotalRsrq());
        buffer.writeInt(geo.getNumberOfMeasRsrp());
        buffer.writeInt(geo.getNumberOfMeasRsrq());

        return buffer.nioBuffer();
    }

    @Override
    public GeoData fromBuffer(ByteBuffer buffer) {

        GeoData geo = new GeoData();
        ByteBuf buf = Unpooled.wrappedBuffer(buffer);

        int hashLength = buf.readInt();
        String geoHash = buf.toString(buf.readerIndex(), hashLength, Charsets.US_ASCII);
        buf.skipBytes(hashLength);
        geo.setGeoHash(geoHash);
        geo.setTimestamp(buf.readLong());
        geo.setTotalRsrp(buf.readLong());
        geo.setTotalRsrq(buf.readLong());
        geo.setNumberOfMeasRsrp(buf.readInt());
        geo.setNumberOfMeasRsrq(buf.readInt());

        return geo;
    }
}