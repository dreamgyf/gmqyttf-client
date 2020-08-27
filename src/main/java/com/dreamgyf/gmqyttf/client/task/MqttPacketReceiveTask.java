package com.dreamgyf.gmqyttf.client.task;

import com.dreamgyf.gmqyttf.client.env.MqttPacketQueue;
import com.dreamgyf.gmqyttf.client.socket.MqttSocket;

public class MqttPacketReceiveTask extends MqttTask {

    private MqttPacketQueue mPacketQueue;

    public MqttPacketReceiveTask(MqttSocket socket) {
        super(socket);
    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    public void stop() {

    }
}
