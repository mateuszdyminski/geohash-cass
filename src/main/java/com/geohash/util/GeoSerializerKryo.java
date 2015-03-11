package com.geohash.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.geohash.model.GeoData;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.ByteBuffer;

public class GeoSerializerKryo implements ISerializer<GeoData> {

    private boolean compression;

    public GeoSerializerKryo(boolean compression) {
        this.compression = compression;
    }

    @Override
    public ByteBuffer toBuffer(GeoData toSerialize) {
        int size = 36 + toSerialize.getGeoHash().length();
        Output output = new Output(size, size);

        KRYO.get().writeObject(output, toSerialize);
        output.close();

        try {
            if (compression) {
                return ByteBuffer.wrap(Snappy.compress(output.toBytes()));
            } else {
                return ByteBuffer.wrap(output.toBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Dupa");
        }
    }

    @Override
    public GeoData fromBuffer(ByteBuffer buffer) {
        byte[] b = new byte[buffer.remaining()];
        buffer.get(b);

        Input input;
        try {
            if (compression) {
                input = new Input(Snappy.uncompress(b));
            } else {
                input = new Input(b);
            }
        } catch (IOException e) {
            throw new RuntimeException("Dupa");
        }
        GeoData deserialized = KRYO.get().readObject(input, GeoData.class);
        input.close();

        return deserialized;
    }

    private static final ThreadLocal<Kryo> KRYO = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo k = new Kryo();
            k.register(GeoData.class, new GeoSerializer());
            return k;
        }
    };

    private static class GeoSerializer extends Serializer<GeoData> {

        public void write (Kryo kryo, Output output, GeoData geo) {
            output.writeString(geo.getGeoHash());
            output.writeLong(geo.getTimestamp());
            output.writeLong(geo.getTotalRsrp());
            output.writeLong(geo.getTotalRsrq());
            output.writeInt(geo.getNumberOfMeasRsrp());
            output.writeInt(geo.getNumberOfMeasRsrq());

        }

        public GeoData read (Kryo kryo, Input input, Class<GeoData> type) {
            GeoData geo = new GeoData();
            geo.setGeoHash(input.readString());
            geo.setTimestamp(input.readLong());
            geo.setTotalRsrp(input.readLong());
            geo.setTotalRsrq(input.readLong());
            geo.setNumberOfMeasRsrp(input.readInt());
            geo.setNumberOfMeasRsrq(input.readInt());

            return geo;
        }
    }
}
