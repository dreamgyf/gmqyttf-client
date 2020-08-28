package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.env.MqttPacketQueue;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;

import java.util.concurrent.Executor;

public class MqttServiceHub {

    private final MqttVersion mVersion;

    private final MqttWritableSocket mSocket;

    private final Executor mThreadPool;

    private MqttPacketQueue mPacketQueue;

    private MqttReceiveService mReceiveService;

    public MqttServiceHub(MqttVersion version, MqttWritableSocket socket, Executor threadPool, MqttPacketQueue packetQueue) {
        mVersion = version;
        mSocket = socket;
        mThreadPool = threadPool;
        mPacketQueue = packetQueue;
    }

    public void init() {
        mReceiveService = new MqttReceiveService(mVersion, mSocket, mThreadPool, mPacketQueue.response);
    }

    public void start() {
        mReceiveService.start();
    }

    public void stop() {
        mReceiveService.stop();
    }
}
