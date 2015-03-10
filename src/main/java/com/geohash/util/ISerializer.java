package com.geohash.util;

import java.nio.ByteBuffer;

public interface ISerializer<T> {
    ByteBuffer toBuffer(T toSerialize);
    T fromBuffer(ByteBuffer buffer);
}