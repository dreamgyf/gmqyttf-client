package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttMessageReceivedListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttPacketSendListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.message.recv.*;
import com.dreamgyf.gmqyttf.client.task.message.send.*;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.*;
import com.dreamgyf.gmqyttf.common.utils.MqttRandomPacketIdGenerator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class MqttMessageService extends MqttService {

    private final MqttRandomPacketIdGenerator mIdGenerator;

    private final ConcurrentHashMap<Short, MqttPublishPacket> mMappingTable;

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

    /**********************************************************
     * 接收消息Task组
     ***********************************************************/

    private MqttPublishRecvTask mPublishRecvTask;
    private MqttPubackSendTask mPubackSendTask;
    private MqttPubrecSendTask mPubrecSendTask;
    private MqttPubrelRecvTask mPubrelRecvTask;
    private MqttPubcompSendTask mPubcompSendTask;

    /**********************************************************
     * 接收消息Task组
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
        mMappingTable = new ConcurrentHashMap<>();
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
        initRecvTask();
    }

    private void initSendTask() {
        mPublishSendTask = new MqttPublishSendTask(getVersion(), getSocket(), mPublishSendQueue);
        mPubackRecvTask = new MqttPubackRecvTask(getVersion(), getSocket(), mIdGenerator, mPubackRecvQueue);
        mPubrecRecvTask = new MqttPubrecRecvTask(getVersion(), getSocket(), mIdGenerator, mPubrecRecvQueue, mPubrelSendQueue);
        mPubrelSendTask = new MqttPubrelSendTask(getVersion(), getSocket(), mPubrelSendQueue);
        mPubcompRecvTask = new MqttPubcompRecvTask(getVersion(), getSocket(), mIdGenerator, mPubcompRecvQueue);
    }

    private void initRecvTask() {
        mPublishRecvTask = new MqttPublishRecvTask(getVersion(), getSocket(), mMappingTable, mPublishRecvQueue, mPubackSendQueue, mPubrecSendQueue);
        mPubackSendTask = new MqttPubackSendTask(getVersion(), getSocket(), mMappingTable, mPubackSendQueue);
        mPubrecSendTask = new MqttPubrecSendTask(getVersion(), getSocket(), mPubrecSendQueue);
        mPubrelRecvTask = new MqttPubrelRecvTask(getVersion(), getSocket(), mMappingTable, mPubrelRecvQueue, mPubcompSendQueue);
        mPubcompSendTask = new MqttPubcompSendTask(getVersion(), getSocket(), mMappingTable, mPubcompSendQueue);
    }

    @Override
    public void start() {
        runOnNewThread(mPublishSendTask);
        runOnNewThread(mPubackRecvTask);
        runOnNewThread(mPubrecRecvTask);
        runOnNewThread(mPubrelSendTask);
        runOnNewThread(mPubcompRecvTask);

        runOnNewThread(mPublishRecvTask);
        runOnNewThread(mPubackSendTask);
        runOnNewThread(mPubrecSendTask);
        runOnNewThread(mPubrelRecvTask);
        runOnNewThread(mPubcompSendTask);
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

        mPublishRecvTask.setOnMqttExceptionListener(listener);
        mPubackSendTask.setOnMqttExceptionListener(listener);
        mPubrecSendTask.setOnMqttExceptionListener(listener);
        mPubrelRecvTask.setOnMqttExceptionListener(listener);
        mPubcompSendTask.setOnMqttExceptionListener(listener);
    }

    @Override
    public void setOnPacketSendListener(OnMqttPacketSendListener listener) {
        super.setOnPacketSendListener(listener);
        mPublishSendTask.setOnPacketSendListener(listener);
        mPubrelSendTask.setOnPacketSendListener(listener);

        mPubackSendTask.setOnPacketSendListener(listener);
        mPubrecSendTask.setOnPacketSendListener(listener);
        mPubcompSendTask.setOnPacketSendListener(listener);
    }

    public void setOnMqttMessageReceivedListener(OnMqttMessageReceivedListener listener) {
        mPublishRecvTask.setOnMqttMessageReceivedListener(listener);
        mPubackSendTask.setOnMqttMessageReceivedListener(listener);
        mPubcompSendTask.setOnMqttMessageReceivedListener(listener);
    }
}
