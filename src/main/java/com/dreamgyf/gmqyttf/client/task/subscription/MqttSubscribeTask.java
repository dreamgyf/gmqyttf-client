package com.dreamgyf.gmqyttf.client.task.subscription;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.net.MqttSocketException;
import com.dreamgyf.gmqyttf.common.packet.MqttSubscribePacket;
import com.dreamgyf.gmqyttf.common.params.MqttTopic;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttSubscribeTask extends MqttTask {

    private final ConcurrentHashMap<Short, List<MqttTopic>> mSubscribeMappingTable;

    private final LinkedBlockingQueue<MqttSubscribePacket> mSubscribeQueue;

    public MqttSubscribeTask(MqttVersion version, MqttWritableSocket socket,
                             ConcurrentHashMap<Short, List<MqttTopic>> subscribeMappingTable,
                             LinkedBlockingQueue<MqttSubscribePacket> subscribeQueue) {
        super(version, socket);
        mSubscribeMappingTable = subscribeMappingTable;
        mSubscribeQueue = subscribeQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttSubscribePacket packet = mSubscribeQueue.take();
        try {
            writeSocket(packet.getPacket());
            onMqttPacketSend();
            mSubscribeMappingTable.put(packet.getId(), packet.getTopicList());
        } catch (MqttSocketException e) {
            e.printStackTrace();
            onMqttExceptionThrow(e);
        }
    }
}
