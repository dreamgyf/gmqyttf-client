package com.dreamgyf.gmqyttf.client.task.message.recv;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttPubrecPacket;
import com.dreamgyf.gmqyttf.common.throwable.exception.net.MqttSocketException;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttPubrecSendTask extends MqttTask {

    private final LinkedBlockingQueue<MqttPubrecPacket> mPubrecQueue;

    public MqttPubrecSendTask(MqttVersion version, MqttWritableSocket socket,
                              LinkedBlockingQueue<MqttPubrecPacket> pubrecQueue) {
        super(version, socket);
        mPubrecQueue = pubrecQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttPubrecPacket packet = mPubrecQueue.take();
        try {
            writeSocket(packet.getPacket());
            onMqttPacketSend();
        } catch (MqttSocketException e) {
            e.printStackTrace();
            onMqttExceptionThrow(e);
        }
    }
}
