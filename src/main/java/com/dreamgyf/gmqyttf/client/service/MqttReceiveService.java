package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.env.MqttPacketQueue;
import com.dreamgyf.gmqyttf.client.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttReceiveTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;

import java.util.concurrent.Executor;

public class MqttReceiveService extends MqttService {

    private final MqttPacketQueue.Response mPacketRespQueue;

    private MqttReceiveTask mReceiveTask;

    public MqttReceiveService(MqttVersion version, MqttWritableSocket socket, Executor threadPool, MqttPacketQueue.Response packetRespQueue) {
        super(version, socket, threadPool);
        mPacketRespQueue = packetRespQueue;
    }

    @Override
    public void initTask() {
        mReceiveTask = new MqttReceiveTask(getVersion(), getSocket(), mPacketRespQueue);
    }

    @Override
    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        super.setOnMqttExceptionListener(listener);
        mReceiveTask.setOnMqttExceptionListener(listener);
    }

    @Override
    public void start() {
        runOnNewThread(mReceiveTask);
    }

}
