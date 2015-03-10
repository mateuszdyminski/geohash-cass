package com.geohash.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * User: mdyminski
 */
public class Message implements Serializable {

    private static final int THOUSAND = 1000;

    private static final long serialVersionUID = -7315952139250806790L;
    private static final int INT_SIZE = Integer.SIZE / Byte.SIZE;

    private Id callId;
    private long seconds;
    private long microseconds;
    private byte[] data;
    private Enums.TraceType traceType;
    private Enums.Direction direction;
    private Enums.Interface interfaceName;
    private Enums.TraceProtocol protocol;
    private Enums.ProtocolFormat protocolFormat;
    private Enums.ProtocolMessageType protocolMessageType;
    private long crnti;

    public Message() {}

    public Id getCallId() {
        return callId;
    }

    public void setCallId(Id callId) {
        this.callId = callId;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public long getMicroseconds() {
        return microseconds;
    }

    public void setMicroseconds(long microseconds) {
        this.microseconds = microseconds;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data.clone();
    }

    public Enums.TraceType getTraceType() {
        return traceType;
    }

    public void setTraceType(Enums.TraceType traceType) {
        this.traceType = traceType;
    }

    public Enums.Direction getDirection() {
        return direction;
    }

    public void setDirection(Enums.Direction direction) {
        this.direction = direction;
    }

    public Enums.Interface getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(Enums.Interface interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Enums.TraceProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Enums.TraceProtocol protocol) {
        this.protocol = protocol;
    }

    public Enums.ProtocolFormat getProtocolFormat() {
        return protocolFormat;
    }

    public void setProtocolFormat(Enums.ProtocolFormat protocolFormat) {
        this.protocolFormat = protocolFormat;
    }

    public long getCrnti() {
        return crnti;
    }

    public void setCrnti(long crnti) {
        this.crnti = crnti;
    }

    public Enums.ProtocolMessageType getProtocolMessageType() {
        return protocolMessageType;
    }

    public void setProtocolMessageType(Enums.ProtocolMessageType protocolMessageType) {
        this.protocolMessageType = protocolMessageType;
    }

    public long getMsgTimeInMillis(){
        long timeInMillis= seconds * THOUSAND;
        timeInMillis += microseconds / THOUSAND;
        return timeInMillis;
    }

    /**
     * encode fields without callId, seconds and microseconds
     *
     * @param msg
     * @return
     */
    public static ByteBuffer encode(Message msg) {
        int totalSize =  INT_SIZE *6 + INT_SIZE + msg.data.length;
        ByteBuf buffer = Unpooled.buffer(totalSize);
        buffer.writeInt(msg.getTraceType().value());
        buffer.writeInt(msg.getDirection().value());
        buffer.writeInt(msg.getInterfaceName().value());
        buffer.writeInt(msg.getProtocol().value());
        buffer.writeInt(msg.getProtocolFormat().value());
        buffer.writeInt(msg.getProtocolMessageType().value());
        buffer.writeInt(msg.data.length);
        buffer.writeBytes(msg.data);
        return buffer.nioBuffer();
    }

    /**
     * decode msg without callId, seconds and microseconds
     */
    public static Message decode(ByteBuffer inBuffer) {
        Message  msg = new Message();
        ByteBuf buffer = Unpooled.wrappedBuffer(inBuffer);
        msg.setTraceType(Enums.TraceType.fromValue(buffer.readInt()));
        msg.setDirection(Enums.Direction.fromValue(buffer.readInt()));
        msg.setInterfaceName(Enums.Interface.fromValue(buffer.readInt()));
        msg.setProtocol(Enums.TraceProtocol.fromValue(buffer.readInt()));
        msg.setProtocolFormat(Enums.ProtocolFormat.fromValue(buffer.readInt()));
        msg.setProtocolMessageType(Enums.ProtocolMessageType.fromValue(buffer.readInt(),msg.protocol));
        msg.setData(buffer.readBytes(buffer.readInt()).array());
        return msg;
    }
}