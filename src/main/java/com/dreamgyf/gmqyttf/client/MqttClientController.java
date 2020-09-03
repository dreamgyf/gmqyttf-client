package com.dreamgyf.gmqyttf.client;

import com.dreamgyf.gmqyttf.client.env.MqttPacketQueue;
import com.dreamgyf.gmqyttf.client.listener.*;
import com.dreamgyf.gmqyttf.client.service.*;
import com.dreamgyf.gmqyttf.client.socket.MqttSocket;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.net.MqttNetworkException;
import com.dreamgyf.gmqyttf.common.packet.*;
import com.dreamgyf.gmqyttf.common.utils.MqttRandomPacketIdGenerator;

import java.util.concurrent.*;

public class MqttClientController {

    private final MqttVersion mVersion;

    private final short mKeepAliveTime;

    private final MqttSocket mSocket;

    private final ExecutorService mThreadPool;

    private volatile MqttPacketQueue mPacketQueue;

    private final MqttRandomPacketIdGenerator mIdGenerator;

    /**********************************************************
     * Service组
     ***********************************************************/
    private MqttReceiveService mReceiveService;
    private MqttConnectionService mConnectionService;
    private MqttPingService mPingService;
    private MqttMessageService mMessageService;
    private MqttSubscriptionService mSubscriptionService;

    /**********************************************************
     * Service组
     ***********************************************************/

    public MqttClientController(MqttVersion version, short keepAliveTime) {
        mVersion = version;
        mKeepAliveTime = keepAliveTime;
        mIdGenerator = MqttRandomPacketIdGenerator.create();
        mSocket = new MqttSocket();
        mThreadPool = new ThreadPoolExecutor(20, 50,
                30, TimeUnit.SECONDS, new SynchronousQueue<>(true),
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
        mSubscriptionService = new MqttSubscriptionService(mVersion, mSocket, mThreadPool, mIdGenerator,
                mPacketQueue.request.subscribe, mPacketQueue.response.suback,
                mPacketQueue.request.unsubscribe, mPacketQueue.response.unsuback);

        mReceiveService.initTask();
        mConnectionService.initTask();
        mPingService.initTask();
        mMessageService.initTask();
        mSubscriptionService.initTask();

        OnMqttPacketSendListener packetSendListener = () -> {
            mPingService.updateLastReqTime();
        };
        mConnectionService.setOnPacketSendListener(packetSendListener);
        mPingService.setOnPacketSendListener(packetSendListener);
        mMessageService.setOnPacketSendListener(packetSendListener);
        mSubscriptionService.setOnPacketSendListener(packetSendListener);
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
        mSubscriptionService.start();
    }

    public void onPacketEventProduce(MqttPacket.Builder builder) {
        if(builder instanceof MqttConnectPacket.Builder) {
            MqttConnectPacket packet = ((MqttConnectPacket.Builder) builder).build(mVersion);
            mConnectionService.connect(packet);
        } else if(builder instanceof MqttPublishPacket.Builder) {
            if(((MqttPublishPacket.Builder) builder).getQoS() > 0) {
                ((MqttPublishPacket.Builder) builder).id(mIdGenerator.next());
            }
            MqttPublishPacket packet = ((MqttPublishPacket.Builder) builder).build(mVersion);
            mMessageService.publish(packet);
        } else if(builder instanceof MqttSubscribePacket.Builder) {
            MqttSubscribePacket packet = ((MqttSubscribePacket.Builder) builder).id(mIdGenerator.next()).build(mVersion);
            mSubscriptionService.subscribe(packet);
        } else if(builder instanceof MqttUnsubscribePacket.Builder) {
            MqttUnsubscribePacket packet = ((MqttUnsubscribePacket.Builder) builder).id(mIdGenerator.next()).build(mVersion);
            mSubscriptionService.unsubscribe(packet);
        } else if(builder instanceof MqttDisconnectPacket.Builder) {
            MqttDisconnectPacket packet = ((MqttDisconnectPacket.Builder) builder).build(mVersion);
            mConnectionService.disconnect(packet);
        }
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
        mSubscriptionService.stop();
    }

    private void clear() {
        mPacketQueue.clear();
    }

    public void setOnMqttConnectSuccessListener(OnMqttConnectSuccessListener listener) {
        mConnectionService.setOnMqttConnectSuccessListener(listener);
    }

    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        mReceiveService.setOnMqttExceptionListener(listener);
        mConnectionService.setOnMqttExceptionListener(listener);
        mPingService.setOnMqttExceptionListener(listener);
        mMessageService.setOnMqttExceptionListener(listener);
        mSubscriptionService.setOnMqttExceptionListener(listener);
    }

    public void setOnMqttSubscribeFailureListener(OnMqttSubscribeFailureListener listener) {
        mSubscriptionService.setOnMqttSubscribeFailureListener(listener);
    }

    public void setOnMqttMessageReceivedListener(OnMqttMessageReceivedListener listener) {
        mMessageService.setOnMqttMessageReceivedListener(listener);
    }
}
