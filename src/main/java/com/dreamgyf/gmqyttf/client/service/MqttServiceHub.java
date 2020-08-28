package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.callback.MqttConnectCallback;
import com.dreamgyf.gmqyttf.client.env.MqttPacketQueue;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttConnectTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttConnectPacket;

import java.util.concurrent.Executor;

public class MqttServiceHub {

    private final MqttVersion mVersion;

    private final MqttWritableSocket mSocket;

    private final Executor mThreadPool;

    private MqttPacketQueue mPacketQueue;

    private MqttReceiveService mReceiveService;

    private MqttConnectionService mConnectionService;

    public MqttServiceHub(MqttVersion version, MqttWritableSocket socket, Executor threadPool, MqttPacketQueue packetQueue) {
        mVersion = version;
        mSocket = socket;
        mThreadPool = threadPool;
        mPacketQueue = packetQueue;
    }

    public void init() {
        mReceiveService = new MqttReceiveService(mVersion, mSocket, mThreadPool, mPacketQueue.response);
        mConnectionService = new MqttConnectionService(mVersion, mSocket, mThreadPool,
                mPacketQueue.request.connect, mPacketQueue.response.connack);
    }

    public void start() {
        mReceiveService.start();
        mConnectionService.start();
    }

    public void stop() {
        mReceiveService.stop();
        mConnectionService.stop();
    }

    public void connect(MqttConnectPacket packet, MqttConnectCallback callback) {
        mConnectionService.connect(packet, callback);
    }
}
