package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttPacketSendListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.structure.BlockingObject;
import com.dreamgyf.gmqyttf.client.task.MqttPingReqTask;
import com.dreamgyf.gmqyttf.client.task.MqttPingRespTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttPingrespPacket;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttPingService extends MqttService {

    private final short mKeepAliveTime;

    private final BlockingObject<Long> mPingReqContainer;

    private final LinkedBlockingQueue<MqttPingrespPacket> mPingRespPacketQueue;

    private MqttPingReqTask mPingReqTask;

    private MqttPingRespTask mPingRespTask;

    public MqttPingService(MqttVersion version, MqttWritableSocket socket, Executor threadPool,
                           short keepAliveTime, LinkedBlockingQueue<MqttPingrespPacket> pingRespPacketQueue) {
        super(version, socket, threadPool);
        mKeepAliveTime = keepAliveTime;
        mPingReqContainer = new BlockingObject<>();
        mPingRespPacketQueue = pingRespPacketQueue;
    }

    @Override
    public void initTask() {
        mPingReqTask = new MqttPingReqTask(getVersion(), getSocket(), mKeepAliveTime, mPingReqContainer);
        mPingRespTask = new MqttPingRespTask(getVersion(), getSocket(), mKeepAliveTime, mPingReqContainer, mPingRespPacketQueue);
    }

    @Override
    public void start() {
        runOnNewThread(mPingReqTask);
        runOnNewThread(mPingRespTask);
    }

    @Override
    public void stop() {

    }

    @Override
    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        super.setOnMqttExceptionListener(listener);
        mPingReqTask.setOnMqttExceptionListener(listener);
        mPingRespTask.setOnMqttExceptionListener(listener);
    }

    @Override
    public void setOnPacketSendListener(OnMqttPacketSendListener listener) {
        super.setOnPacketSendListener(listener);
        mPingReqTask.setOnPacketSendListener(listener);
    }

    public void updateLastReqTime() {
        mPingReqTask.updateLastReqTime();
    }
}
