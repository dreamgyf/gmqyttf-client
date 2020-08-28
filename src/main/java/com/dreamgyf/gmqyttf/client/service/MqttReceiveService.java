package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.env.MqttPacketQueue;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttPacketReceiveTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;

import java.util.concurrent.Executor;

public class MqttReceiveService extends MqttService {

    private final MqttPacketReceiveTask mReceiveTask;

    public MqttReceiveService(MqttVersion version, MqttWritableSocket socket, Executor threadPool, MqttPacketQueue.Response packetRespQueue) {
        super(version, socket, threadPool);
        mReceiveTask = new MqttPacketReceiveTask(version, socket, packetRespQueue);
    }

    @Override
    public void start() {
        runOnNewThread(mReceiveTask);
    }

    @Override
    public void stop() {
        mReceiveTask.stop();
    }
}
