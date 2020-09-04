package com.dreamgyf.gmqyttf.client.task;

import com.dreamgyf.gmqyttf.client.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttPacketSendListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.throwable.exception.MqttException;
import com.dreamgyf.gmqyttf.common.throwable.exception.net.MqttSocketException;

public abstract class MqttTask implements Runnable {

    private final MqttVersion mVersion;

    private final MqttWritableSocket mSocket;

    private OnMqttExceptionListener mExceptionListener;

    private OnMqttPacketSendListener mPacketSendListener;

    public MqttTask(MqttVersion version, MqttWritableSocket socket) {
        mVersion = version;
        mSocket = socket;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Thread-" + this.getClass().getSimpleName());
        while (!Thread.currentThread().isInterrupted()) {
            try {
                onLoop();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public abstract void onLoop() throws InterruptedException;

    public MqttVersion getVersion() {
        return mVersion;
    }

    public void writeSocket(byte[] packet) throws MqttSocketException {
        mSocket.write(packet);
    }

    public byte readSocketOneBit() throws MqttSocketException {
        return mSocket.readOneBit();
    }

    public byte[] readSocketBit(int bitCount) throws MqttSocketException {
        return mSocket.readBit(bitCount);
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

}
