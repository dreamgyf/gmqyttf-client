package com.dreamgyf.gmqyttf.client.task.message.send;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttPubrelPacket;
import com.dreamgyf.gmqyttf.common.throwable.exception.net.MqttSocketException;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttPubrelSendTask extends MqttTask {

    private final LinkedBlockingQueue<MqttPubrelPacket> mPubrelQueue;

    public MqttPubrelSendTask(MqttVersion version, MqttWritableSocket socket,
                              LinkedBlockingQueue<MqttPubrelPacket> pubrelQueue) {
        super(version, socket);
        mPubrelQueue = pubrelQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        try {
            MqttPubrelPacket pubrelPacket = mPubrelQueue.take();
            writeSocket(pubrelPacket.getPacket());
            onMqttPacketSend();
        } catch (MqttSocketException e) {
            e.printStackTrace();
            onMqttExceptionThrow(e);
        }
    }
}
