package com.hazelcast.msf.messaging;

import java.io.Serializable;

public class APIResponse<T> implements Serializable {
    public enum Status { SUCCESS, FAILURE, CANCELED }

    private long uniqueID;
    private Status status;
    private Throwable error;
    private T result;

    public APIResponse(long uniqueID, T result) {
        this.uniqueID = uniqueID;
        this.status = Status.SUCCESS;
        this.result = result;
    }

    public APIResponse(long uniqueID, Throwable error) {
        this.uniqueID = uniqueID;
        this.status = Status.FAILURE;
        this.error = error;
    }

    public long getUniqueID() { return uniqueID; }
    public Throwable getError() { return error; }
    public Status getStatus() { return status; }
    public T getResultValue() { return result; }
}
