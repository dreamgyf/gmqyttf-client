package com.dreamgyf.gmqyttf.client.task.subscription;

import com.dreamgyf.gmqyttf.client.listener.OnMqttSubscribeFailureListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttSubackReturnCode;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttSubackPacket;
import com.dreamgyf.gmqyttf.common.params.MqttTopic;
import com.dreamgyf.gmqyttf.common.throwable.exception.packet.InvalidPacketIdException;
import com.dreamgyf.gmqyttf.common.utils.MqttRandomPacketIdGenerator;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttSubackTask extends MqttTask {

    private final MqttRandomPacketIdGenerator mIdGenerator;

    private final ConcurrentHashMap<Short, List<MqttTopic>> mSubscribeMappingTable;

    private final LinkedBlockingQueue<MqttSubackPacket> mSubackQueue;

    private OnMqttSubscribeFailureListener mSubscribeFailureListener;

    public MqttSubackTask(MqttVersion version, MqttWritableSocket socket,
                          MqttRandomPacketIdGenerator idGenerator,
                          ConcurrentHashMap<Short, List<MqttTopic>> subscribeMappingTable,
                          LinkedBlockingQueue<MqttSubackPacket> subackQueue) {
        super(version, socket);
        mIdGenerator = idGenerator;
        mSubscribeMappingTable = subscribeMappingTable;
        mSubackQueue = subackQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttSubackPacket packet = mSubackQueue.take();
        if (!mIdGenerator.remove(packet.getId())) {
            onMqttExceptionThrow(new InvalidPacketIdException());
        } else {
            List<MqttTopic> topicList = mSubscribeMappingTable.remove(packet.getId());
            List<Byte> codeList = packet.getReturnCodeList();

            if (topicList == null || topicList.size() != codeList.size()) {
                onMqttExceptionThrow(new InvalidPacketIdException());
            } else {
                for (int i = 0; i < codeList.size(); i++) {
                    if (codeList.get(i) == MqttSubackReturnCode.V3_1_1.FAILURE) {
                        onSubscribeFailure(topicList.get(i));
                    }
                }
            }
        }
    }

    public void setOnMqttSubscribeFailureListener(OnMqttSubscribeFailureListener listener) {
        mSubscribeFailureListener = listener;
    }

    private void onSubscribeFailure(MqttTopic mqttTopic) {
        if (mSubscribeFailureListener != null) {
            mSubscribeFailureListener.onSubscribeFailure(mqttTopic);
        }
    }
}
