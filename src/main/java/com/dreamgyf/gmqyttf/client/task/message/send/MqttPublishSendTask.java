package com.dreamgyf.gmqyttf.client.task.message.send;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttPublishPacket;
import com.dreamgyf.gmqyttf.common.throwable.exception.net.MqttSocketException;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttPublishSendTask extends MqttTask {

    private final LinkedBlockingQueue<MqttPublishPacket> mPublishQueue;

    public MqttPublishSendTask(MqttVersion version, MqttWritableSocket socket,
                               LinkedBlockingQueue<MqttPublishPacket> publishQueue) {
        super(version, socket);
        mPublishQueue = publishQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        try {
            MqttPublishPacket packet = mPublishQueue.take();
            writeSocket(packet.getPacket());
            onMqttPacketSend();
        } catch (MqttSocketException e) {
            e.printStackTrace();
            onMqttExceptionThrow(e);
        }
    }
}
