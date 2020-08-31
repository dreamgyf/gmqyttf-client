package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttMessageReceivedListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttPacketSendListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.message.send.*;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.*;
import com.dreamgyf.gmqyttf.common.utils.MqttRandomPacketIdGenerator;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttMessageService extends MqttService {

    private final MqttRandomPacketIdGenerator mIdGenerator;

    private final LinkedBlockingQueue<MqttPublishPacket> mPublishSendQueue;

    private final LinkedBlockingQueue<MqttPubackPacket> mPubackSendQueue;

    private final LinkedBlockingQueue<MqttPubrecPacket> mPubrecSendQueue;

    private final LinkedBlockingQueue<MqttPubrelPacket> mPubrelSendQueue;

    private final LinkedBlockingQueue<MqttPubcompPacket> mPubcompSendQueue;

    private final LinkedBlockingQueue<MqttPublishPacket> mPublishRecvQueue;

    private final LinkedBlockingQueue<MqttPubackPacket> mPubackRecvQueue;

    private final LinkedBlockingQueue<MqttPubrecPacket> mPubrecRecvQueue;

    private final LinkedBlockingQueue<MqttPubrelPacket> mPubrelRecvQueue;

    private final LinkedBlockingQueue<MqttPubcompPacket> mPubcompRecvQueue;

    /**********************************************************
     * 发送消息Task组
     ***********************************************************/
    private MqttPublishSendTask mPublishSendTask;
    private MqttPubackRecvTask mPubackRecvTask;
    private MqttPubrecRecvTask mPubrecRecvTask;
    private MqttPubrelSendTask mPubrelSendTask;
    private MqttPubcompRecvTask mPubcompRecvTask;

    /**********************************************************
     * 发送消息Task组
     ***********************************************************/

    public MqttMessageService(MqttVersion version, MqttWritableSocket socket, Executor threadPool,
                              MqttRandomPacketIdGenerator idGenerator,
                              LinkedBlockingQueue<MqttPublishPacket> publishSendQueue,
                              LinkedBlockingQueue<MqttPubackPacket> pubackSendQueue,
                              LinkedBlockingQueue<MqttPubrecPacket> pubrecSendQueue,
                              LinkedBlockingQueue<MqttPubrelPacket> pubrelSendQueue,
                              LinkedBlockingQueue<MqttPubcompPacket> pubcompSendQueue,
                              LinkedBlockingQueue<MqttPublishPacket> publishRecvQueue,
                              LinkedBlockingQueue<MqttPubackPacket> pubackRecvQueue,
                              LinkedBlockingQueue<MqttPubrecPacket> pubrecRecvQueue,
                              LinkedBlockingQueue<MqttPubrelPacket> pubrelRecvQueue,
                              LinkedBlockingQueue<MqttPubcompPacket> pubcompRecvQueue) {
        super(version, socket, threadPool);
        mIdGenerator = idGenerator;
        mPublishSendQueue = publishSendQueue;
        mPubackSendQueue = pubackSendQueue;
        mPubrecSendQueue = pubrecSendQueue;
        mPubrelSendQueue = pubrelSendQueue;
        mPubcompSendQueue = pubcompSendQueue;
        mPublishRecvQueue = publishRecvQueue;
        mPubackRecvQueue = pubackRecvQueue;
        mPubrecRecvQueue = pubrecRecvQueue;
        mPubrelRecvQueue = pubrelRecvQueue;
        mPubcompRecvQueue = pubcompRecvQueue;
    }

    @Override
    public void initTask() {
        initSendTask();
    }

    private void initSendTask() {
        mPublishSendTask = new MqttPublishSendTask(getVersion(), getSocket(), mPublishSendQueue);
        mPubackRecvTask = new MqttPubackRecvTask(getVersion(), getSocket(), mIdGenerator, mPubackRecvQueue);
        mPubrecRecvTask = new MqttPubrecRecvTask(getVersion(), getSocket(), mPubrecRecvQueue, mPubrelSendQueue);
        mPubrelSendTask = new MqttPubrelSendTask(getVersion(), getSocket(), mPubrelSendQueue);
        mPubcompRecvTask = new MqttPubcompRecvTask(getVersion(), getSocket(), mIdGenerator, mPubcompRecvQueue);
    }

    @Override
    public void start() {
        runOnNewThread(mPublishSendTask);
        runOnNewThread(mPubackRecvTask);
        runOnNewThread(mPubrecRecvTask);
        runOnNewThread(mPubrelSendTask);
        runOnNewThread(mPubcompRecvTask);
    }

    @Override
    public void stop() {

    }

    public void publish(MqttPublishPacket packet) {
        runOnNewThread(() -> {
            try {
                mPublishSendQueue.put(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        super.setOnMqttExceptionListener(listener);
        mPublishSendTask.setOnMqttExceptionListener(listener);
        mPubackRecvTask.setOnMqttExceptionListener(listener);
        mPubrecRecvTask.setOnMqttExceptionListener(listener);
        mPubrelSendTask.setOnMqttExceptionListener(listener);
        mPubcompRecvTask.setOnMqttExceptionListener(listener);
    }

    @Override
    public void setOnPacketSendListener(OnMqttPacketSendListener listener) {
        super.setOnPacketSendListener(listener);
        mPublishSendTask.setOnPacketSendListener(listener);
        mPubrelSendTask.setOnPacketSendListener(listener);
    }

    public void setOnMqttMessageReceivedListener(OnMqttMessageReceivedListener listener) {

    }
}
