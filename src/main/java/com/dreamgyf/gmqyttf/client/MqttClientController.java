package com.dreamgyf.gmqyttf.client;

import com.dreamgyf.gmqyttf.client.callback.MqttConnectCallback;
import com.dreamgyf.gmqyttf.client.env.MqttPacketQueue;
import com.dreamgyf.gmqyttf.client.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttMessageReceivedListener;
import com.dreamgyf.gmqyttf.client.listener.OnMqttPacketSendListener;
import com.dreamgyf.gmqyttf.client.service.*;
import com.dreamgyf.gmqyttf.client.socket.MqttSocket;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.net.MqttNetworkException;
import com.dreamgyf.gmqyttf.common.packet.MqttConnectPacket;
import com.dreamgyf.gmqyttf.common.packet.MqttPublishPacket;
import com.dreamgyf.gmqyttf.common.utils.MqttRandomPacketIdGenerator;

import java.util.concurrent.*;

public class MqttClientController {

    private final MqttVersion mVersion;

    private final short mKeepAliveTime;

    private final MqttSocket mSocket;

    private final ExecutorService mThreadPool;

    private volatile MqttPacketQueue mPacketQueue;

    private MqttRandomPacketIdGenerator mIdGenerator;

    /**********************************************************
     * Service组
     ***********************************************************/
    private MqttReceiveService mReceiveService;
    private MqttConnectionService mConnectionService;
    private MqttPingService mPingService;
    private MqttMessageService mMessageService;

    /**********************************************************
     * Service组
     ***********************************************************/

    public MqttClientController(MqttVersion version, short keepAliveTime, MqttRandomPacketIdGenerator idGenerator) {
        mVersion = version;
        mKeepAliveTime = keepAliveTime;
        mIdGenerator = idGenerator;
        mSocket = new MqttSocket();
        mThreadPool = new ThreadPoolExecutor(10, 30,
                30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void init() {
        mPacketQueue = new MqttPacketQueue();
        initService();
    }

    private void initService() {
        mReceiveService = new MqttReceiveService(mVersion, mSocket, mThreadPool, mPacketQueue.response);
        mConnectionService = new MqttConnectionService(mVersion, mSocket, mThreadPool,
                mPacketQueue.request.connect, mPacketQueue.response.connack);
        mPingService = new MqttPingService(mVersion, mSocket, mThreadPool, mKeepAliveTime, mPacketQueue.response.pingresp);
        mMessageService = new MqttMessageService(mVersion, mSocket, mThreadPool, mIdGenerator,
                mPacketQueue.request.publish, mPacketQueue.request.puback, mPacketQueue.request.pubrec,
                mPacketQueue.request.pubrel, mPacketQueue.request.pubcomp, mPacketQueue.response.publish,
                mPacketQueue.response.puback, mPacketQueue.response.pubrec, mPacketQueue.response.pubrel,
                mPacketQueue.response.pubcomp);

        mReceiveService.initTask();
        mConnectionService.initTask();
        mPingService.initTask();
        mMessageService.initTask();

        OnMqttPacketSendListener packetSendListener = () -> {
            mPingService.updateLastReqTime();
        };
        mConnectionService.setOnPacketSendListener(packetSendListener);
        mPingService.setOnPacketSendListener(packetSendListener);
        mMessageService.setOnPacketSendListener(packetSendListener);
    }

    public void start(String host, int port) throws MqttNetworkException {
        mSocket.connect(host, port);
        startService();
    }

    private void startService() {
        mReceiveService.start();
        mConnectionService.start();
        mPingService.start();
        mMessageService.start();
    }

    public void stop() {
        stopService();
        mThreadPool.shutdownNow();
        clear();
    }

    private void stopService() {
        mReceiveService.stop();
        mConnectionService.stop();
        mPingService.stop();
        mMessageService.stop();
    }

    private void clear() {
        mPacketQueue.clear();
    }

    public void connect(MqttConnectPacket packet, MqttConnectCallback callback) {
        mConnectionService.connect(packet, callback);
    }

    public void publish(MqttPublishPacket packet) {
        mMessageService.publish(packet);
    }

    public void disconnect() {
        mConnectionService.disconnect();
    }

    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        mReceiveService.setOnMqttExceptionListener(listener);
        mConnectionService.setOnMqttExceptionListener(listener);
        mPingService.setOnMqttExceptionListener(listener);
        mMessageService.setOnMqttExceptionListener(listener);
    }

    public void setOnMqttMessageReceivedListener(OnMqttMessageReceivedListener listener) {
        mMessageService.setOnMqttMessageReceivedListener(listener);
    }
}
