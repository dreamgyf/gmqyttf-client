package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.exception.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;

import java.util.concurrent.Executor;

public abstract class MqttService {

    private final MqttVersion mVersion;

    private final MqttWritableSocket mSocket;

    private final Executor mThreadPool;

    private OnMqttExceptionListener mListener;

    public MqttService(MqttVersion version, MqttWritableSocket socket, Executor threadPool) {
        mVersion = version;
        mSocket = socket;
        mThreadPool = threadPool;
    }

    protected void runOnNewThread(Runnable runnable) {
        mThreadPool.execute(runnable);
    }

    public OnMqttExceptionListener getOnMqttExceptionListener() {
        return mListener;
    }

    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        mListener = listener;
    }

    public abstract void start();

    public abstract void stop();
}
