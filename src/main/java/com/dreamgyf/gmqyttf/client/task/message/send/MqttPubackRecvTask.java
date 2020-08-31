package com.dreamgyf.gmqyttf.client.task.message.send;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.packet.MqttPacketException;
import com.dreamgyf.gmqyttf.common.packet.MqttPubackPacket;
import com.dreamgyf.gmqyttf.common.utils.MqttRandomPacketIdGenerator;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttPubackRecvTask extends MqttTask {

    private final MqttRandomPacketIdGenerator mIdGenerator;

    private final LinkedBlockingQueue<MqttPubackPacket> mPubackQueue;

    public MqttPubackRecvTask(MqttVersion version, MqttWritableSocket socket,
                              MqttRandomPacketIdGenerator idGenerator,
                              LinkedBlockingQueue<MqttPubackPacket> pubackQueue) {
        super(version, socket);
        mIdGenerator = idGenerator;
        mPubackQueue = pubackQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttPubackPacket packet = mPubackQueue.take();
        short id = packet.getId();
        if (!mIdGenerator.remove(id)) {
            onMqttExceptionThrow(new MqttPacketException());
        }
    }
}
