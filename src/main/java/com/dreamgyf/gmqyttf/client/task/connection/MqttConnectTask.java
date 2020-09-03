package com.dreamgyf.gmqyttf.client.task.connection;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.net.MqttSocketException;
import com.dreamgyf.gmqyttf.common.packet.MqttConnectPacket;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttConnectTask extends MqttTask {

    private final LinkedBlockingQueue<MqttConnectPacket> mConnectQueue;

    public MqttConnectTask(MqttVersion version, MqttWritableSocket socket,
                           LinkedBlockingQueue<MqttConnectPacket> connectQueue) {
        super(version, socket);
        mConnectQueue = connectQueue;
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
        }
    }

}
