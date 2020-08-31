package com.dreamgyf.gmqyttf.client.task.message.send;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttPubrecPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttPubrelPacket;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttPubrecRecvTask extends MqttTask {

    private final LinkedBlockingQueue<MqttPubrecPacket> mPubrecQueue;

    private final LinkedBlockingQueue<MqttPubrelPacket> mPubrelQueue;

    public MqttPubrecRecvTask(MqttVersion version, MqttWritableSocket socket,
                              LinkedBlockingQueue<MqttPubrecPacket> pubrecQueue,
                              LinkedBlockingQueue<MqttPubrelPacket> pubrelQueue) {
        super(version, socket);
        mPubrecQueue = pubrecQueue;
        mPubrelQueue = pubrelQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttPubrecPacket pubrecPacket = mPubrecQueue.take();
        MqttPubrelPacket pubrelPacket = new MqttPubrelPacket.Builder()
                .id(pubrecPacket.getId())
                .build(getVersion());
        mPubrelQueue.put(pubrelPacket);
    }
}
