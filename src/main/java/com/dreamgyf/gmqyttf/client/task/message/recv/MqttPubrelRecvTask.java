package com.dreamgyf.gmqyttf.client.task.message.recv;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.packet.MqttPacketException;
import com.dreamgyf.gmqyttf.common.packet.MqttPubcompPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttPublishPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttPubrelPacket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttPubrelRecvTask extends MqttTask {

    private final ConcurrentHashMap<Short, MqttPublishPacket> mMappingTable;

    private final LinkedBlockingQueue<MqttPubrelPacket> mPubrelQueue;

    private final LinkedBlockingQueue<MqttPubcompPacket> mPubcompQueue;

    public MqttPubrelRecvTask(MqttVersion version, MqttWritableSocket socket,
                              ConcurrentHashMap<Short, MqttPublishPacket> mappingTable,
                              LinkedBlockingQueue<MqttPubrelPacket> pubrelQueue,
                              LinkedBlockingQueue<MqttPubcompPacket> pubcompQueue) {
        super(version, socket);
        mMappingTable = mappingTable;
        mPubrelQueue = pubrelQueue;
        mPubcompQueue = pubcompQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttPubrelPacket packet = mPubrelQueue.take();
        short id = packet.getId();
        if (!mMappingTable.containsKey(id)) {
            onMqttExceptionThrow(new MqttPacketException());
        } else {
            mPubcompQueue.put(new MqttPubcompPacket.Builder()
                    .id(id)
                    .build(getVersion()));
        }
    }
}
