package com.dreamgyf.gmqyttf.client;

import com.dreamgyf.gmqyttf.client.env.MqttPacketQueue;
import com.dreamgyf.gmqyttf.client.service.MqttServiceHub;
import com.dreamgyf.gmqyttf.client.socket.MqttSocket;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.net.MqttNetworkException;

import java.util.concurrent.*;

public class MqttClientController {

    private MqttVersion mVersion;

    private MqttSocket mSocket;

    private ExecutorService mThreadPool;

    private MqttPacketQueue mPacketQueue;

    private MqttServiceHub mServiceHub;

    public MqttClientController() {
        mThreadPool = new ThreadPoolExecutor(10, 30,
                30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
        mPacketQueue = new MqttPacketQueue();
        mServiceHub = new MqttServiceHub(mVersion, mSocket, mThreadPool);
    }

    public void start(String host, int port) throws MqttNetworkException {
        mSocket = new MqttSocket();
        mSocket.connect(host, port);

        mServiceHub.init();
        mServiceHub.start();
    }

    public void stop() {
        mServiceHub.stop();
    }

    public void clear() {
        mPacketQueue.clear();
    }



}
