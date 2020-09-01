package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttPacketSendListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttSubscribeFailureListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.subscription.MqttSubscribeTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttSubackPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttSubscribePacket;
import com.dreamgyf.gmqyttf.common.packet.MqttUnsubackPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttUnsubscribePacket;
import com.dreamgyf.gmqyttf.common.utils.MqttRandomPacketIdGenerator;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttSubscriptionService extends MqttService {

    private final MqttRandomPacketIdGenerator mIdGenerator;

    public final LinkedBlockingQueue<MqttSubscribePacket> mSubscribeQueue;

    public final LinkedBlockingQueue<MqttSubackPacket> mSubackQueue;

    public final LinkedBlockingQueue<MqttUnsubscribePacket> mUnsubscribeQueue;

    public final LinkedBlockingQueue<MqttUnsubackPacket> mUnsubackQueue;

    public MqttSubscribeTask mSubscribeTask;

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
        mUnsubscribeQueue = unsubscribeQueue;
        mUnsubackQueue = unsubackQueue;
    }

    @Override
    public void initTask() {
        mSubscribeTask = new MqttSubscribeTask(getVersion(), getSocket(), mSubscribeQueue);
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
