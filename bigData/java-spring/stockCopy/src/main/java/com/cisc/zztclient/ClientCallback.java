package com.cisc.zztclient;

public interface ClientCallback {
    void call(Object var1);

    void error(Throwable var1);
}