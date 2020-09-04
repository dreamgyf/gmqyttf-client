package com.dreamgyf.gmqyttf.client.task.subscription;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttUnsubackPacket;
import com.dreamgyf.gmqyttf.common.throwable.exception.packet.InvalidPacketIdException;
import com.dreamgyf.gmqyttf.common.utils.MqttRandomPacketIdGenerator;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttUnsubackTask extends MqttTask {

    private final MqttRandomPacketIdGenerator mIdGenerator;

    private final LinkedBlockingQueue<MqttUnsubackPacket> mUnsubackQueue;

    public MqttUnsubackTask(MqttVersion version, MqttWritableSocket socket,
                            MqttRandomPacketIdGenerator idGenerator,
                            LinkedBlockingQueue<MqttUnsubackPacket> unsubackQueue) {
        super(version, socket);
        mIdGenerator = idGenerator;
        mUnsubackQueue = unsubackQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttUnsubackPacket packet = mUnsubackQueue.take();
        if (!mIdGenerator.remove(packet.getId())) {
            onMqttExceptionThrow(new InvalidPacketIdException());
        }
    }
}
