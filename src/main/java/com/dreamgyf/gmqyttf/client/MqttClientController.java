package com.dreamgyf.gmqyttf.client;

import com.dreamgyf.gmqyttf.client.callback.MqttConnectCallback;
import com.dreamgyf.gmqyttf.client.env.MqttPacketQueue;
import com.dreamgyf.gmqyttf.client.exception.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.client.service.MqttServiceHub;
import com.dreamgyf.gmqyttf.client.socket.MqttSocket;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.net.MqttNetworkException;
import com.dreamgyf.gmqyttf.common.packet.MqttConnectPacket;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class MqttClientController {

    private final MqttVersion mVersion;

    private final MqttSocket mSocket;

    private final ExecutorService mThreadPool;

    private MqttPacketQueue mPacketQueue;

    private MqttServiceHub mServiceHub;

    public MqttClientController(MqttVersion version) {
        mVersion = version;
        mSocket = new MqttSocket();
        mThreadPool = new ThreadPoolExecutor(10, 30,
                30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void init() {
        mPacketQueue = new MqttPacketQueue();
        mServiceHub = new MqttServiceHub(mVersion, mSocket, mThreadPool, mPacketQueue);
        mServiceHub.init();
    }

    public void start(String host, int port) throws MqttNetworkException {
        mSocket.connect(host, port);
        mServiceHub.start();
    }

    public void stop() {
        mServiceHub.stop();
        mThreadPool.shutdownNow();
        clear();
    }

    private void clear() {
        mPacketQueue.clear();
    }

    public void setOnMqttExceptionListener(OnMqttExceptionListener listener) {
        mServiceHub.setOnMqttExceptionListener(listener);
    }

    public void connect(MqttConnectPacket packet, MqttConnectCallback callback) {
        mServiceHub.connect(packet, callback);
    }

    public void disconnect() {
        mServiceHub.disconnect();
    }
}
