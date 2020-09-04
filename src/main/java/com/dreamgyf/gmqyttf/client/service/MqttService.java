package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttPacketSendListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.throwable.exception.MqttException;

import java.util.concurrent.Executor;

public abstract class MqttService {

    private final MqttVersion mVersion;

    private final MqttWritableSocket mSocket;

    private final Executor mThreadPool;

    private OnMqttExceptionListener mExceptionListener;

    private OnMqttPacketSendListener mPacketSendListener;

    public MqttService(MqttVersion version, MqttWritableSocket socket, Executor threadPool) {
        mVersion = version;
        mSocket = socket;
        mThreadPool = threadPool;
    }

    public abstract void initTask();

    protected void runOnNewThread(Runnable runnable) {
        mThreadPool.execute(runnable);
    }

    public MqttVersion getVersion() {
        return mVersion;
    }

    protected MqttWritableSocket getSocket() {
        return mSocket;
    }

    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        mExceptionListener = listener;
    }

    public void setOnPacketSendListener(OnMqttPacketSendListener listener) {
        mPacketSendListener = listener;
    }

    protected void onMqttExceptionThrow(MqttException e) {
        if (mExceptionListener != null) {
            mExceptionListener.onMqttExceptionThrow(e);
        }
    }

    protected void onMqttPacketSend() {
        if (mPacketSendListener != null) {
            mPacketSendListener.onPacketSend();
        }
    }

    public abstract void start();
}
