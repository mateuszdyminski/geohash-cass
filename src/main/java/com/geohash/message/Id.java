package com.geohash.message;

import com.google.common.base.Splitter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * User: mdyminski
 */
public final class Id implements Serializable {

    private static final long serialVersionUID = 3030161700978779654L;

    private static final int MILION = 1000000;

    public static final String SEPARATOR = "$";

    private static final Splitter SPLITTER = Splitter.on(SEPARATOR).trimResults().omitEmptyStrings();

    private int traceRecordingSessionId;

    private int traceId;

    private long eNodeBId;

    private long timeInMicroseconds;

    public Id() {
    }

    public static Id make(long timeInMicroseconds, long eNodeBId, int traceRecordingSessionId, int traceId) {
        Id id = new Id();
        id.setTimeInMicroseconds(timeInMicroseconds);
        id.seteNodeBId(eNodeBId);
        id.setTraceRecordingSessionId(traceRecordingSessionId);
        id.setTraceId(traceId);
        return id;
    }

    public static Id make(String callId) {
        Iterator<String> iterator = SPLITTER.split(callId).iterator();
        return parseStringsToUniqueCallId(callId, iterator);
    }

    private static Id parseStringsToUniqueCallId(String callId, Iterator<String> iterator) {
        try {
            return make(Long.parseLong(iterator.next()),
                    Long.parseLong(iterator.next()),
                    Integer.parseInt(iterator.next()),
                    Integer.parseInt(iterator.next()));
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Cannot create UniqueCallId object! Wrong callId: " + callId, e);
        }
    }

    public int getTraceRecordingSessionId() {
        return traceRecordingSessionId;
    }

    public void setTraceRecordingSessionId(int traceRecordingSessionId) {
        this.traceRecordingSessionId = traceRecordingSessionId;
    }

    public int getTraceId() {
        return traceId;
    }

    public void setTraceId(int traceId) {
        this.traceId = traceId;
    }

    public long geteNodeBId() {
        return eNodeBId;
    }

    public void seteNodeBId(long eNodeBId) {
        this.eNodeBId = eNodeBId;
    }

    public long getTimeInMicroseconds() {
        return timeInMicroseconds;
    }

    public void setTimeInMicroseconds(long timeInMicroseconds) {
        this.timeInMicroseconds = timeInMicroseconds;
    }

    public void setTime(long seconds, long microseconds) {
        timeInMicroseconds = seconds * MILION;
        timeInMicroseconds += microseconds;
    }

    public long getMicroseconds() {
        return timeInMicroseconds % MILION;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Id)) {
            return false;
        }
        Id other = (Id) o;
        return traceId == other.getTraceId() && traceRecordingSessionId == other.getTraceRecordingSessionId()
                && eNodeBId == other.geteNodeBId() && timeInMicroseconds == other.getTimeInMicroseconds();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{traceRecordingSessionId, traceId, eNodeBId, timeInMicroseconds});
    }

    @Override
    public String toString() {
        return idAsString();
    }

    public String idAsString() {
        return timeInMicroseconds + SEPARATOR + eNodeBId + SEPARATOR + traceRecordingSessionId + SEPARATOR + traceId;
    }
}
