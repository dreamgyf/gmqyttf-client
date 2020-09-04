package com.dreamgyf.gmqyttf.client.task.subscription;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttUnsubscribePacket;
import com.dreamgyf.gmqyttf.common.throwable.exception.net.MqttSocketException;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttUnsubscribeTask extends MqttTask {

    private final LinkedBlockingQueue<MqttUnsubscribePacket> mUnsubscribeQueue;

    public MqttUnsubscribeTask(MqttVersion version, MqttWritableSocket socket,
                               LinkedBlockingQueue<MqttUnsubscribePacket> unsubscribeQueue) {
        super(version, socket);
        mUnsubscribeQueue = unsubscribeQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttUnsubscribePacket packet = mUnsubscribeQueue.take();
        try {
            writeSocket(packet.getPacket());
            onMqttPacketSend();
        } catch (MqttSocketException e) {
            e.printStackTrace();
            onMqttExceptionThrow(e);
        }
    }
}
