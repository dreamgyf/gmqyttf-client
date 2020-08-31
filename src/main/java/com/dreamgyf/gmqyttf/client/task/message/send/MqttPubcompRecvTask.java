package com.dreamgyf.gmqyttf.client.task.message.send;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.packet.MqttPacketException;
import com.dreamgyf.gmqyttf.common.packet.MqttPubcompPacket;
import com.dreamgyf.gmqyttf.common.utils.MqttRandomPacketIdGenerator;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttPubcompRecvTask extends MqttTask {

    private final MqttRandomPacketIdGenerator mIdGenerator;

    private final LinkedBlockingQueue<MqttPubcompPacket> mPubcompQueue;

    public MqttPubcompRecvTask(MqttVersion version, MqttWritableSocket socket,
                               MqttRandomPacketIdGenerator idGenerator,
                               LinkedBlockingQueue<MqttPubcompPacket> pubcompQueue) {
        super(version, socket);
        mPubcompQueue = pubcompQueue;
        mIdGenerator = idGenerator;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttPubcompPacket packet = mPubcompQueue.take();
        short id = packet.getId();
        if (!mIdGenerator.remove(id)) {
            onMqttExceptionThrow(new MqttPacketException());
        }
    }
}
