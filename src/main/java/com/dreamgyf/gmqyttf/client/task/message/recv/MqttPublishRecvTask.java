package com.dreamgyf.gmqyttf.client.task.message.recv;

import com.dreamgyf.gmqyttf.client.listener.OnMqttMessageReceivedListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttPubackPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttPublishPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttPubrecPacket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttPublishRecvTask extends MqttTask {

    private final ConcurrentHashMap<Short, MqttPublishPacket> mMappingTable;

    private final LinkedBlockingQueue<MqttPublishPacket> mPublishQueue;

    private final LinkedBlockingQueue<MqttPubackPacket> mPubackQueue;

    private final LinkedBlockingQueue<MqttPubrecPacket> mPubrecQueue;

    private OnMqttMessageReceivedListener mMessageReceivedListener;

    public MqttPublishRecvTask(MqttVersion version, MqttWritableSocket socket,
                               ConcurrentHashMap<Short, MqttPublishPacket> mappingTable,
                               LinkedBlockingQueue<MqttPublishPacket> publishQueue,
                               LinkedBlockingQueue<MqttPubackPacket> pubackQueue,
                               LinkedBlockingQueue<MqttPubrecPacket> pubrecQueue) {
        super(version, socket);
        mMappingTable = mappingTable;
        mPublishQueue = publishQueue;
        mPubackQueue = pubackQueue;
        mPubrecQueue = pubrecQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttPublishPacket publishPacket = mPublishQueue.take();
        int QoS = publishPacket.getQoS();
        if (QoS == 0) {
            onMessageReceived(publishPacket.getTopic(), publishPacket.getMessage());
        } else if (QoS == 1) {
            short id = publishPacket.getId();
            mMappingTable.put(id, publishPacket);
            mPubackQueue.put(new MqttPubackPacket.Builder()
                    .id(publishPacket.getId())
                    .build(getVersion()));
        } else if (QoS == 2) {
            short id = publishPacket.getId();
            mMappingTable.put(id, publishPacket);
            mPubrecQueue.put(new MqttPubrecPacket.Builder()
                    .id(publishPacket.getId())
                    .build(getVersion()));
        }
    }

    public void setOnMqttMessageReceivedListener(OnMqttMessageReceivedListener listener) {
        mMessageReceivedListener = listener;
    }

    private void onMessageReceived(String topic, String message) {
        if (mMessageReceivedListener != null) {
            mMessageReceivedListener.onMessageReceived(topic, message);
        }
    }
}
