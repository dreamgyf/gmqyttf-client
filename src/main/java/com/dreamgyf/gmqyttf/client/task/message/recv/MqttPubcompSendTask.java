package com.dreamgyf.gmqyttf.client.task.message.recv;

import com.dreamgyf.gmqyttf.client.listener.OnMqttMessageReceivedListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttPubcompPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttPublishPacket;
import com.dreamgyf.gmqyttf.common.throwable.exception.net.MqttSocketException;
import com.dreamgyf.gmqyttf.common.throwable.exception.packet.InvalidPacketIdException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttPubcompSendTask extends MqttTask {

    private final ConcurrentHashMap<Short, MqttPublishPacket> mMappingTable;

    private final LinkedBlockingQueue<MqttPubcompPacket> mPubcompQueue;

    private OnMqttMessageReceivedListener mMessageReceivedListener;

    public MqttPubcompSendTask(MqttVersion version, MqttWritableSocket socket,
                               ConcurrentHashMap<Short, MqttPublishPacket> mappingTable,
                               LinkedBlockingQueue<MqttPubcompPacket> pubcompQueue) {
        super(version, socket);
        mMappingTable = mappingTable;
        mPubcompQueue = pubcompQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttPubcompPacket pubcompPacket = mPubcompQueue.take();
        MqttPublishPacket publishPacket = mMappingTable.remove(pubcompPacket.getId());
        if (publishPacket == null) {
            onMqttExceptionThrow(new InvalidPacketIdException());
        } else {
            try {
                writeSocket(pubcompPacket.getPacket());
                onMqttPacketSend();
                onMessageReceived(publishPacket.getTopic(), publishPacket.getMessage());
            } catch (MqttSocketException e) {
                e.printStackTrace();
                onMqttExceptionThrow(e);
            }
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
