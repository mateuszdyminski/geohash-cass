package com.geohash.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * User: mdyminski
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -7315952139250806790L;

    private long id;
    private byte[] data;
    private int traceType;
    private int direction;
    private int interfaceName;
    private int protocol;
    private int protocolFormat;
    private int protocolMessageType;

    public Message() {}

    public long getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getTraceType() {
        return traceType;
    }

    public void setTraceType(int traceType) {
        this.traceType = traceType;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(int interfaceName) {
        this.interfaceName = interfaceName;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public int getProtocolFormat() {
        return protocolFormat;
    }

    public void setProtocolFormat(int protocolFormat) {
        this.protocolFormat = protocolFormat;
    }

    public int getProtocolMessageType() {
        return protocolMessageType;
    }

    public void setProtocolMessageType(int protocolMessageType) {
        this.protocolMessageType = protocolMessageType;
    }

    public static ByteBuffer encode(Message msg) {
        int totalSize =  28 + msg.data.length;
        ByteBuf buffer = Unpooled.buffer(totalSize);
        buffer.writeInt(msg.getTraceType());
        buffer.writeInt(msg.getDirection());
        buffer.writeInt(msg.getInterfaceName());
        buffer.writeInt(msg.getProtocol());
        buffer.writeInt(msg.getProtocolFormat());
        buffer.writeInt(msg.getProtocolMessageType());
        buffer.writeInt(msg.data.length);
        buffer.writeBytes(msg.data);
        return buffer.nioBuffer();
    }

    public static Message decode(ByteBuffer inBuffer) {
        Message  msg = new Message();
        ByteBuf buffer = Unpooled.wrappedBuffer(inBuffer);
        msg.setTraceType(buffer.readInt());
        msg.setDirection(buffer.readInt());
        msg.setInterfaceName(buffer.readInt());
        msg.setProtocol(buffer.readInt());
        msg.setProtocolFormat(buffer.readInt());
        msg.setProtocolMessageType(buffer.readInt());
        msg.setData(buffer.readBytes(buffer.readInt()).array());
        return msg;
    }
}
