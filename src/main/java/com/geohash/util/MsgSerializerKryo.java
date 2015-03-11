package com.geohash.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.geohash.message.Enums;
import com.geohash.message.Message;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * User: mdyminski
 */
public class MsgSerializerKryo implements ISerializer<Message> {

    private boolean compression;

    public MsgSerializerKryo(boolean compression) {
        this.compression = compression;
    }

    public MsgSerializerKryo() {
        this.compression = false;
    }

    @Override
    public ByteBuffer toBuffer(Message toSerialize) {
        int size = 32 + toSerialize.getData().length;
        Output output = new Output(10000, 10000);

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
    public Message fromBuffer(ByteBuffer buffer) {
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
        Message deserialized = KRYO.get().readObject(input, Message.class);
        input.close();

        return deserialized;
    }

    private static final ThreadLocal<Kryo> KRYO = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo k = new Kryo();
            k.register(Message.class, new JavaSerializer());
            return k;
        }
    };


    private static class MsgSerializer extends Serializer<Message> {

        public void write (Kryo kryo, Output output, Message msg) {
            output.writeInt(msg.getTraceType().value());
            output.writeInt(msg.getDirection().value());
            output.writeInt(msg.getInterfaceName().value());
            output.writeInt(msg.getProtocol().value());
            output.writeInt(msg.getProtocolFormat().value());
            output.writeInt(msg.getProtocolMessageType().value());
            output.writeInt(msg.getData().length);
            output.writeBytes(msg.getData());
        }

        public Message read (Kryo kryo, Input input, Class<Message> type) {
            Message  msg = new Message();
            msg.setTraceType(Enums.TraceType.fromValue(input.readInt()));
            msg.setDirection(Enums.Direction.fromValue(input.readInt()));
            msg.setInterfaceName(Enums.Interface.fromValue(input.readInt()));
            msg.setProtocol(Enums.TraceProtocol.fromValue(input.readInt()));
            msg.setProtocolFormat(Enums.ProtocolFormat.fromValue(input.readInt()));
            msg.setProtocolMessageType(Enums.ProtocolMessageType.fromValue(input.readInt(),msg.getProtocol()));
            msg.setData(input.readBytes(input.readInt()));
            return msg;
        }
    }
}
