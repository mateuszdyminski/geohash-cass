package com.geohash.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
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
            k.register(Message.class, new MsgSerializer());
            return k;
        }
    };


    private static class MsgSerializer extends Serializer<Message> {

        public void write (Kryo kryo, Output output, Message msg) {
            output.writeInt(msg.getTraceType());
            output.writeInt(msg.getDirection());
            output.writeInt(msg.getInterfaceName());
            output.writeInt(msg.getProtocol());
            output.writeInt(msg.getProtocolFormat());
            output.writeInt(msg.getProtocolMessageType());
            output.writeInt(msg.getData().length);
            output.writeBytes(msg.getData());
        }

        public Message read (Kryo kryo, Input input, Class<Message> type) {
            Message  msg = new Message();

            msg.setTraceType(input.readInt());
            msg.setDirection(input.readInt());
            msg.setInterfaceName(input.readInt());
            msg.setProtocol(input.readInt());
            msg.setProtocolFormat(input.readInt());
            msg.setProtocolMessageType(input.readInt());
            msg.setData(input.readBytes(input.readInt()));

            return msg;
        }
    }
}
