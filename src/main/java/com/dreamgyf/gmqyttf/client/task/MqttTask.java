package com.dreamgyf.gmqyttf.client.task;

import com.dreamgyf.gmqyttf.client.exception.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.MqttException;
import com.dreamgyf.gmqyttf.common.exception.net.MqttSocketException;

public abstract class MqttTask implements Runnable {

    private final MqttVersion mVersion;

    private final MqttWritableSocket mSocket;

    private OnMqttExceptionListener mListener;

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

    protected OnMqttExceptionListener getOnMqttExceptionListener() {
        return mListener;
    }

    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        mListener = listener;
    }

    protected void onMqttExceptionThrow(MqttException e) {
        if (mListener != null) {
            mListener.onMqttExceptionThrow(e);
        }
    }

}
