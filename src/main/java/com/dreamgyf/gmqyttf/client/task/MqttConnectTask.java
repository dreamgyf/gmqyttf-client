package com.dreamgyf.gmqyttf.client.task;

import com.dreamgyf.gmqyttf.client.callback.MqttConnectCallback;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.structure.BlockingObject;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.net.MqttSocketException;
import com.dreamgyf.gmqyttf.common.packet.MqttConnectPacket;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttConnectTask extends MqttTask {

    private final LinkedBlockingQueue<MqttConnectPacket> mConnectQueue;

    private final BlockingObject<MqttConnectCallback> mCallbackContainer;

    public MqttConnectTask(MqttVersion version, MqttWritableSocket socket,
                           LinkedBlockingQueue<MqttConnectPacket> connectQueue,
                           BlockingObject<MqttConnectCallback> callbackContainer) {
        super(version, socket);
        mConnectQueue = connectQueue;
        mCallbackContainer = callbackContainer;
    }

    @Override
    public void onLoop() throws InterruptedException {
        try {
            MqttConnectPacket packet = mConnectQueue.take();
            writeSocket(packet.getPacket());
            onMqttPacketSend();
        } catch (MqttSocketException e) {
            e.printStackTrace();
            onMqttExceptionThrow(e);
            MqttConnectCallback callback = mCallbackContainer.poll();
            if (callback != null) {
                callback.onConnectFailure(e);
            }
        }
    }

}
