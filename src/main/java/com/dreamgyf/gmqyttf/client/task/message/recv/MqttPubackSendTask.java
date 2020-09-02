package com.dreamgyf.gmqyttf.client.task.message.recv;

import com.dreamgyf.gmqyttf.client.listener.OnMqttMessageReceivedListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.net.MqttSocketException;
import com.dreamgyf.gmqyttf.common.exception.packet.InvalidPacketIdException;
import com.dreamgyf.gmqyttf.common.packet.MqttPubackPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttPublishPacket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttPubackSendTask extends MqttTask {

    private final ConcurrentHashMap<Short, MqttPublishPacket> mMappingTable;

    private final LinkedBlockingQueue<MqttPubackPacket> mPubackQueue;

    private OnMqttMessageReceivedListener mMessageReceivedListener;

    public MqttPubackSendTask(MqttVersion version, MqttWritableSocket socket,
                              ConcurrentHashMap<Short, MqttPublishPacket> mappingTable,
                              LinkedBlockingQueue<MqttPubackPacket> pubackQueue) {
        super(version, socket);
        mMappingTable = mappingTable;
        mPubackQueue = pubackQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttPubackPacket pubackPacket = mPubackQueue.take();
        MqttPublishPacket publishPacket = mMappingTable.remove(pubackPacket.getId());
        if (publishPacket == null) {
            onMqttExceptionThrow(new InvalidPacketIdException());
        } else {
            try {
                writeSocket(pubackPacket.getPacket());
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
