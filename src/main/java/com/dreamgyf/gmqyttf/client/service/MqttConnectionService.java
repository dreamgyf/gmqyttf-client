package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.callback.MqttConnectCallback;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.structure.BlockingObject;
import com.dreamgyf.gmqyttf.client.task.MqttConnackTask;
import com.dreamgyf.gmqyttf.client.task.MqttConnectTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.MqttException;
import com.dreamgyf.gmqyttf.common.packet.MqttConnackPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttConnectPacket;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttConnectionService extends MqttService {

    private final LinkedBlockingQueue<MqttConnectPacket> mConnectQueue;

    private final LinkedBlockingQueue<MqttConnackPacket> mConnackQueue;

    private final BlockingObject<MqttConnectCallback> mCallbackContainer;

    private final MqttConnectTask mConnectTask;

    private final MqttConnackTask mConnackTask;

    public MqttConnectionService(MqttVersion version, MqttWritableSocket socket, Executor threadPool,
                                 LinkedBlockingQueue<MqttConnectPacket> connectQueue,
                                 LinkedBlockingQueue<MqttConnackPacket> connackQueue) {
        super(version, socket, threadPool);
        mConnectQueue = connectQueue;
        mConnackQueue = connackQueue;
        mCallbackContainer = new BlockingObject<>();
        mConnectTask = new MqttConnectTask(version, socket, connectQueue, mCallbackContainer);
        mConnackTask = new MqttConnackTask(version, socket, connackQueue, mCallbackContainer);
    }

    @Override
    public void start() {
        runOnNewThread(mConnectTask);
        runOnNewThread(mConnackTask);
    }

    @Override
    public void stop() {
        mConnectTask.stop();
        mConnackTask.stop();
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
                if(callback != null) {
                    callback.onConnectFailure(new MqttException("Unknown Exception"));
                }
            }
        });
    }
}
