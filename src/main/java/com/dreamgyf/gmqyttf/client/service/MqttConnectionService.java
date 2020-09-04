package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.listener.OnMqttConnectSuccessListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttPacketSendListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.connection.MqttConnackTask;
import com.dreamgyf.gmqyttf.client.task.connection.MqttConnectTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttConnackPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttConnectPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttDisconnectPacket;
import com.dreamgyf.gmqyttf.common.throwable.exception.net.MqttSocketException;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttConnectionService extends MqttService {

    private final LinkedBlockingQueue<MqttConnectPacket> mConnectQueue;

    private final LinkedBlockingQueue<MqttConnackPacket> mConnackQueue;

    private MqttConnectTask mConnectTask;

    private MqttConnackTask mConnackTask;

    public MqttConnectionService(MqttVersion version, MqttWritableSocket socket, Executor threadPool,
                                 LinkedBlockingQueue<MqttConnectPacket> connectQueue,
                                 LinkedBlockingQueue<MqttConnackPacket> connackQueue) {
        super(version, socket, threadPool);
        mConnectQueue = connectQueue;
        mConnackQueue = connackQueue;
    }

    @Override
    public void initTask() {
        mConnectTask = new MqttConnectTask(getVersion(), getSocket(), mConnectQueue);
        mConnackTask = new MqttConnackTask(getVersion(), getSocket(), mConnackQueue);
    }

    public void setOnMqttConnectSuccessListener(OnMqttConnectSuccessListener listener) {
        mConnackTask.setOnMqttConnectSuccessListener(listener);
    }

    @Override
    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        super.setOnMqttExceptionListener(listener);
        mConnectTask.setOnMqttExceptionListener(listener);
        mConnackTask.setOnMqttExceptionListener(listener);
    }

    @Override
    public void setOnPacketSendListener(OnMqttPacketSendListener listener) {
        super.setOnPacketSendListener(listener);
        mConnectTask.setOnPacketSendListener(listener);
    }

    @Override
    public void start() {
        runOnNewThread(mConnectTask);
        runOnNewThread(mConnackTask);
    }

    @Override
    public void stop() {
    }

    public void connect(MqttConnectPacket packet) {
        runOnNewThread(() -> {
            try {
                mConnectQueue.put(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void disconnect(MqttDisconnectPacket packet) {
        runOnNewThread(() -> {
            try {
                getSocket().write(packet.getPacket());
            } catch (MqttSocketException e) {
                e.printStackTrace();
                onMqttExceptionThrow(e);
            }
        });
    }
}
