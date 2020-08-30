package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.callback.MqttConnectCallback;
import com.dreamgyf.gmqyttf.client.exception.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.structure.BlockingObject;
import com.dreamgyf.gmqyttf.client.task.MqttConnackTask;
import com.dreamgyf.gmqyttf.client.task.MqttConnectTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.MqttException;
import com.dreamgyf.gmqyttf.common.exception.UnknownException;
import com.dreamgyf.gmqyttf.common.exception.net.MqttSocketException;
import com.dreamgyf.gmqyttf.common.packet.MqttConnackPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttConnectPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttDisconnectPacket;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttConnectionService extends MqttService {

    private final LinkedBlockingQueue<MqttConnectPacket> mConnectQueue;

    private final LinkedBlockingQueue<MqttConnackPacket> mConnackQueue;

    private final BlockingObject<MqttConnectCallback> mCallbackContainer;

    private MqttConnectTask mConnectTask;

    private MqttConnackTask mConnackTask;

    public MqttConnectionService(MqttVersion version, MqttWritableSocket socket, Executor threadPool,
                                 LinkedBlockingQueue<MqttConnectPacket> connectQueue,
                                 LinkedBlockingQueue<MqttConnackPacket> connackQueue) {
        super(version, socket, threadPool);
        mConnectQueue = connectQueue;
        mConnackQueue = connackQueue;
        mCallbackContainer = new BlockingObject<>();
    }

    @Override
    public void initTask() {
        mConnectTask = new MqttConnectTask(getVersion(), getSocket(), mConnectQueue, mCallbackContainer);
        mConnackTask = new MqttConnackTask(getVersion(), getSocket(), mConnackQueue, mCallbackContainer);
    }

    @Override
    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        super.setOnMqttExceptionListener(listener);
        mConnectTask.setOnMqttExceptionListener(listener);
        mConnackTask.setOnMqttExceptionListener(listener);
    }

    @Override
    public void start() {
        runOnNewThread(mConnectTask);
        runOnNewThread(mConnackTask);
    }

    @Override
    public void stop() {
    }

    public void connect(MqttConnectPacket packet, MqttConnectCallback callback) {
        runOnNewThread(() -> {
            try {
                mConnectQueue.put(packet);
                if (callback != null) {
                    mCallbackContainer.put(callback);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                onMqttExceptionThrow(new UnknownException());
                if (callback != null) {
                    callback.onConnectFailure(new UnknownException());
                }
            }
        });
    }

    public void disconnect() {
        MqttDisconnectPacket packet = new MqttDisconnectPacket.Builder().build(MqttVersion.V3_1_1);
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
