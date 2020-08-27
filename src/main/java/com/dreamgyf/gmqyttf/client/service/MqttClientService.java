package com.dreamgyf.gmqyttf.client.service;

import com.dreamgyf.gmqyttf.client.env.MqttPacketQueue;
import com.dreamgyf.gmqyttf.client.socket.MqttSocket;
import com.dreamgyf.gmqyttf.client.task.MqttPacketReceiveTask;
import com.dreamgyf.gmqyttf.common.exception.net.MqttNetworkException;

import java.util.concurrent.*;

public class MqttClientService {

    private ExecutorService mThreadPool;

    private MqttPacketQueue mPacketQueue;

    private MqttPacketReceiveTask mReceiveService;

    private MqttSocket mSocket;

    private boolean isRunning;

    public MqttClientService() {
        mThreadPool = new ThreadPoolExecutor(10, 30,
                30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
        mPacketQueue = new MqttPacketQueue();
    }

    public void start(String host, int port) throws MqttNetworkException {
        mSocket = new MqttSocket();
        mSocket.connect(host, port);

    }

    public void stop() {

    }

    public void clear() {

    }

}
