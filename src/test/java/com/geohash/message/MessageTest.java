package com.geohash.message;

import com.geohash.util.ISerializer;
import com.geohash.util.MsgSerializerKryo;
import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MessageTest {


    private ISerializer<Message> serializer = new MsgSerializerKryo();

    @Test
    public void serializationNetty_shouldSerializeAllFields() {
        // given
        Message msg = prepareMsg();

        // when
        ByteBuffer bb = Message.encode(msg);
        System.out.println("Netty object size: " + bb.capacity());
        Message msg1 = Message.decode(bb);

        // then
        assertThat(msg.getCallId(), is(msg1.getCallId()));
        assertThat(msg.getTraceType(), is(msg1.getTraceType()));
        assertThat(msg.getCrnti(), is(msg1.getCrnti()));
        assertThat(msg.getData(), is(msg1.getData()));
        assertThat(msg.getProtocolMessageType(), is(msg1.getProtocolMessageType()));
        assertThat(msg.getProtocolFormat(), is(msg1.getProtocolFormat()));
        assertThat(msg.getProtocol(), is(msg1.getProtocol()));
    }

    @Test
    public void serializationKryo_snappy() {
        Message msg = prepareMsg();
        ByteBuffer bb1 = serializer.toBuffer(msg);
        System.out.println("Message size - uncompressed: " + bb1.capacity());

        serializer = new MsgSerializerKryo(true);
        ByteBuffer bb2 = serializer.toBuffer(msg);

        System.out.println("Message size - compressed: " + bb2.capacity());
    }

    @Test
    public void serializationKryo_shouldSerializeAllFields() {
        // given
        Message msg = prepareMsg();

        // when
        ByteBuffer bb = serializer.toBuffer(msg);
        System.out.println("Kryo object size: " + bb.capacity());
        Message msg1 = serializer.fromBuffer(bb);

        // then
        assertThat(msg.getCallId(), is(msg1.getCallId()));
        assertThat(msg.getTraceType(), is(msg1.getTraceType()));
        assertThat(msg.getCrnti(), is(msg1.getCrnti()));
        assertThat(msg.getData(), is(msg1.getData()));
        assertThat(msg.getProtocolMessageType(), is(msg1.getProtocolMessageType()));
        assertThat(msg.getProtocolFormat(), is(msg1.getProtocolFormat()));
        assertThat(msg.getProtocol(), is(msg1.getProtocol()));
    }

    @Test
    public void serializationPertTest_kryo() {
        // given
        int loopRound = 1000000;
        Message msg = prepareMsg();

        // when
        Stopwatch sw = Stopwatch.createStarted();
        for (int i = 0; i < loopRound; i++) {
            ByteBuffer bb = serializer.toBuffer(msg);
            serializer.fromBuffer(bb);
        }

        // then
        System.out.println("Kryo Serialize + deserialize " + loopRound + " times took: " + sw.elapsed(TimeUnit.MILLISECONDS) + " ms");
    }

    private Message prepareMsg() {
        Message msg = new Message();
        msg.setData("00010060000008000000020000000100010000024002000000420004000000000035000E00001B00090100616263640000000068000100006B0004200080000028002100AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes());
        msg.setTraceType(Enums.TraceType.CELL_TRACE);
        msg.setDirection(Enums.Direction.DOWN);
        msg.setInterfaceName(Enums.Interface.E_UU);
        msg.setProtocol(Enums.TraceProtocol.MAC);
        msg.setProtocolFormat(Enums.ProtocolFormat.AMD_PDU);
        msg.setProtocolMessageType(Enums.ProtocolMessageType.fromValue(1,msg.getProtocol()));

        return msg;
    }

    @Test
    public void serializationPertTest_netty() {
        // given
        int loopRound = 1000000;
        Message msg = prepareMsg();

        // when
        Stopwatch sw = Stopwatch.createStarted();
        for (int i = 0; i < loopRound; i++) {
            ByteBuffer bb = Message.encode(msg);
            Message msg1 = Message.decode(bb);
        }

        // then
        System.out.println("Netty Serialize + deserialize " + loopRound + " times took: " + sw.elapsed(TimeUnit.MILLISECONDS) + " ms");
    }
}