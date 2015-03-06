package com.geohash.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Serializer {

    public byte[] toBytes(Object toSerialize) {
        Output output = new Output(204800, -1);
        KRYO.get().writeObject(output, toSerialize);
        output.close();
        return output.toBytes();
    }

    public <T> T fromBytes(byte[] bytes, Class<T> clazz) {
        Input input = new Input(bytes);
        T deserialized = KRYO.get().readObject(input, clazz);
        input.close();
        return deserialized;
    }

    private static final ThreadLocal<Kryo> KRYO = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            return new Kryo();
        }
    };
}
