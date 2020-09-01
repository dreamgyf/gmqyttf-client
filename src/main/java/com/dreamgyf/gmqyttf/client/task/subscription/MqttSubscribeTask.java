package com.dreamgyf.gmqyttf.client.task.subscription;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.net.MqttSocketException;
import com.dreamgyf.gmqyttf.common.packet.MqttSubscribePacket;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttSubscribeTask extends MqttTask {

    private final LinkedBlockingQueue<MqttSubscribePacket> mSubscribeQueue;

    public MqttSubscribeTask(MqttVersion version, MqttWritableSocket socket,
                             LinkedBlockingQueue<MqttSubscribePacket> subscribeQueue) {
        super(version, socket);
        mSubscribeQueue = subscribeQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttSubscribePacket packet = mSubscribeQueue.take();
        try {
            writeSocket(packet.getPacket());
            onMqttPacketSend();
        } catch (MqttSocketException e) {
            e.printStackTrace();
            onMqttExceptionThrow(e);
        }
    }
}
