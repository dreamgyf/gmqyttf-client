package com.dreamgyf.gmqyttf.client.task;

import com.dreamgyf.gmqyttf.client.socket.MqttSocket;
import com.dreamgyf.gmqyttf.common.exception.net.MqttSocketException;

public abstract class MqttTask implements Runnable {

    private final MqttSocket mSocket;

    public MqttTask(MqttSocket socket) {
        mSocket = socket;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Thread-" + this.getClass().getName());
    }

    public void writeSocket(byte[] packet) throws MqttSocketException {
        mSocket.write(packet);
    }

    public void readSocketOneBit() throws MqttSocketException {
        mSocket.readOneBit();
    }

    public void readSocketBit(int bitCount) throws MqttSocketException {
        mSocket.readBit(bitCount);
    }

    public abstract void stop();

}
