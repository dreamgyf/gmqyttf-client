package com.dreamgyf.gmqyttf.client.task.message.send;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttPubrecPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttPubrelPacket;
import com.dreamgyf.gmqyttf.common.throwable.exception.packet.InvalidPacketIdException;
import com.dreamgyf.gmqyttf.common.utils.MqttRandomPacketIdGenerator;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttPubrecRecvTask extends MqttTask {

    private final MqttRandomPacketIdGenerator mIdGenerator;

    private final LinkedBlockingQueue<MqttPubrecPacket> mPubrecQueue;

    private final LinkedBlockingQueue<MqttPubrelPacket> mPubrelQueue;

    public MqttPubrecRecvTask(MqttVersion version, MqttWritableSocket socket,
                              MqttRandomPacketIdGenerator idGenerator,
                              LinkedBlockingQueue<MqttPubrecPacket> pubrecQueue,
                              LinkedBlockingQueue<MqttPubrelPacket> pubrelQueue) {
        super(version, socket);
        mIdGenerator = idGenerator;
        mPubrecQueue = pubrecQueue;
        mPubrelQueue = pubrelQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttPubrecPacket pubrecPacket = mPubrecQueue.take();
        short id = pubrecPacket.getId();
        if (!mIdGenerator.contains(id)) {
            onMqttExceptionThrow(new InvalidPacketIdException());
        } else {
            mPubrelQueue.put(new MqttPubrelPacket.Builder()
                    .id(id)
                    .build(getVersion()));
        }
    }
}
