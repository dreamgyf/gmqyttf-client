package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttPacketSendListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttSubscribeFailureListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.subscription.MqttSubackTask;
import com.dreamgyf.gmqyttf.client.task.subscription.MqttSubscribeTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttSubackPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttSubscribePacket;
import com.dreamgyf.gmqyttf.common.packet.MqttUnsubackPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttUnsubscribePacket;
import com.dreamgyf.gmqyttf.common.params.MqttTopic;
import com.dreamgyf.gmqyttf.common.utils.MqttRandomPacketIdGenerator;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttSubscriptionService extends MqttService {

    private final MqttRandomPacketIdGenerator mIdGenerator;

    private final LinkedBlockingQueue<MqttSubscribePacket> mSubscribeQueue;

    private final LinkedBlockingQueue<MqttSubackPacket> mSubackQueue;

    private final ConcurrentHashMap<Short, List<MqttTopic>> mSubscribeMappingTable;

    private final LinkedBlockingQueue<MqttUnsubscribePacket> mUnsubscribeQueue;

    private final LinkedBlockingQueue<MqttUnsubackPacket> mUnsubackQueue;

    private MqttSubscribeTask mSubscribeTask;

    private MqttSubackTask mSubackTask;

    public MqttSubscriptionService(MqttVersion version, MqttWritableSocket socket, Executor threadPool,
                                   MqttRandomPacketIdGenerator idGenerator,
                                   LinkedBlockingQueue<MqttSubscribePacket> subscribeQueue,
                                   LinkedBlockingQueue<MqttSubackPacket> subackQueue,
                                   LinkedBlockingQueue<MqttUnsubscribePacket> unsubscribeQueue,
                                   LinkedBlockingQueue<MqttUnsubackPacket> unsubackQueue) {
        super(version, socket, threadPool);
        mIdGenerator = idGenerator;
        mSubscribeQueue = subscribeQueue;
        mSubackQueue = subackQueue;
        mSubscribeMappingTable = new ConcurrentHashMap<>();
        mUnsubscribeQueue = unsubscribeQueue;
        mUnsubackQueue = unsubackQueue;
    }

    @Override
    public void initTask() {
        mSubscribeTask = new MqttSubscribeTask(getVersion(), getSocket(), mSubscribeMappingTable, mSubscribeQueue);
        mSubackTask = new MqttSubackTask(getVersion(), getSocket(), mIdGenerator, mSubscribeMappingTable, mSubackQueue);
    }

    @Override
    public void start() {
        runOnNewThread(mSubscribeTask);
    }

    @Override
    public void stop() {

    }

    @Override
    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        super.setOnMqttExceptionListener(listener);
        mSubscribeTask.setOnMqttExceptionListener(listener);
    }

    @Override
    public void setOnPacketSendListener(OnMqttPacketSendListener listener) {
        super.setOnPacketSendListener(listener);
        mSubscribeTask.setOnPacketSendListener(listener);
    }

    public void setOnMqttSubscribeFailureListener(OnMqttSubscribeFailureListener listener) {
        mSubackTask.setOnMqttSubscribeFailureListener(listener);
    }

    public void subscribe(MqttSubscribePacket packet) {
        runOnNewThread(() -> {
            try {
                mSubscribeQueue.put(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void unsubscribe(MqttUnsubscribePacket packet) {
        runOnNewThread(() -> {
            try {
                mUnsubscribeQueue.put(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
