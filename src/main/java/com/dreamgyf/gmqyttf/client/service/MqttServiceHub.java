package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.callback.MqttConnectCallback;
import com.dreamgyf.gmqyttf.client.env.MqttPacketQueue;
import com.dreamgyf.gmqyttf.client.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttPacketSendListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttConnectPacket;

import java.util.concurrent.Executor;

public class MqttServiceHub {

    private final MqttVersion mVersion;

    private final short mKeepAliveTime;

    private final MqttWritableSocket mSocket;

    private final Executor mThreadPool;

    private final MqttPacketQueue mPacketQueue;

    private MqttReceiveService mReceiveService;

    private MqttConnectionService mConnectionService;

    private MqttPingService mPingService;

    public MqttServiceHub(MqttVersion version, short keepAliveTime,
                          MqttWritableSocket socket, Executor threadPool, MqttPacketQueue packetQueue) {
        mVersion = version;
        mKeepAliveTime = keepAliveTime;
        mSocket = socket;
        mThreadPool = threadPool;
        mPacketQueue = packetQueue;
    }

    public void init() {
        mReceiveService = new MqttReceiveService(mVersion, mSocket, mThreadPool, mPacketQueue.response);
        mConnectionService = new MqttConnectionService(mVersion, mSocket, mThreadPool,
                mPacketQueue.request.connect, mPacketQueue.response.connack);
        mPingService = new MqttPingService(mVersion, mSocket, mThreadPool, mKeepAliveTime, mPacketQueue.response.pingresp);

        mReceiveService.initTask();
        mConnectionService.initTask();
        mPingService.initTask();

        OnMqttPacketSendListener packetSendListener = () -> {
            mPingService.updateLastReqTime();
        };
        mConnectionService.setOnPacketSendListener(packetSendListener);
        mPingService.setOnPacketSendListener(packetSendListener);
    }

    public void start() {
        mReceiveService.start();
        mConnectionService.start();
        mPingService.start();
    }

    public void stop() {
        mReceiveService.stop();
        mConnectionService.stop();
        mPingService.stop();
    }

    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        mReceiveService.setOnMqttExceptionListener(listener);
        mConnectionService.setOnMqttExceptionListener(listener);
        mPingService.setOnMqttExceptionListener(listener);
    }

    public void connect(MqttConnectPacket packet, MqttConnectCallback callback) {
        mConnectionService.connect(packet, callback);
    }

    public void disconnect() {
        mConnectionService.disconnect();
    }
}
